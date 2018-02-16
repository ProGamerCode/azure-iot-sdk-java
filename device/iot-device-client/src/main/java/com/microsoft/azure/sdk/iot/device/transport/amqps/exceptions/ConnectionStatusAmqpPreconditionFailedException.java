/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.amqps.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 * This exception is thrown when a amqp:precondition-failed error is encountered over an AMQP connection
 *
 * See {@linktourl http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-complete-v1.0-os.pdf}
 */
public class ConnectionStatusAmqpPreconditionFailedException extends ProtocolConnectionStatusException
{
    public static final String errorCode = "amqp:precondition-failed";

    public ConnectionStatusAmqpPreconditionFailedException()
    {
        super();
    }

    public ConnectionStatusAmqpPreconditionFailedException(String message)
    {
        super(message);
    }

    public ConnectionStatusAmqpPreconditionFailedException(String message, Throwable cause) { super(message, cause); }

    public ConnectionStatusAmqpPreconditionFailedException(Throwable cause) { super(cause); }
}
