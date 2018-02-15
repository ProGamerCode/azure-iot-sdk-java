/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions;

import com.microsoft.azure.sdk.iot.device.exceptions.IotHubException;

public class ConnectionStatusException extends IotHubException
{
    protected boolean isRetryable;

    public ConnectionStatusException()
    {
        super();
        this.isRetryable = false;
    }

    public ConnectionStatusException(String message)
    {
        super(message);
        this.isRetryable = false;
    }

    public ConnectionStatusException(String message, Throwable cause)
    {
        super(message, cause);
        this.isRetryable = false;
    }

    public ConnectionStatusException(Throwable cause)
    {
        super(cause);
        this.isRetryable = false;
    }

    public boolean isRetryable()
    {
        return this.isRetryable;
    }

    public void setRetryable(boolean isRetryable)
    {
        this.isRetryable = isRetryable;
    }
}
