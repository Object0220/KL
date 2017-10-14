package com.vlusi.klintelligent.utils;

import android.content.Context;
import android.util.Log;

import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.adapters.ClientManager;
import com.vlusi.klintelligent.adapters.DeviceAdapterIist;

import java.util.List;

/**
 * 蓝牙扫描工具类
 * Created 吴启 on 2017/8/19.
 */

public class BlueToothSearchUtils {


    public static void searchDevice(final List<SearchResult> mDevices, final DeviceAdapterIist mDeviceAdapterIist) {
        final BluetoothClient mClient = ClientManager.getClient();
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 2)
                .build();
        mClient.search(request, new SearchResponse() {

            @Override
            public void onSearchStarted() {
                mDevices.clear();
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                if (!mDevices.contains(device)) {

                    String address = device.getAddress();
                    if (address != null) {
                        if (device.getName().contains("OTA") || device.getName().contains("YUN")) {
                            mDevices.add(device);
                            ClientManager.getClient().stopSearch();

                        }
                    }
                    mDeviceAdapterIist.notifyDataSetChanged();
                }
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });

    }


}
