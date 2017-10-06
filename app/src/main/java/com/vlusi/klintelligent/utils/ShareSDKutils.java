package com.vlusi.klintelligent.utils;

import android.util.Log;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

/**
 * ShareSDK工具类
 * Created by 吴启 on 2017/8/10.
 */
public class ShareSDKutils {

    private static String shareOpenid;
    private static String shareUserName;
    private static String shareUserIcon;
    private static String login_type = "1";

    /**
     * 登录
     */
    public void Login(String name) {
        //根据名字不同返回不同的登陆类型
        if ("QQ".equals(name) ) {
            login_type = "1";
            Log.e("onComplete", "---------"+name);
        } else if ("Wechat".equals(name) ) {
            login_type = "2";
            Log.e("onComplete", "---------"+name);
        } else if ("Facebook".equals(name) ) {
            login_type = "3";
            Log.e("onComplete", "---------"+name);
        } else if ("GooglePlus".equals(name) ) {
            login_type = "4";
            Log.e("onComplete", "---------"+name);
        }


        Platform mPlatform = ShareSDK.getPlatform(name);//得到指定的平台

        if (mPlatform == null) {
            return;
        }
        if (mPlatform.isAuthValid()) { //身份验证
            mPlatform.removeAccount(true);
            return;
        }

        mPlatform.setPlatformActionListener(mPlatformActionListener);
        mPlatform.SSOSetting(false); ////设置false表示使用SSO授权方式
        //mPlatform.authorize();//单独授权,OnComplete返回的hashmap是空的
        mPlatform.showUser(null); //执行登录，登录后在回调里面获取用户资料

      /*  Platform.ShareParams sp =new Platform.ShareParams();
        sp.setText("测试分享的文本");
        mPlatform.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e("onComplete", "登录成功");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e("onComplete", "错误");
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e("onComplete", "取消");
            }
        }); // 设置分享事件回调
        mPlatform.share(sp);
*/

    }

    public PlatformActionListener mPlatformActionListener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {

                Log.e("onComplete", "登录成功");
                shareOpenid = platform.getDb().getUserId();
                shareUserIcon = platform.getDb().getUserIcon();
                shareUserName = platform.getDb().getUserName();
                Log.e("onComplete", platform.getDb().getUserId());/*拿到登录后的openid*/
                Log.e("onComplete", platform.getDb().getUserIcon());/*拿到登录后的图标*/
                Log.e("onComplete", platform.getDb().getUserName());/*拿到登录用户的昵称*/
                //执行方法登陆
                onSetData.sendData(shareOpenid, shareUserIcon, shareUserName, login_type);
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
           Log.e("onComplete", "登录失败" + throwable.toString());

        }

        @Override
        public void onCancel(Platform platform, int i) {
                Log.e("onCancel", "登录取消");
        }

    };




    public interface OnSetData {
        void sendData(String id, String icon, String name, String login_type);
    }
    private OnSetData onSetData;

    public void setDataListener(OnSetData onSetData) {
        this.onSetData = onSetData;
    }


}