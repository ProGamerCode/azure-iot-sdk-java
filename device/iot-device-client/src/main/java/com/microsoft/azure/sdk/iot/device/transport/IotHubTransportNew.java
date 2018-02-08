/*
 *
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 *
 */

package com.microsoft.azure.sdk.iot.device.transport;

import com.microsoft.azure.sdk.iot.device.*;
import com.microsoft.azure.sdk.iot.device.transport.amqps.AmqpsIotHubConnection;
import com.microsoft.azure.sdk.iot.device.transport.https.HttpsIotHubConnection;
import com.microsoft.azure.sdk.iot.device.transport.mqtt.MqttIotHubConnection;

import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class IotHubTransportNew implements Closeable, IotHubListener
{
    private State state;
    private final IotHubTransportConnection iotHubTransportConnection;

    /* Messages waiting to be sent to the IoT Hub. */
    private final Queue<IotHubOutboundPacket> waitingMessages;

    /* Messages which are sent to the IoT Hub but did not receive ack yet. */
    private final Queue<IotHubOutboundPacket> inProgressMessages;

    /* Messages received from the IoT Hub */
    private final Queue<Message> receivedMessages;

    /* Messages whose callbacks that are waiting to be invoked. */
    private final Queue<IotHubCallbackPacket> callbackList;

    /*Connection Status callback information */
    private IotHubConnectionStateCallback stateCallback;
    private Object stateCallbackContext;

    /* Locks to synchronise queuing operations between send and receive */
    final private Object sendLock;
    final private Object receiveLock;

    private final DeviceClientConfig deviceClientConfig;

    private final CustomLogger logger;

    public IotHubTransportNew(DeviceClientConfig deviceClientConfig) throws IOException
    {
        this.state = State.CLOSED;
        this.waitingMessages = new LinkedBlockingQueue<>();
        this.inProgressMessages = new LinkedBlockingQueue<>();
        this.receivedMessages = new LinkedBlockingQueue<>();
        this.callbackList  = new LinkedBlockingQueue<>();
        this.sendLock = new Object();
        this.receiveLock = new Object();
        this.deviceClientConfig = deviceClientConfig;
        this.logger = new CustomLogger(this.getClass());

        switch (deviceClientConfig.getProtocol())
        {
            case HTTPS:
                this.iotHubTransportConnection = new HttpsIotHubConnection(deviceClientConfig);
                break;
            case MQTT:
                this.iotHubTransportConnection = new MqttIotHubConnection(deviceClientConfig);
                break;
            case MQTT_WS:
                this.iotHubTransportConnection = new MqttIotHubConnection(deviceClientConfig);
                break;
            case AMQPS:
                this.iotHubTransportConnection = new AmqpsIotHubConnection(deviceClientConfig);
                break;
            case AMQPS_WS:
                this.iotHubTransportConnection = new AmqpsIotHubConnection(deviceClientConfig);
                break;
            default:
                    throw new IOException("Protocol not supported");
        }
    }

    /**
     * Method executed when a message was acknowledged by IoTHub.
     *
     * @param message
     * @param e
     */
    @Override
    public void messageSent(Message message, Throwable e)
    {
        synchronized (this)
        {
            if (e != null)
            {
                handleException(e);
            }
            else
            {
                // remove from in progress queue and add to callback queue
                if (inProgressMessages.contains(message))
                {

                }
            }

        }
    }

    @Override
    public void messageReceived(Message message, Throwable e)
    {
        synchronized (receiveLock)
        {
            if (e != null)
            {
                handleException(e);
            }
            else
            {
                logger.LogInfo("Message with hashcode %s is received from IotHub on %s, method name is %s ", message.hashCode(), new Date(), logger.getMethodName());
                // Codes_SRS_AMQPSTRANSPORT_15_034: [The message received is added to the list of messages to be processed.]
                this.receivedMessages.add(message);
            }
        }
    }

    @Override
    public void connectionLost(Throwable e)
    {
        if (e != null)
        {
            handleException(e);
        }
        else
        {
            // Move in progress to waiting as they may not have sent
            logger.LogInfo("The messages in progress are buffered to be sent again due to a connection loss, method name is %s ", logger.getMethodName());
            // Codes_SRS_AMQPSTRANSPORT_15_032: [The messages in progress are buffered to be sent again.]
            this.waitingMessages.addAll(inProgressMessages);

            // inform user of connection drop
            if (this.stateCallback != null)
            {
                this.stateCallback.execute(IotHubConnectionState.CONNECTION_DROP, this.stateCallbackContext);
            }
        }
    }

    @Override
    public void connectionEstablished(Throwable e)
    {
        if (e != null)
        {
            handleException(e);
        }
        else
        {
            // Inform user that connection is established
            logger.LogInfo("The connection to the IoT Hub has been established, method name is %s ", logger.getMethodName());
            // Notify listener that the connection is up
            // Codes_SRS_AMQPSTRANSPORT_99_002: [Registered connection state callback is notified that the connection has been established.]
            if (this.stateCallback != null)
            {
                this.stateCallback.execute(IotHubConnectionState.CONNECTION_SUCCESS, this.stateCallbackContext);
            }
        }

    }

    /**
     * Establishes a communication channel with an IoT Hub. If a channel is
     * already open, the function shall do nothing.
     *
     * @throws IOException if a communication channel cannot be
     * established.
     */
    public synchronized void open() throws IOException
    {
        if(this.state == State.OPEN)
        {
            return;
        }

        this.iotHubTransportConnection.addListener(this);
        this.iotHubTransportConnection.open();
        this.state = State.OPEN;
    }

    /**
     * Establishes a communication channel usingmultiplexing with an IoT Hub. If a channel is
     * already open, the function shall do nothing.
     *
     * @param deviceClientList the list of clients use the same transport.
     * @throws IOException if a communication channel cannot be
     * established.
     */
/*    void multiplexOpen(List<DeviceClient> deviceClientList) throws IOException
    {

    }*/

    /**
     * Closes all resources used to communicate with an IoT Hub. Once {@code close()} is
     * called, the transport is no longer usable. If the transport is already
     * closed, the function shall do nothing.
     *
     * @throws IOException if an error occurs in closing the transport.
     */
    public synchronized void close() throws IOException
    {
        if (this.state == State.CLOSED)
        {
            return;
        }

        // Move waiting messages to callback to inform user of close
        while (!this.waitingMessages.isEmpty())
        {
            IotHubOutboundPacket packet = this.waitingMessages.remove();
            Message message = packet.getMessage();

            // Codes_SRS_AMQPSTRANSPORT_15_015: [The function shall skip messages with null or empty body.]
            if (message != null && message.getBytes().length > 0)
            {

                IotHubCallbackPacket callbackPacket = new IotHubCallbackPacket(IotHubStatusCode.MESSAGE_CANCELLED_ONCLOSE, packet.getCallback(), packet.getContext());
                this.callbackList.add(callbackPacket);
            }
        }

        // Move in progress message to callback to inform user of close
        while(!this.inProgressMessages.isEmpty())
        {
            // TODO: check how HTTP transport will behave here where it needs response message as oppose to packet
            IotHubOutboundPacket packet = this.inProgressMessages.remove();
            IotHubCallbackPacket callbackPacket = new IotHubCallbackPacket(IotHubStatusCode.MESSAGE_CANCELLED_ONCLOSE, packet.getCallback(), packet.getContext());
            this.callbackList.add(callbackPacket);
        }

        // invoke all the callbacks
        invokeCallbacks();

        this.iotHubTransportConnection.close();

        this.state = State.CLOSED;
    }

    private void handleException(Throwable e)
    {

    }

    /**
     * Adds a message to the transport queue.
     *
     * @param message the message to be sent.
     * @param callback the callback to be invoked when a response for the
     * message is received.
     * @param callbackContext the context to be passed in when the callback is
     * invoked.
     */
    void addMessage(Message message,
                    IotHubEventCallback callback,
                    Object callbackContext)
    {

        // Codes_SRS_AMQPSTRANSPORT_15_010: [If the AMQPS session is closed, the function shall throw an IllegalStateException.]
        if (this.state == State.CLOSED)
        {
            logger.LogError("Cannot add a message when the AMQPS transport is closed, method name is %s ", logger.getMethodName());
            throw new IllegalStateException("Cannot add a message when the AMQPS transport is closed.");
        }

        // Codes_SRS_AMQPSTRANSPORT_15_011: [The function shall add a packet containing the message, callback, and callback context to the queue of messages waiting to be sent.]
        IotHubOutboundPacket packet = new IotHubOutboundPacket(message, callback, callbackContext);
        this.waitingMessages.add(packet);

    }

    /**
     * Adds a message to the transport queue.
     *
     * @param message the message to be sent.
     * @param callback the callback to be invoked when a response for the
     * message is received.
     * @param callbackContext the context to be passed in when the callback is
     * invoked.
     */
    void addMessage(Message message,
                    IotHubResponseCallback callback,
                    Object callbackContext)
    {
        // TODO : Check if you can get rid of this
    }

    /**
     * Sends all messages on the transport queue. If a previous send attempt had
     * failed, the function will attempt to resend the messages in the previous
     * attempt.
     *
     * @throws IOException if the server could not be reached.
     */
    public synchronized void sendMessages() throws IOException
    {

    }

    /** Invokes the callbacks for all completed requests. */
    public synchronized void invokeCallbacks()
    {
        // Codes_SRS_AMQPSTRANSPORT_15_019: [If the transport closed, the function shall throw an IllegalStateException.]
        if (this.state == State.CLOSED)
        {
            logger.LogError("Cannot invoke callbacks when AMQPS transport is closed, method name is %s ", logger.getMethodName());
            throw new IllegalStateException("Cannot invoke callbacks when AMQPS transport is closed.");
        }

        // Codes_SRS_AMQPSTRANSPORT_15_020: [The function shall invoke all the callbacks from the callback queue.]
        while (!this.callbackList.isEmpty())
        {
            IotHubCallbackPacket packet = this.callbackList.remove();

            IotHubStatusCode status = packet.getStatus();
            IotHubEventCallback callback = packet.getCallback();
            Object context = packet.getContext();

            logger.LogInfo("Invoking the callback function for sent message, IoT Hub responded to message with status %s, method name is %s ", status.name(), logger.getMethodName());
            callback.execute(status, context);
        }
    }

    /**
     * <p>
     * Invokes the message callback if a message is found and
     * responds to the IoT Hub on how the processed message should be
     * handled by the IoT Hub.
     * </p>
     * If no message callback is set, the function will do nothing.
     *
     * @throws IOException if the server could not be reached.
     */
    public void handleMessage() throws IOException
    {
        synchronized (receiveLock)
        {

        }
    }

    /**
     * Returns {@code true} if the transport has no more messages to handle,
     * and {@code false} otherwise.
     *
     * @return {@code true} if the transport has no more messages to handle,
     * and {@code false} otherwise.
     */
    public boolean isEmpty()
    {
        return this.waitingMessages.isEmpty() && this.inProgressMessages.size() == 0 && this.callbackList.isEmpty();
    }

    /**
     * Registers a callback to be executed whenever the connection to the IoT Hub is lost or established.
     *
     * @param callback the callback to be called.
     * @param callbackContext a context to be passed to the callback. Can be
     * {@code null} if no callback is provided.
     */
    public void registerConnectionStateCallback(IotHubConnectionStateCallback callback, Object callbackContext)
    {
        //Codes_SRS_AMQPSTRANSPORT_34_042: If the provided callback is null, an IllegalArgumentException shall be thrown.]
        if (callback == null)
        {
            throw new IllegalArgumentException("Callback cannot be null");
        }

        // Codes_SRS_AMQPSTRANSPORT_99_003: [The registerConnectionStateCallback shall register the connection state callback.]
        this.stateCallback = callback;
        this.stateCallbackContext = callbackContext;
    }
}
