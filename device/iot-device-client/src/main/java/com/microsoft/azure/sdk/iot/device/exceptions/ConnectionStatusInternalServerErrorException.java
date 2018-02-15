/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

public class ConnectionStatusInternalServerErrorException extends ProtocolConnectionStatusException
{
    public ConnectionStatusInternalServerErrorException()
    {
        super();
    }

    public ConnectionStatusInternalServerErrorException(String message)
    {
        super(message);
    }

    public ConnectionStatusInternalServerErrorException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusInternalServerErrorException(Throwable cause)
    {
        super(cause);
    }
}
