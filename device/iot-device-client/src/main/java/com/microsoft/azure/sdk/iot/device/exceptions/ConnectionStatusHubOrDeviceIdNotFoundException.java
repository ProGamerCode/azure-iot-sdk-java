/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

public class ConnectionStatusHubOrDeviceIdNotFoundException extends ProtocolConnectionStatusException
{
    public ConnectionStatusHubOrDeviceIdNotFoundException()
    {
        super();
    }

    public ConnectionStatusHubOrDeviceIdNotFoundException(String message)
    {
        super(message);
    }

    public ConnectionStatusHubOrDeviceIdNotFoundException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusHubOrDeviceIdNotFoundException(Throwable cause)
    {
        super(cause);
    }
}
