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
import android.widget.TextView;

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 相机分辨率设置界面
 * Created by qi022 on 2017/7/20.
 */

public class PhotoAesolutionActivity extends AppCompatActivity implements View.OnClickListener {


    @InjectView(R.id.aesolution1)
    RadioButton aesolution1;
    @InjectView(R.id.aesolution2)
    RadioButton aesolution2;
    @InjectView(R.id.aesolution3)
    RadioButton aesolution3;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.activiy_photo_aesolution);
        ButterKnife.inject(this);
        init();
    }

    private void init() {
        ImageButton mIvBack = (ImageButton) findViewById(R.id.iv_back);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.PhotoSize);
        mIvBack.setOnClickListener(this);
        aesolution1.setOnClickListener(this);
        aesolution2.setOnClickListener(this);
        aesolution3.setOnClickListener(this);
        initCheck();
    }

    private void initCheck() {
        int a = SPUtil.getInt(mContext, Constant.PHOTO_AESOLUTION, 2);
        switch (a) {
            case 1:
                aesolution1.setChecked(true);
                break;
            case 2:
                aesolution2.setChecked(true);
                break;
            case 3:
                aesolution3.setChecked(true);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.aesolution1:
                SPUtil.putInt(mContext, Constant.PHOTO_AESOLUTION,1);
                setCaptureResult("3264x2448");
                finish();
                break;
            case R.id.aesolution2:
                SPUtil.putInt(mContext, Constant.PHOTO_AESOLUTION,2);
                setCaptureResult("1920×1080");
                finish();
                break;
            case R.id.aesolution3:
                SPUtil.putInt(mContext, Constant.PHOTO_AESOLUTION,3);
                setCaptureResult("1280×720");
                finish();
                break;

        }
    }

    private void setCaptureResult(String result) {
        Intent data = new Intent();
        data.putExtra("aaa", result);
        this.setResult(1, data);
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
