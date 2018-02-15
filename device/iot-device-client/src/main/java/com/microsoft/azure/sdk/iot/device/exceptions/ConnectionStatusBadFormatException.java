/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

public class ConnectionStatusBadFormatException extends ProtocolConnectionStatusException
{
    public ConnectionStatusBadFormatException()
    {
        super();
    }

    public ConnectionStatusBadFormatException(String message)
    {
        super(message);
    }

    public ConnectionStatusBadFormatException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusBadFormatException(Throwable cause)
    {
        super(cause);
    }
}
