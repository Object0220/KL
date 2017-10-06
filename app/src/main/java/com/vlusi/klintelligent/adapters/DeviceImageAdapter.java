package com.vlusi.klintelligent.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

/**
 * Created by suoyo on 2016/12/15.
 */

public class DeviceImageAdapter extends PagerAdapter {

    private int[] mImgsId;
    private ImageView[] mImageViews;
    private Context mContext;

    public DeviceImageAdapter(int[] imgsId, Context context) {
        mImgsId = imgsId;
        mContext = context;
        mImageViews = new ImageView[imgsId.length];

        addImageView(context);
    }

    private void addImageView(Context context) {

        for (int i = 0; i < mImgsId.length; i++) {
            ImageView imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setImageBitmap(BitmapFactory.decodeResource(mContext.getResources(), mImgsId[i]));
            mImageViews[i] = imageView;
        }

    }

    @Override
    public int getCount() {
        return mImgsId != null ? mImgsId.length : 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    /**
     * 加载下一个即将展示的Page
     *
     * @param container
     * @param position
     * @return
     */
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = mImageViews[position];
        if (imageView != null) {
            ViewParent parent = imageView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(imageView);
            }

            container.addView(imageView);
        }

        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

    }
}
