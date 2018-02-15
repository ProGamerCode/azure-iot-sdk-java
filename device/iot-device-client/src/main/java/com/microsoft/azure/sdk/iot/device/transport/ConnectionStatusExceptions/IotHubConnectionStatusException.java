/*
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.sdk.iot.device.transport.ConnectionStatusExceptions;

/**
 * Exception class that covers all exceptions communicated from the IoT Hub that are not due to connection issues in
 * the transport protocols. These exceptions map to standard status codes from the service (401 -> unauthorized,
 * 404 -> not found, etc.)
 */
public class IotHubConnectionStatusException extends ConnectionStatusException
{
    public IotHubConnectionStatusException()
    {
        super();
    }

    public IotHubConnectionStatusException(String message)
    {
        super(message);
    }

    public IotHubConnectionStatusException(String message, Throwable cause) { super(message, cause); }

    public IotHubConnectionStatusException(Throwable cause) { super(cause); }
}
