/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.amqps.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 * This exception is thrown when a amqp:link:transfer-limit-exceeded error is encountered over an AMQP connection
 *
 * See {@linktourl http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-complete-v1.0-os.pdf}
 */
public class ConnectionStatusAmqpTransferLimitExceededException extends ProtocolConnectionStatusException
{
    public static final String errorCode = "amqp:link:transfer-limit-exceeded";

    public ConnectionStatusAmqpTransferLimitExceededException()
    {
        super();
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpTransferLimitExceededException(String message)
    {
        super(message);
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpTransferLimitExceededException(String message, Throwable cause)
    {
        super(message, cause);
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpTransferLimitExceededException(Throwable cause)
    {
        super(cause);
        this.isRetryable = true;
    }
}
