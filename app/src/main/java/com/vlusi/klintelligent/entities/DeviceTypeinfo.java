package com.vlusi.klintelligent.entities;

public class DeviceTypeinfo {
    private int id;
    private int select_id;
    private int type;//0:null,1:eva,2:ring,3,broad
    private int userId;

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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public DeviceTypeinfo(int id, int select_id, int type, int userId) {
        super();
        this.id = id;
        this.select_id = select_id;
        this.type = type;
        this.userId = userId;
    }

    public DeviceTypeinfo(int select_id, int type, int userId) {
        super();
        this.select_id = select_id;
        this.type = type;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "DeviceTypeinfo [id=" + id + ", select_id=" + select_id
                + ", type=" + type + ", userId=" + userId + "]";
    }

    public DeviceTypeinfo() {
        super();
        // TODO Auto-generated constructor stub
    }

}
