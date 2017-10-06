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
import android.widget.TextView;

import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.DataCleanManager;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.Locale;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private TextView mTvCache;
    private TextView mTvUnitValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.activity_setting);
        initView();
    }

    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mTvTitle.setText(R.string.Setting);

        // 语言设置
        View languageSetting = findViewById(R.id.language_setting);
        TextView tvLanguage = (TextView) languageSetting.findViewById(R.id.tv_left);
        tvLanguage.setText(R.string.language);
        languageSetting.setOnClickListener(this);

        //清理缓存
        View cleanSetting = findViewById(R.id.clean_setting);
        TextView tvClean = (TextView) cleanSetting.findViewById(R.id.tv_left);
        tvClean.setText(R.string.clean_cache);
        mTvCache = (TextView) cleanSetting.findViewById(R.id.tv_right);
        cleanSetting.setOnClickListener(this);

        // 单位
        View unitSetting = findViewById(R.id.unit_setting);
        TextView tvUnit = (TextView) unitSetting.findViewById(R.id.tv_left);
        tvUnit.setText(R.string.unit);
        mTvUnitValue = (TextView) unitSetting.findViewById(R.id.tv_right);
        unitSetting.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (SPUtil.isContainKey(mContext, "miles"))
            if (SPUtil.getBoolean(mContext, "miles", false)) mTvUnitValue.setText(R.string.miles);
            else mTvUnitValue.setText(R.string.km);
        else SPUtil.putBoolean(mContext, "miles", false);
        String cacheSize = "0M";
        try {
            cacheSize = DataCleanManager.getTotalCacheSize(mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mTvCache.setText(cacheSize);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.unit_setting: // 单位
                startActivity(new Intent(mContext,
                        UnitActivity.class));
                break;
            case R.id.language_setting: // 语言设置
                startActivity(new Intent(mContext,
                        LanguageActivity.class));
                break;
            case R.id.clean_setting: // 清理缓存
                new com.vlusi.klintelligent.dialog.AlertDialog(mContext).builder()
                        .setMsg(getString(R.string.Whether_to_clear_the_cache))
                        .setPositiveButton(getString(R.string.Sure), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                DataCleanManager.clearAllCache(mContext);
                                onResume();
                            }
                        }).setNegativeButton(getString(R.string.Cancle), null
                ).setCancelable(false)
                        .show();

                break;
        }
    }


    /**
     * 改变语言的切换
     */
    public void changeAppLanguage() {
        String  lanString = SPUtil.getString(mContext, "language", "zh");
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

        SPUtil.putString(mContext,"language",lanString);
    }

}
