package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 闪光灯设置界面
 * Created by qi022 on 2017/7/20.
 */

public class CameraFlashActivity extends AppCompatActivity implements View.OnClickListener {


    @InjectView(R.id.rb_1)
    RadioButton rb1;
    @InjectView(R.id.rb_2)
    RadioButton rb2;
    @InjectView(R.id.rb_3)
    RadioButton rb3;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.activity_camera_flash);
        ButterKnife.inject(this);
        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        initView();
        initCheked();
    }


    private void initView() {
        ImageButton mIvBack = (ImageButton) findViewById(R.id.iv_back);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.FlashLamp);
        mIvBack.setOnClickListener(this);
    }

    private void initCheked() {
        int anInt = SPUtil.getInt(mContext, Constant.FLASH_SETTING, 1);
        switch (anInt) {
            case 1:
                rb1.setChecked(true);
                break;
            case 2:
                rb2.setChecked(true);
                break;
            case 3:
                rb3.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rb_1:
                SPUtil.putInt(mContext, Constant.FLASH_SETTING,1);
                finish();
                break;
            case R.id.rb_2:
                SPUtil.putInt(mContext, Constant.FLASH_SETTING,2);
                finish();
                break;
            case R.id.rb_3:
                SPUtil.putInt(mContext, Constant.FLASH_SETTING,3);
                finish();
                break;
        }
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
