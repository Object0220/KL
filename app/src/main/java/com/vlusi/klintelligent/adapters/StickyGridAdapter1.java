package com.vlusi.klintelligent.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vlusi.klintelligent.Bean.GridItem;
import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.vlusi.klintelligent.R;

import java.util.List;

public class StickyGridAdapter1 extends BaseAdapter implements StickyGridHeadersSimpleAdapter {

    private List<GridItem> list;
    private LayoutInflater mInflater;
    private GridView mGridView;
    private Context context;

    //自定义图片点击接口
    public interface GridItems {
        void onItem(int position, String path, int rule);
    }

    private GridItems listener;//声明接口对象

    //供外部去实现
    public void setGuoListener(GridItems listener) {
        this.listener = listener;
    }

    public StickyGridAdapter1(Context context, List<GridItem> list, GridView mGridView) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
        this.mGridView = mGridView;
        this.context = context;
    }

    @Override
    public int getCount() {
        if (list == null) {
            return 0;
        } else {
            return list.size();
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
     final ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            mViewHolder.mImageViewGridItem = (ImageView) convertView.findViewById(R.id.grid_item);
            mViewHolder.imageViewPlay = (ImageView) convertView.findViewById(R.id.iv_play);
            mViewHolder.iv_Check = (ImageView) convertView.findViewById(R.id.iv_Check);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        final GridItem gridItem = list.get(position);
        if (gridItem.getisMp4orPng()){
            //true mp4
            Glide.with(context)
                    .load(list.get(position).getPath())
                    .placeholder(R.drawable.pan_photo).centerCrop().into(mViewHolder.mImageViewGridItem);
            mViewHolder.imageViewPlay.setVisibility(View.VISIBLE);
        }else{
            // false png
            Glide.with(context)
                    .load(list.get(position).getPath())
                    .placeholder(R.drawable.pan_photo).centerCrop().into(mViewHolder.mImageViewGridItem);
            mViewHolder.imageViewPlay.setVisibility(View.GONE);
        }

        mViewHolder.mImageViewGridItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItem(position, gridItem.getPath(), 0);  //短按 0
            }
        });

        mViewHolder.mImageViewGridItem.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItem(position, gridItem.getPath(), 1);   //长按 1
                return true;
            }
        });

        return convertView;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView.findViewById(R.id.header);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        String substring = list.get(position).getTime().substring(0, 11);
        mHeaderHolder.mTextView.setText(substring);


        return convertView;
    }

    public static class ViewHolder {
        //gridItem,存放GridView里面的缩略图
        public ImageView mImageViewGridItem;
        public ImageView imageViewPlay;
        public ImageView iv_Check;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
    }

    //头的ID 用来控制 头
    @Override
    public long getHeaderId(int position) {
        return list.get(position).getSection();
    }


}
