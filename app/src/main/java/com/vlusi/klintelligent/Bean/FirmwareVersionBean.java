package com.vlusi.klintelligent.Bean;

import java.util.List;

/**
 * 作者： 吴启  on 2017/8/27.
 * 功能：
 */

public class FirmwareVersionBean {


    /**
     * code : 200
     * data : [{"desc":"新电机解决空载开机震动2017.8.17","devicetypename":"YUN","downloadurl":"http://app.fastwheel.com:8800/vlusi/Uploads/firmware/KL_YT_1.15APP.bin","hversion":"YUN00100","id":"161","is_last_version":"0","sversion":"YUN00115"}]
     * msg : 有新的版本可以更新!
     */

    private int code;
    private String msg;
    private List<DataBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * desc : 新电机解决空载开机震动2017.8.17
         * devicetypename : YUN
         * downloadurl : http://app.fastwheel.com:8800/vlusi/Uploads/firmware/KL_YT_1.15APP.bin
         * hversion : YUN00100
         * id : 161
         * is_last_version : 0
         * sversion : YUN00115
         */

        private String desc;
        private String devicetypename;
        private String downloadurl;
        private String hversion;
        private String id;
        private String is_last_version;
        private String sversion;

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getDevicetypename() {
            return devicetypename;
        }

        public void setDevicetypename(String devicetypename) {
            this.devicetypename = devicetypename;
        }

        public String getDownloadurl() {
            return downloadurl;
        }

        public void setDownloadurl(String downloadurl) {
            this.downloadurl = downloadurl;
        }

        public String getHversion() {
            return hversion;
        }

        public void setHversion(String hversion) {
            this.hversion = hversion;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getIs_last_version() {
            return is_last_version;
        }

        public void setIs_last_version(String is_last_version) {
            this.is_last_version = is_last_version;
        }

        public String getSversion() {
            return sversion;
        }

        public void setSversion(String sversion) {
            this.sversion = sversion;
        }
    }
}
