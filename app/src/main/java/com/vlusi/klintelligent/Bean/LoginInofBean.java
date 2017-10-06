package com.vlusi.klintelligent.Bean;

/**
 * 作者： 吴启  on 2017/8/16.
 * 功能： 登陆成功返回的bean
 */

public  class  LoginInofBean {
    /**
     * code : 200
     * data : {"alias":"","app_name":"fastwheel","email":"971531794@qq.com","id":"20252","password":"364d5ac2f380bc094f7b7de33722724c","totaldistance":"0","usertoken":"20252fastwheel510001"}
     * msg : 登录成功
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
         * alias :
         * app_name : fastwheel
         * email : 971531794@qq.com
         * id : 20252
         * password : 364d5ac2f380bc094f7b7de33722724c
         * totaldistance : 0
         * usertoken : 20252fastwheel510001
         * "headimageurl": "http://q.qlogo.cn/qqapp/1106067281/4DB582CA65E927F2C418756820C90CC8/40",
         */

        private String alias; //别名
        private String app_name; //应用程序名
        private String email; //邮箱
        private String id;  //id
        private String password;  //密码
        private String totaldistance;
        private String usertoken;  //用户的token
        private String headimageurl; //图片的url

        public String getHeadimageurl() {
            return headimageurl;
        }

        public void setHeadimageurl(String headimageurl) {
            this.headimageurl = headimageurl;
        }

        public String getAlias() {
            return alias;
        }

        public void setAlias(String alias) {
            this.alias = alias;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getTotaldistance() {
            return totaldistance;
        }

        public void setTotaldistance(String totaldistance) {
            this.totaldistance = totaldistance;
        }

        public String getUsertoken() {
            return usertoken;
        }

        public void setUsertoken(String usertoken) {
            this.usertoken = usertoken;
        }
    }
}
