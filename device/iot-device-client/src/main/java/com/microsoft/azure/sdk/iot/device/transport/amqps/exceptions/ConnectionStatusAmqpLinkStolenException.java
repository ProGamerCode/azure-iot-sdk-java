/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.amqps.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 * This exception is thrown when a amqp:link:stolen error is encountered over an AMQP connection
 *
 * See {@linktourl http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-complete-v1.0-os.pdf}
 */
public class ConnectionStatusAmqpLinkStolenException extends ProtocolConnectionStatusException
{
    public static final String errorCode = "amqp:link:stolen";

    public ConnectionStatusAmqpLinkStolenException()
    {
        super();
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpLinkStolenException(String message)
    {
        super(message);
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpLinkStolenException(String message, Throwable cause)
    {
        super(message, cause);
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpLinkStolenException(Throwable cause)
    {
        super(cause);
        this.isRetryable = true;
    }
}
