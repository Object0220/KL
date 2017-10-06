package com.vlusi.klintelligent;

import android.app.Application;
import android.content.Context;

import com.inuker.bluetooth.library.BluetoothContext;

/**
 * Created by suoyo on 2016/12/10.
 */

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        BluetoothContext.set(this);
        System.loadLibrary("opencv_java3");
    }

    public static MyApplication getInstance() {
        return instance;
    }

}
