// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.sdk.iot.device.transport.mqtt;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ConnectionStatusException;
import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.IotHubConnectionStatusException;
import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;
import com.microsoft.azure.sdk.iot.device.transport.*;
import com.microsoft.azure.sdk.iot.device.transport.mqtt.exceptions.*;
import org.eclipse.paho.client.mqttv3.MqttException;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import static org.eclipse.paho.client.mqttv3.MqttException.*;

public class MqttIotHubConnection implements MqttConnectionStateListener, IotHubTransportConnection
{
    /** The MQTT connection lock. */
    private final Object MQTT_CONNECTION_LOCK = new Object();

    private final DeviceClientConfig config;
    private State state = State.CLOSED;

    private String iotHubUserName;
    private String iotHubUserPassword;
    private MqttConnection mqttConnection;

    //string constants
    private static final String WS_SSL_PREFIX = "wss://";
    private static final String WS_SSL_PORT_SUFFIX = ":443";

    private static final String WEBSOCKET_RAW_PATH = "/$iothub/websocket";
    private static final String WEBSOCKET_QUERY = "?iothub-no-client-cert=true";

    private static final String SSL_PREFIX = "ssl://";
    private static final String SSL_PORT_SUFFIX = ":8883";

    private static final String TWIN_API_VERSION = "api-version=2016-11-14";

    private static final int UNDEFINED_MQTT_CONNECT_CODE_LOWER_BOUND = 6;
    private static final int UNDEFINED_MQTT_CONNECT_CODE_UPPER_BOUND = 255;

    private IotHubListener listener;

    //Messaging clients
    private MqttMessaging deviceMessaging;
    private MqttDeviceTwin deviceTwin;
    private MqttDeviceMethod deviceMethod;

    /**
     * Constructs an instance from the given {@link DeviceClientConfig}
     * object.
     *
     * @param config the client configuration.
     */
    public MqttIotHubConnection(DeviceClientConfig config) throws IllegalArgumentException
    {
        synchronized (MQTT_CONNECTION_LOCK)
        {
            // Codes_SRS_MQTTIOTHUBCONNECTION_15_003: [The constructor shall throw a new IllegalArgumentException
            // if any of the parameters of the configuration is null or empty.]
            if (config == null)
            {
                throw new IllegalArgumentException("The DeviceClientConfig cannot be null.");
            }
            if (config.getIotHubHostname() == null || config.getIotHubHostname().length() == 0)
            {
                throw new IllegalArgumentException("hostName cannot be null or empty.");
            }
            if (config.getDeviceId() == null || config.getDeviceId().length() == 0)
            {
                throw new IllegalArgumentException("deviceID cannot be null or empty.");
            }
            if (config.getIotHubName() == null || config.getIotHubName().length() == 0)
            {
                throw new IllegalArgumentException("hubName cannot be null or empty.");
            }
            if (config.getAuthenticationType() == DeviceClientConfig.AuthType.SAS_TOKEN)
            {
                if (config.getIotHubConnectionString().getSharedAccessKey() == null || config.getIotHubConnectionString().getSharedAccessKey().isEmpty())
                {
                    if(config.getSasTokenAuthentication().getCurrentSasToken() == null || config.getSasTokenAuthentication().getCurrentSasToken().isEmpty())
                    {
                        //Codes_SRS_MQTTIOTHUBCONNECTION_34_020: [If the config has no shared access token, device key, or x509 certificates, this constructor shall throw an IllegalArgumentException.]
                        throw new IllegalArgumentException("Must have a deviceKey, a shared access token, or x509 certificate saved.");
                    }
                }
            }

            // Codes_SRS_MQTTIOTHUBCONNECTION_15_001: [The constructor shall save the configuration.]
            this.config = config;
            this.deviceMessaging = null;
            this.deviceMethod = null;
            this.deviceTwin = null;
        }
    }

    /**
     * Establishes a connection for the device and IoT Hub given in the client
     * configuration. If the connection is already open, the function shall do
     * nothing.
     *
     * @throws IOException if a connection could not to be established.
     */
    public void open() throws IOException
    {
        synchronized (MQTT_CONNECTION_LOCK)
        {
            //Codes_SRS_MQTTIOTHUBCONNECTION_15_006: [If the MQTT connection is already open,
            // the function shall do nothing.]
            if (this.state == State.OPEN)
            {
                return;
            }

            // Codes_SRS_MQTTIOTHUBCONNECTION_15_004: [The function shall establish an MQTT connection
            // with an IoT Hub using the provided host name, user name, device ID, and sas token.]
            try
            {
                SSLContext sslContext = null;
                if (this.config.getAuthenticationType() == DeviceClientConfig.AuthType.SAS_TOKEN)
                {
                    this.iotHubUserPassword = this.config.getSasTokenAuthentication().getRenewedSasToken();
                    sslContext = this.config.getSasTokenAuthentication().getSSLContext();
                }
                else if (this.config.getAuthenticationType() == DeviceClientConfig.AuthType.X509_CERTIFICATE)
                {
                    if (this.config.isUseWebsocket())
                    {
                        //Codes_SRS_MQTTIOTHUBCONNECTION_34_027: [If this function is called while using websockets and x509 authentication, an UnsupportedOperation shall be thrown.]
                        throw new UnsupportedOperationException("X509 authentication is not supported over MQTT_WS");
                    }

                    this.iotHubUserPassword = null;
                    sslContext = this.config.getX509Authentication().getSSLContext();
                }

                String clientIdentifier = "DeviceClientType=" + URLEncoder.encode(TransportUtils.JAVA_DEVICE_CLIENT_IDENTIFIER + TransportUtils.CLIENT_VERSION, "UTF-8");
                this.iotHubUserName = this.config.getIotHubHostname() + "/" + this.config.getDeviceId() + "/" + TWIN_API_VERSION + "&" + clientIdentifier;

                if (this.config.isUseWebsocket())
                {
                    //Codes_SRS_MQTTIOTHUBCONNECTION_25_018: [The function shall establish an MQTT WS connection with a server uri as wss://<hostName>/$iothub/websocket?iothub-no-client-cert=true if websocket was enabled.]
                    final String wsServerUri = WS_SSL_PREFIX + this.config.getIotHubHostname() + WEBSOCKET_RAW_PATH + WEBSOCKET_QUERY ;
                    mqttConnection = new MqttConnection(wsServerUri,
                            this.config.getDeviceId(), this.iotHubUserName, this.iotHubUserPassword, sslContext);
                }
                else
                {
                    //Codes_SRS_MQTTIOTHUBCONNECTION_25_019: [The function shall establish an MQTT connection with a server uri as ssl://<hostName>:8883 if websocket was not enabled.]
                    final String serverUri = SSL_PREFIX + this.config.getIotHubHostname() + SSL_PORT_SUFFIX;
                    mqttConnection = new MqttConnection(serverUri,
                            this.config.getDeviceId(), this.iotHubUserName, this.iotHubUserPassword, sslContext);
                }

                //Codes_SRS_MQTTIOTHUBCONNECTION_34_030: [This function shall instantiate this object's MqttMessaging object with this object as the listener.]
                this.deviceMessaging = new MqttMessaging(mqttConnection, this.config.getDeviceId(), this);
                this.mqttConnection.setMqttCallback(this.deviceMessaging);
                this.deviceMethod = new MqttDeviceMethod(mqttConnection);
                this.deviceTwin = new MqttDeviceTwin(mqttConnection);

                // Codes_SRS_MQTTIOTHUBCONNECTION_99_017 : [The function shall set DeviceClientConfig object needed for SAS token renewal.]
                this.deviceMessaging.setDeviceClientConfig(this.config);

                this.deviceMessaging.start();
                this.state = State.OPEN;
            }
            catch (Exception e)
            {
                this.state = State.CLOSED;
                // Codes_SRS_MQTTIOTHUBCONNECTION_15_005: [If an MQTT connection is unable to be established
                // for any reason, the function shall throw an IOException.]
                if (this.deviceMethod != null)
                {
                    this.deviceMethod.stop();
                }
                if (this.deviceTwin != null )
                {
                    this.deviceTwin.stop();
                }
                if (this.deviceMessaging != null)
                {
                    this.deviceMessaging.stop();
                }
                throw new IOException(e);
            }
        }
    }

    /**
     * Closes the connection. After the connection is closed, it is no longer usable.
     * If the connection is already closed, the function shall do nothing.
     *
     */
    public void close()
    {
        // Codes_SRS_MQTTIOTHUBCONNECTION_15_007: [If the MQTT session is closed, the function shall do nothing.]
        if (this.state == State.CLOSED)
        {
            return;
        }

        // Codes_SRS_MQTTIOTHUBCONNECTION_15_006: [The function shall close the MQTT connection.]
        try
        {
            this.deviceMethod.stop();
            this.deviceMethod = null;

            this.deviceTwin.stop();
            this.deviceTwin = null;

            this.deviceMessaging.stop();
            this.deviceMessaging = null;

            this.state = State.CLOSED;
        }
        catch (Exception e)
        {
            this.state = State.CLOSED;
        }
    }

    /**
     * Sends an event message.
     *
     * @param message the event message.
     *
     * @return the status code from sending the event message.
     *
     * @throws IllegalStateException if the MqttIotHubConnection is not open
     */
    public IotHubStatusCode sendEvent(Message message) throws IllegalStateException
    {
        synchronized (MQTT_CONNECTION_LOCK)
        {
            // Codes_SRS_MQTTIOTHUBCONNECTION_15_010: [If the message is null or empty,
            // the function shall return status code BAD_FORMAT.]
            if (message == null || message.getBytes() == null ||
                    (
                            (message.getMessageType() != MessageType.DEVICE_TWIN
                                    && message.getMessageType() != MessageType.DEVICE_METHODS)
                                    && message.getBytes().length == 0))
            {
                return IotHubStatusCode.BAD_FORMAT;
            }

            // Codes_SRS_MQTTIOTHUBCONNECTION_15_013: [If the MQTT connection is closed,
            // the function shall throw an IllegalStateException.]
            if (this.state == State.CLOSED)
            {
                throw new IllegalStateException("Cannot send event using a closed MQTT connection");
            }

            // Codes_SRS_MQTTIOTHUBCONNECTION_15_008: [The function shall send an event message
            // to the IoT Hub given in the configuration.]
            // Codes_SRS_MQTTIOTHUBCONNECTION_15_011: [If the message was successfully received by the service,
            // the function shall return status code OK_EMPTY.]
            IotHubStatusCode result = IotHubStatusCode.OK_EMPTY;

            if (this.config.getAuthenticationType() == DeviceClientConfig.AuthType.SAS_TOKEN && this.config.getSasTokenAuthentication().isRenewalNecessary())
            {
                //Codes_SRS_MQTTIOTHUBCONNECTION_34_035: [If the sas token saved in the config has expired and needs to be renewed, this function shall return UNAUTHORIZED.]
                return IotHubStatusCode.UNAUTHORIZED;
            }

            try
            {
                // Codes_SRS_MQTTIOTHUBCONNECTION_15_009: [The function shall send the message payload.]
                if (message.getMessageType() == MessageType.DEVICE_METHODS)
                {
                    this.deviceMethod.start();
                    this.deviceMethod.send((IotHubTransportMessage) message);
                }
                else if (message.getMessageType() == MessageType.DEVICE_TWIN)
                {
                    this.deviceTwin.start();
                    this.deviceTwin.send((IotHubTransportMessage) message);
                }
                else
                {
                    this.deviceMessaging.send(message);
                }
            }
            // Codes_SRS_MQTTIOTHUBCONNECTION_15_012: [If the message was not successfully
            // received by the service, the function shall return status code ERROR.]
            catch (Exception e)
            {
                result = IotHubStatusCode.ERROR;
            }

            return result;
        }
    }

    /**
     * Receives a message, if one exists.
     *
     * @return the message received, or null if none exists.
     *
     * @throws IllegalStateException if the connection state is currently closed.
     * @throws IOException if receiving on any of messaging clients fail.
     */
    public Message receiveMessage() throws IllegalStateException, IOException
    {
        // Codes_SRS_MQTTIOTHUBCONNECTION_15_015: [If the MQTT connection is closed,
        // the function shall throw an IllegalStateException.]
        if (this.state == State.CLOSED)
        {
            throw new IllegalStateException("The MQTT connection is currently closed. Call open() before attempting " +
                    "to receive a message.");
        }


        // Codes_SRS_MQTTIOTHUBCONNECTION_15_014: [The function shall attempt to consume a message
        // from various messaging clients.]
        // Codes_SRS__MQTTIOTHUBCONNECTION_34_016: [If any of the messaging clients throw an exception, The associated message will be removed from the queue and the exception will be propagated up to the receive task.]
        Message message = this.deviceMethod.receive();
        if (message == null)
        {
            message = deviceTwin.receive();
        }

        if (message == null)
        {
            message = deviceMessaging.receive();
        }

        return message;
    }

    public void onConnectionLost(Throwable throwable)
    {
        if (throwable instanceof MqttException)
        {
            //Codes_SRS_MQTTIOTHUBCONNECTION_34_037: [If the provided throwable is an instance of MqttException, this function shall derive the associated ConnectionStatusException and notify the listener of that derived exception.]
            ConnectionStatusException connectionStatusException = getConnectionStatusExceptionFromMqttException((MqttException) throwable);
            this.listener.onConnectionLost(connectionStatusException);
        }
        else
        {
            //Codes_SRS_MQTTIOTHUBCONNECTION_34_037: [If the provided throwable is not an instance of MqttException, this function shall notify the listener of that throwable.]
            //Currently, this should never happen. Throwable should always be an MqttException, but Paho might change
            this.listener.onConnectionLost(throwable);
        }
    }

    public void onConnectionEstablished()
    {
        //Codes_SRS_MQTTIOTHUBCONNECTION_34_036: [This function shall notify its listener that connection was established successfully.]
        this.listener.onConnectionEstablished(null);
    }

    @Override
    public void addListener(IotHubListener listener) throws IOException
    {
        this.listener = listener;
    }

    @Override
    public IotHubStatusCode sendMessage(Message message) throws IOException
    {
        return null;
    }

    @Override
    public IotHubStatusCode sendMessageResult(Message message, IotHubMessageResult result) throws IOException
    {
        return null;
    }

    private static ConnectionStatusException getConnectionStatusExceptionFromMqttException(MqttException mqttException)
    {
        switch (mqttException.getReasonCode())
        {
            case REASON_CODE_CLIENT_EXCEPTION:
                // MQTT Client encountered an exception, no connect code retrieved from service, so the reason
                // for this connection loss is in the mqttException cause
                if (mqttException.getCause() instanceof UnknownHostException || mqttException.getCause() instanceof InterruptedException)
                {
                    //Codes_SRS_MQTTIOTHUBCONNECTION_34_039: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map all client exceptions with underlying UnknownHostException or InterruptedException to a retryable ProtocolConnectionStatusException.]
                    ConnectionStatusException connectionStatusException = new ProtocolConnectionStatusException(mqttException);
                    connectionStatusException.setRetryable(true);
                    return connectionStatusException;
                }
                else
                {
                    //Codes_SRS_MQTTIOTHUBCONNECTION_34_040: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map all client exceptions without underlying UnknownHostException and InterruptedException to a non retryable ProtocolConnectionStatusException.]
                    return new ProtocolConnectionStatusException(mqttException);
                }
            case REASON_CODE_INVALID_PROTOCOL_VERSION:
                // Codes_SRS_MQTTIOTHUBCONNECTION_34_041: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map REASON_CODE_INVALID_PROTOCOL_VERSION to ConnectionStatusMqttRejectedProtocolVersionException.]
                return new ConnectionStatusMqttRejectedProtocolVersionException(mqttException);
            case REASON_CODE_INVALID_CLIENT_ID:
                // Codes_SRS_MQTTIOTHUBCONNECTION_34_042: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map REASON_CODE_INVALID_CLIENT_ID to ConnectionStatusMqttIdentifierRejectedException.]
                return new ConnectionStatusMqttIdentifierRejectedException(mqttException);
            case REASON_CODE_BROKER_UNAVAILABLE:
                // Codes_SRS_MQTTIOTHUBCONNECTION_34_043: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map REASON_CODE_BROKER_UNAVAILABLE to ConnectionStatusMqttServerUnavailableException.]
                return new ConnectionStatusMqttServerUnavailableException(mqttException);
            case REASON_CODE_FAILED_AUTHENTICATION:
                // Codes_SRS_MQTTIOTHUBCONNECTION_34_044: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map REASON_CODE_FAILED_AUTHENTICATION to ConnectionStatusMqttBadUsernameOrPasswordException.]
                return new ConnectionStatusMqttBadUsernameOrPasswordException(mqttException);
            case REASON_CODE_NOT_AUTHORIZED:
                // Codes_SRS_MQTTIOTHUBCONNECTION_34_045: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map REASON_CODE_NOT_AUTHORIZED to ConnectionStatusMqttUnauthorizedException.]
                return new ConnectionStatusMqttUnauthorizedException(mqttException);
            case REASON_CODE_SUBSCRIBE_FAILED:
            case REASON_CODE_CLIENT_NOT_CONNECTED:
            case REASON_CODE_TOKEN_INUSE:
            case REASON_CODE_CONNECTION_LOST:
            case REASON_CODE_SERVER_CONNECT_ERROR:
            case REASON_CODE_CLIENT_TIMEOUT:
            case REASON_CODE_WRITE_TIMEOUT:
            case REASON_CODE_MAX_INFLIGHT:
                // Codes_SRS_MQTTIOTHUBCONNECTION_34_046: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map REASON_CODE_SUBSCRIBE_FAILED, REASON_CODE_CLIENT_NOT_CONNECTED, REASON_CODE_TOKEN_INUSE, REASON_CODE_CONNECTION_LOST, REASON_CODE_SERVER_CONNECT_ERROR, REASON_CODE_CLIENT_TIMEOUT, REASON_CODE_WRITE_TIMEOUT, and REASON_CODE_MAX_INFLIGHT to a retryable ProtocolConnectionStatusException.]
                //Client lost internet connection, or server could not be reached, or other retryable connection exceptions
                ConnectionStatusException connectionStatusException = new ProtocolConnectionStatusException(mqttException);
                connectionStatusException.setRetryable(true);
                return connectionStatusException;
            default:
                if (mqttException.getReasonCode() >= UNDEFINED_MQTT_CONNECT_CODE_LOWER_BOUND && mqttException.getReasonCode() <= UNDEFINED_MQTT_CONNECT_CODE_UPPER_BOUND)
                {
                    //Mqtt connect codes 6 to 255 are reserved for future MQTT standard codes and are unused as of MQTT 3
                    //Codes_SRS_MQTTIOTHUBCONNECTION_34_047: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map any connect codes between 6 and 255 inclusive to ConnectionStatusMqttUnexpectedErrorException.]
                    return new ConnectionStatusMqttUnexpectedErrorException(mqttException);
                }
                else
                {
                    //Mqtt connect code was not MQTT standard code, and was not a retryable exception as defined by Paho
                    //Codes_SRS_MQTTIOTHUBCONNECTION_34_048: [When deriving the ConnectionStatusException from the provided MqttException, this function shall map all other MqttExceptions to ProtocolConnectionStatusException.]
                    return new ProtocolConnectionStatusException(mqttException);
                }
        }
    }
}
