package com.vlusi.klintelligent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.vlusi.klintelligent.Bean.FirmwareVersionBean;
import com.vlusi.klintelligent.R;

import java.util.List;

/**
 * 作者： 吴启  on 2017/8/27.
 * 功能：固件展示界面
 */

public class HardwareVersionAdapterList extends BaseAdapter {

    private List<FirmwareVersionBean.DataBean> data ;
    private Context context;

    public HardwareVersionAdapterList(List<FirmwareVersionBean.DataBean> data,Context context) {
        this.data = data;
        this.context=context;
    }

    @Override
    public int getCount() {
        return data != null ? data.size() : 0;
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
    public View getView(int i, View convertView, ViewGroup parent) {
        View view;
        DataViewHolder dataViewHolder;
        if (convertView==null){
            view= LayoutInflater.from(context).inflate(R.layout.list_item_hardware,null);
            dataViewHolder=new DataViewHolder();
            dataViewHolder.hardware= (TextView) view.findViewById(R.id.hardware1);
            dataViewHolder.Software= (TextView) view.findViewById(R.id.oftware1);
            dataViewHolder.desc= (TextView) view.findViewById(R.id.desc1);
            view.setTag(dataViewHolder); //  将ViewHolder 存储在View 中
        }else{
            view=convertView;
            dataViewHolder= (DataViewHolder) convertView.getTag();
        }
        String hversion = data.get(i).getHversion();
        String sversion = data.get(i).getSversion();
        String desc = data.get(i).getDesc();
        dataViewHolder.hardware.setText(hversion);
        dataViewHolder.Software.setText(sversion);
        dataViewHolder.desc.setText(desc);
        return view;
    }


    public  class DataViewHolder {
        TextView hardware;
        TextView Software;
        TextView desc;
    }

}
