package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.Locale;

/**
 * 相机界面的设置界面
 */
public class CameraSettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private TextView mTvCache;
    private TextView mTvUnitValue;
    private String TAG = "CameraSettingActivity";
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.activity_cameta_setting);
        initView();
    }

    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mTvTitle.setText(R.string.Setting);

        // 相机
        View languageSetting = findViewById(R.id.language_setting);
        TextView tvLanguage = (TextView) languageSetting.findViewById(R.id.tv_left);
        tvLanguage.setText(R.string.photo);
        languageSetting.setOnClickListener(this);

        //云台
        View cleanSetting = findViewById(R.id.clean_setting);
        TextView tvClean = (TextView) cleanSetting.findViewById(R.id.tv_left);
        tvClean.setText(R.string.cloud_deck);
        mTvCache = (TextView) cleanSetting.findViewById(R.id.tv_right);
        cleanSetting.setOnClickListener(this);

        //固件升级
        View unitSetting = findViewById(R.id.unit_setting);
        TextView tvUnit = (TextView) unitSetting.findViewById(R.id.tv_left);
        tvUnit.setText(R.string.firmware_upgrade);
        mTvUnitValue = (TextView) unitSetting.findViewById(R.id.tv_right);
        unitSetting.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
    }




    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            intent =new Intent(mContext,Camera_Activity.class);
            startActivity(intent);
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                intent =new Intent(mContext,Camera_Activity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.unit_setting:
               // Log.i(TAG, "固件升级");
                intent=new Intent(getApplicationContext(),HardwareActivity.class);
                startActivity(intent);
                break;
            case R.id.language_setting:
              //  Log.i(TAG, "相机设置");
                intent = new Intent(getApplicationContext(), CameraActivitySettings.class);
                startActivity(intent);
                break;
            case R.id.clean_setting:
               // Log.i(TAG, "云台");
                intent = new Intent(getApplicationContext(), YunnanActivitySetting.class);
                startActivity(intent);
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
