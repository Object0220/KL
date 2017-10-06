package com.vlusi.klintelligent.Bean;

/**
 * 作者： 吴启  on 2017/8/24.
 * 功能：
 */

public class UpdateBean {

    /**
     * code : 200
     * data : {"desc":"8.22","devicetypename":"YUN","downloadurl":"http://app.fastwheel.com:8800/vlusi/Uploads/firmware/KL_YT_1.1.bin","hversion":"YUN00100","id":"162","is_last_version":"1","sversion":"YUN00110"}
     * msg : 请求成功!
     */

    private int code;
    private DataBean data;
    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class DataBean {
        /**
         * desc : 8.22
         * devicetypename : YUN
         * downloadurl : http://app.fastwheel.com:8800/vlusi/Uploads/firmware/KL_YT_1.1.bin
         * hversion : YUN00100
         * id : 162
         * is_last_version : 1
         * sversion : YUN00110
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
