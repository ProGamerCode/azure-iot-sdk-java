/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.IotHubConnectionStatusException;

public class ConnectionStatusUnknownException extends IotHubConnectionStatusException
{
    public ConnectionStatusUnknownException()
    {
        super();
    }

    public ConnectionStatusUnknownException(String message)
    {
        super(message);
    }

    public ConnectionStatusUnknownException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusUnknownException(Throwable cause)
    {
        super(cause);
    }
}
