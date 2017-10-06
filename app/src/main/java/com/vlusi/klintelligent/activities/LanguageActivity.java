package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.vlusi.klintelligent.MainActivity;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

/**
 * 设置语言界面
 */
public class LanguageActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private String TAG = "LanguageActivity";
    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private RadioButton mRbEnglish;
    private RadioButton mRbZhCn;
    private RadioGroup mRgLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        mContext = this;
        initView();
    }

    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mRbEnglish = (RadioButton) findViewById(R.id.rb_english);
        mRbZhCn = (RadioButton) findViewById(R.id.rb_zh_cn);

        mRgLanguage = (RadioGroup) findViewById(R.id.rg_language);

        mTvTitle.setText(R.string.Language);

        if (SPUtil.isContainKey(mContext, "language")) {

            switch (SPUtil.getString(mContext, "language")) {
                case "en":
                    mRbEnglish.setChecked(true);
                    break;
                default:
                    mRbZhCn.setChecked(true);
            }

        } else {

            SPUtil.putString(mContext, "language", "zh");
            mRbZhCn.setChecked(true);
        }
        mIvBack.setOnClickListener(this);
        mRgLanguage.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        String lanString = "zh";

        if (i == mRbEnglish.getId()) {
            lanString = "en";
            Log.i("---" + TAG, lanString);
        } else if (i == mRbZhCn.getId()) {
            lanString = "zh";
            Log.i("---" + TAG, lanString);
        }
        //TODO: 把语言切换到mainActivity
        SPUtil.putString(mContext, "language", lanString);
        finish();
        Intent intent = new Intent(LanguageActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

    }
}
