package com.vlusi.klintelligent.entities;

public class DeviceTypeSimpleInfo {


    int id;
    int deviceType;
    String deviceTypeName;

    //	String buyURL;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(int deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceTypeName() {
        return deviceTypeName;
    }

    public void setDeviceTypeName(String deviceTypeName) {
        this.deviceTypeName = deviceTypeName;
    }

    //	public String getBuyURL() {
//		return buyURL;
//	}
//	public void setBuyURL(String buyURL) {
//		this.buyURL = buyURL;
//	}
    public DeviceTypeSimpleInfo(int id, int deviceType, String deviceTypeName) {
        super();
        this.id = id;
        this.deviceType = deviceType;
        this.deviceTypeName = deviceTypeName;
//		this.buyURL = buyURL;
    }

    public DeviceTypeSimpleInfo(int deviceType, String deviceTypeName
    ) {
        super();
        this.deviceType = deviceType;
        this.deviceTypeName = deviceTypeName;
//		this.buyURL = buyURL;
    }

    @Override
    public String toString() {
        return "DeviceTypeSimpleInfo [id=" + id + ", deviceType=" + deviceType
                + ", deviceTypeName=" + deviceTypeName
                + "]";
    }


}
