/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.amqps.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 * This exception is thrown when a amqp:resource-deleted error is encountered over an AMQP connection
 *
 * See {@linktourl http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-complete-v1.0-os.pdf}
 */
public class ConnectionStatusAmqpResourceDeletedException extends ProtocolConnectionStatusException
{
    public static final String errorCode = "amqp:resource-deleted";

    public ConnectionStatusAmqpResourceDeletedException() { super(); }

    public ConnectionStatusAmqpResourceDeletedException(String message)
    {
        super(message);
    }

    public ConnectionStatusAmqpResourceDeletedException(String message, Throwable cause) { super(message, cause); }

    public ConnectionStatusAmqpResourceDeletedException(Throwable cause) { super(cause); }
}
