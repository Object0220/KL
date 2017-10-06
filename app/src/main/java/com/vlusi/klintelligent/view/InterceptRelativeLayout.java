package com.vlusi.klintelligent.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * 作者： 吴启  on 2017/8/8.
 * 功能： 拦截chekBox
 */

public class InterceptRelativeLayout extends RelativeLayout {
    public InterceptRelativeLayout(Context context) {
        super(context);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //拦截事件
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;   //不拦截事件，传递给下一个处理
    }


    //处理事件
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return true;   //不处理当前事件，向上回传
    }

}
