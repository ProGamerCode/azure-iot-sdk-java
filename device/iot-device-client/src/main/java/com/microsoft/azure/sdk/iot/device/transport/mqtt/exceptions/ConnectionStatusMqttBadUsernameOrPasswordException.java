/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.mqtt.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 *  This exception is thrown when an MQTT Connection Return code of 0x04 is encountered when opening an MQTT connection
 *
 * <a href="http://docs.oasis-open.org/mqtt/mqtt/v3.1.1/csprd02/mqtt-v3.1.1-csprd02.html#_Toc385349771">
 *     MQTT Connect Return Code Documentation</a>
 */
public class ConnectionStatusMqttBadUsernameOrPasswordException extends ProtocolConnectionStatusException
{
    public ConnectionStatusMqttBadUsernameOrPasswordException() { super(); }

    public ConnectionStatusMqttBadUsernameOrPasswordException(String message)
    {
        super(message);
    }

    public ConnectionStatusMqttBadUsernameOrPasswordException(String message, Throwable cause) { super(message, cause); }

    public ConnectionStatusMqttBadUsernameOrPasswordException(Throwable cause) { super(cause); }
}
