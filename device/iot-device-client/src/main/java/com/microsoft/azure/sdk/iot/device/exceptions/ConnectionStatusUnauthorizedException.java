/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

public class ConnectionStatusUnauthorizedException extends ProtocolConnectionStatusException
{
    public ConnectionStatusUnauthorizedException()
    {
        super();
    }

    public ConnectionStatusUnauthorizedException(String message)
    {
        super(message);
    }

    public ConnectionStatusUnauthorizedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusUnauthorizedException(Throwable cause)
    {
        super(cause);
    }
}
