/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.amqps.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 * This exception is thrown when a amqp:invalid-field error is encountered over an AMQP connection
 *
 * See {@linktourl http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-complete-v1.0-os.pdf}
 */
public class ConnectionStatusAmqpInvalidFieldException extends ProtocolConnectionStatusException
{
    public static final String errorCode = "amqp:invalid-field";

    public ConnectionStatusAmqpInvalidFieldException()
    {
        super();
    }

    public ConnectionStatusAmqpInvalidFieldException(String message)
    {
        super(message);
    }

    public ConnectionStatusAmqpInvalidFieldException(String message, Throwable cause) { super(message, cause); }

    public ConnectionStatusAmqpInvalidFieldException(Throwable cause) { super(cause); }
}
