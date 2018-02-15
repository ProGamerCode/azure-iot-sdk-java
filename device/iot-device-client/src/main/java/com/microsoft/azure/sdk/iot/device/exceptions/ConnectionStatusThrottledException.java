/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.exceptions;

import com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions.IotHubConnectionStatusException;

public class ConnectionStatusThrottledException extends IotHubConnectionStatusException
{
    public ConnectionStatusThrottledException()
    {
        super();
        this.isRetryable = true;
    }

    public ConnectionStatusThrottledException(String message)
    {
        super(message);
        this.isRetryable = true;
    }

    public ConnectionStatusThrottledException(String message, Throwable cause)
    {
        super(message, cause);
        this.isRetryable = true;
    }

    public ConnectionStatusThrottledException(Throwable cause)
    {
        super(cause);
        this.isRetryable = true;
    }
}
