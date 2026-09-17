package org.curryware.controllers;

public class DeviceTokenRequest {
    private String deviceToken;
    private String deviceVendorId;
    private String userDeviceType;
    private String deviceName;
    private String vendorId;

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getDeviceVendorId() {
        return deviceVendorId;
    }

     public void setDeviceVendorId(String deviceVendorId) {
        this.deviceVendorId = deviceVendorId;
    }

    public String getUserDeviceType() {
        return userDeviceType;
    }

    public void setUserDeviceType(String userDeviceType) {
        this.userDeviceType = userDeviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }
}
