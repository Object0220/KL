package com.vlusi.klintelligent.entities;

public class DeviceInfo {
    private int id;
    private int select_id;
    private int userId;
    private int allways;
    private String deviceId;
    private String mac;
    private String deviceName;
    private String imageName;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public int getSelect_id() {
        return select_id;
    }

    public void setSelect_id(int select_id) {
        this.select_id = select_id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getAllways() {
        return allways;
    }

    public void setAllways(int allways) {
        this.allways = allways;
    }


    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }


    public DeviceInfo() {
        super();
        // TODO Auto-generated constructor stub
    }


    public DeviceInfo(int select_id, int userId, int allways, String deviceId,
                      String mac, String deviceName, String imageName) {
        super();
        this.select_id = select_id;
        this.userId = userId;
        this.allways = allways;
        this.deviceId = deviceId;
        this.mac = mac;
        this.deviceName = deviceName;
        this.imageName = imageName;
    }

    public DeviceInfo(int id, int select_id, int userId, int allways,
                      String deviceId, String mac, String deviceName, String imageName) {
        super();
        this.id = id;
        this.select_id = select_id;
        this.userId = userId;
        this.allways = allways;
        this.deviceId = deviceId;
        this.mac = mac;
        this.deviceName = deviceName;
        this.imageName = imageName;
    }

    public DeviceInfo(int userId, int allways) {
        super();
        this.userId = userId;
        this.allways = allways;
    }

}
