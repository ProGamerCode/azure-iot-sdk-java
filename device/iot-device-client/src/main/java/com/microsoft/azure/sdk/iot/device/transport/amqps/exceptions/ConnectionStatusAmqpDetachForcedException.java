/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.amqps.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 * This exception is thrown when a amqp:link:detach-forced error is encountered over an AMQP connection
 *
 * See {@linktourl http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-complete-v1.0-os.pdf}
 */
public class ConnectionStatusAmqpDetachForcedException extends ProtocolConnectionStatusException
{
    public static final String errorCode = "amqp:link:detach-forced";

    public ConnectionStatusAmqpDetachForcedException()
    {
        super();
    }

    public ConnectionStatusAmqpDetachForcedException(String message)
    {
        super(message);
    }

    public ConnectionStatusAmqpDetachForcedException(String message, Throwable cause) { super(message, cause); }

    public ConnectionStatusAmqpDetachForcedException(Throwable cause) { super(cause); }
}
