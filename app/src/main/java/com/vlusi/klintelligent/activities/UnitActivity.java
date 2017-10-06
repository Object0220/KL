package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

/**
 * 我的设置里面单位
 */
public class UnitActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private RadioButton mRbMetric;
    private RadioButton mRbImperial;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unit);
        mContext = this;
        initView();
    }


    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mRbMetric = (RadioButton) findViewById(R.id.rb_metric);
        mRbImperial = (RadioButton) findViewById(R.id.rb_imperial);

        mTvTitle.setText(R.string.Unit);

        if (SPUtil.getBoolean(mContext, "miles", false)) {
            mRbImperial.setChecked(true);
        } else {
            mRbMetric.setChecked(true);
        }

        mRbMetric.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtil.putBoolean(mContext, "miles", !b);
            }
        });

        mRbImperial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtil.putBoolean(mContext, "miles", b);
            }
        });

        mIvBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }
}
