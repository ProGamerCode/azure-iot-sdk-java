/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

public class ConnectionStatusRequestEntityTooLargeException extends ProtocolConnectionStatusException
{
    public ConnectionStatusRequestEntityTooLargeException()
    {
        super();
    }

    public ConnectionStatusRequestEntityTooLargeException(String message)
    {
        super(message);
    }

    public ConnectionStatusRequestEntityTooLargeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusRequestEntityTooLargeException(Throwable cause)
    {
        super(cause);
    }
}
