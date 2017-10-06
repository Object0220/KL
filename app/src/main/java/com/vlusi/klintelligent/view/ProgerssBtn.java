package com.vlusi.klintelligent.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

/**
 * Created by 吴启 on 2017/5/18.
 */
public class ProgerssBtn extends android.support.v7.widget.AppCompatButton {
    private boolean isProgressEnable = true;
    private long progressMax = 100;
    private long mCurrProgress;
    private Drawable blueBg;

    //是否允许进度
    public void setProgressEnable(boolean progressEnable) {
        isProgressEnable = progressEnable;
    }

    //设置进度的最大值
    public void setProgressMax(long progressMax) {
        this.progressMax = progressMax;
    }

    //设置当前的进度
    public void setmCurrProgress(long mProgress) {
        this.mCurrProgress = mProgress;
        //（进度值发生改变）就需要重绘进度
        invalidate();  //该方法触发onDraw方法
    }

    public ProgerssBtn(Context context) {
        this(context, null);
    }

    public ProgerssBtn(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        //之前
        if (isProgressEnable) {  //是否允许进度更新
            //canvas.drawText("wuqi",10,10,getPaint());
            if (blueBg == null) {   //只创建一次
                blueBg = new ColorDrawable(Color.GREEN);    //决定显示的颜色效果
            }
            int left = 0;
            int top = 0;
             //进度值计算 公式：  当前的进度/最大进度*
            int right = (int) (mCurrProgress*1.0/progressMax*getMeasuredWidth()+.5f);  //动态计算
            int bottom = getBottom();
            blueBg.setBounds(left, top, right, bottom);  //决定画的范围
        }

        blueBg.draw(canvas);     //绘画
        super.onDraw(canvas); //默认的绘画
        //之后

    }
}
