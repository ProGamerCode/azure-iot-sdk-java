/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.amqps.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

/**
 * This exception is thrown when a amqp:link:redirect error is encountered over an AMQP connection
 *
 * Check the headers of the message with this error to see where you are being redirected to
 *
 * See {@linktourl http://docs.oasis-open.org/amqp/core/v1.0/os/amqp-core-complete-v1.0-os.pdf}
 */
public class ConnectionStatusAmqpLinkRedirectException extends ProtocolConnectionStatusException
{
    public static final String errorCode = "amqp:link:redirect";

    public ConnectionStatusAmqpLinkRedirectException()
    {
        super();
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpLinkRedirectException(String message)
    {
        super(message);
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpLinkRedirectException(String message, Throwable cause)
    {
        super(message, cause);
        this.isRetryable = true;
    }

    public ConnectionStatusAmqpLinkRedirectException(Throwable cause)
    {
        super(cause);
        this.isRetryable = true;
    }
}
