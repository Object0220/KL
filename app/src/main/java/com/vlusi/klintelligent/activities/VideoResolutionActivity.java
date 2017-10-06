package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 视频分辨率设置界面
 * Created by qi022 on 2017/7/20.
 */

public class VideoResolutionActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.btsale1)
    RadioButton btsale1;
    @InjectView(R.id.Radio_1)
    RadioGroup Radio1;
    @InjectView(R.id.btsale2)
    RadioButton btsale2;
    @InjectView(R.id.Radio_2)
    RadioGroup Radio2;
    @InjectView(R.id.btsale3)
    RadioButton btsale3;
    @InjectView(R.id.Radio_3)
    RadioGroup Radio3;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.activity_video_resolution);
        ButterKnife.inject(this);
        initView();
        initCheck();
    }

    private void initView() {
        btsale1.setOnClickListener(this);
        btsale2.setOnClickListener(this);
        btsale3.setOnClickListener(this);
        ImageButton mIvBack = (ImageButton) findViewById(R.id.iv_back);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.VideoSize);
        mIvBack.setOnClickListener(this);
    }

    private void initCheck() {
        int a = SPUtil.getInt(mContext, Constant.VIDEORESOLUTION, 2);
        switch (a) {
            case 1:
                btsale1.setChecked(true);
                break;
            case 2:
                btsale2.setChecked(true);
                break;
            case 3:
                btsale3.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.btsale1:
                SPUtil.putInt(mContext, Constant.VIDEORESOLUTION, 1);
                setCaptureResult("1080p 60fps");
                finish();
                break;
            case R.id.btsale2:
                SPUtil.putInt(mContext, Constant.VIDEORESOLUTION, 2);
                setCaptureResult("720p 30fps");
                finish();
                break;
            case R.id.btsale3:
                SPUtil.putInt(mContext, Constant.VIDEORESOLUTION, 3);
                setCaptureResult("480p 30fps");
                finish();
                break;
        }
    }

    private void setCaptureResult(String result) {
        Intent data = new Intent();
        data.putExtra("result", result);
        this.setResult(0, data);
    }


    /**
     * 改变语言的切换
     */
    public void changeAppLanguage() {
        String lanString = "zh";
        //得到语言设置的返回值

        lanString = SPUtil.getString(mContext, "language", lanString);
        Configuration config = getResources().getConfiguration();//获得设置对象
        Resources resources = getResources();//获得res资源对象
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
