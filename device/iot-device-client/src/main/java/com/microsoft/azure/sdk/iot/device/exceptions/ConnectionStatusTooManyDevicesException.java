/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

public class ConnectionStatusTooManyDevicesException extends ProtocolConnectionStatusException
{
    public ConnectionStatusTooManyDevicesException()
    {
        super();
    }

    public ConnectionStatusTooManyDevicesException(String message)
    {
        super(message);
    }

    public ConnectionStatusTooManyDevicesException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusTooManyDevicesException(Throwable cause)
    {
        super(cause);
    }
}
