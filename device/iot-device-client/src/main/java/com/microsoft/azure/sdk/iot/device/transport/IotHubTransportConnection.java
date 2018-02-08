/*
 *
 *  Copyright (c) Microsoft. All rights reserved.
 *  Licensed under the MIT license. See LICENSE file in the project root for full license information.
 *
 */

package com.microsoft.azure.sdk.iot.device.transport;

import com.microsoft.azure.sdk.iot.device.IotHubMessageResult;
import com.microsoft.azure.sdk.iot.device.IotHubStatusCode;
import com.microsoft.azure.sdk.iot.device.Message;

import java.io.IOException;

public interface IotHubTransportConnection
{
    void open() throws IOException;
    void addListener(IotHubListener listener) throws IOException;
    void close() throws IOException;
    IotHubStatusCode sendMessage(Message message) throws IOException;
    IotHubStatusCode sendMessageResult(Message message, IotHubMessageResult result) throws IOException;
}
