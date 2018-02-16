package com.microsoft.azure.sdk.iot.device;

public interface IotHubConnectionStatusChangeCallback
{
    void execute(IotHubConnectionStatus status, IotHubConnectionStatusChangeReason statusChangeReason, Throwable cause, Object callbackContext);
}
