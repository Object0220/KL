package com.vlusi.klintelligent.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.inuker.bluetooth.library.search.SearchResult;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.List;
import java.util.Locale;

/**
 * Created by vlusi on 2017/5/19.
 * 蓝牙设备列表显示
 */

public class DeviceAdapterIist extends BaseAdapter {
    private List<SearchResult> mDevices;
    private Context context;
    private int connectedSate;

    public DeviceAdapterIist(Context context, List<SearchResult> list) {
        this.mDevices = list;
        this.context = context;
    }

    //自定义MAC地址接口
    public interface MacListener {
        void onMAC(String mac, String name);
    }

    //声明接口
    private MacListener macListener;

    //供外部去实现
    public void setMacListener(MacListener macListener) {
        this.macListener = macListener;
    }

    @Override
    public int getCount() {
        return mDevices != null ? mDevices.size() : 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View convertView, ViewGroup viewGroup) {

        final SearchResult device = mDevices.get(i);
        View view;
        DeviceViewHolder viewHolder;
        changeAppLanguage();
        if (convertView == null) {
            view = LayoutInflater.from(context).inflate(R.layout.list_item_device, null);
            viewHolder = new DeviceViewHolder();
            viewHolder.mTvDevice = (TextView) view.findViewById
                    (R.id.tv_device);
            viewHolder.mBtnConnect = (Button) view.findViewById
                    (R.id.btn_connect);
            view.setTag(viewHolder); //  将ViewHolder 存储在View 中
        } else {
            view = convertView;
            viewHolder = (DeviceViewHolder) convertView.getTag(); //  重新获取ViewHolder
        }
          connectedSate = SPUtil.getInt(context, Constant.connectState, 0);
        //只有从设备界面得到蓝牙的连接状态才执行下面的操作
        String name = mDevices.get(i).getName();
        if (connectedSate == name.hashCode()) { //如果是 true
            viewHolder.mBtnConnect.setText(R.string.Connected);
        }
        if (name.contains("OTA")) {
            viewHolder.mBtnConnect.setText(R.string.update_ota);
        }
        viewHolder.mTvDevice.setText(mDevices.get(i).getName());
        viewHolder.mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mac = device.getAddress();
                String name = device.getName();
                if (macListener != null) {
                    macListener.onMAC(mac, name);
                    Log.i("TAG", "                " + mac+"---"+name);
                }
            }
        });
        return view;
    }

    class DeviceViewHolder {
        TextView mTvDevice;
        Button mBtnConnect;
    }



    /**
     * 改变语言的切换
     */
    public void changeAppLanguage() {
        String lanString = "zh";
        //得到语言设置的返回值

        lanString = SPUtil.getString(context, "language", lanString);
        Configuration config = context.getResources().getConfiguration();//获得设置对象
        Resources resources = context.getResources();//获得res资源对象
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (lanString) {
            case "zh":
                config.locale = Locale.SIMPLIFIED_CHINESE; //简体中文
                break;
            case "en":
                config.locale = Locale.ENGLISH;            //英文
                break;
        }
        resources.updateConfiguration(config, dm);
    }

}