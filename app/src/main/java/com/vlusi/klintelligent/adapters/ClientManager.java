package com.vlusi.klintelligent.adapters;

import com.inuker.bluetooth.library.BluetoothClient;
import com.vlusi.klintelligent.MyApplication;

/**
 * 蓝牙管理员
 * Created by suoyo on 2016/12/8.
 */

public class ClientManager {

    private static BluetoothClient mClient;

    public static BluetoothClient getClient() {

        if (mClient == null) {

            synchronized (ClientManager.class) {

                if (mClient == null) {

                    mClient = new BluetoothClient(MyApplication.getInstance());
                }
            }
        }

        return mClient;
    }
}
