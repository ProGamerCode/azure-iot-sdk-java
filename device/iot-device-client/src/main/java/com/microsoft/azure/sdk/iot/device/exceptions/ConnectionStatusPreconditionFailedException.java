/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.ProtocolConnectionStatusException;

public class ConnectionStatusPreconditionFailedException extends ProtocolConnectionStatusException
{
    public ConnectionStatusPreconditionFailedException()
    {
        super();
    }

    public ConnectionStatusPreconditionFailedException(String message)
    {
        super(message);
    }

    public ConnectionStatusPreconditionFailedException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public ConnectionStatusPreconditionFailedException(Throwable cause)
    {
        super(cause);
    }
}
