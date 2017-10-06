package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * 相机网格设置界面
 * Created by qi022 on 2017/7/20.
 */

public class CameraGridActivity extends AppCompatActivity implements View.OnClickListener {

    @InjectView(R.id.grid_1)
    RadioButton grid1;
    @InjectView(R.id.grid_2)
    RadioButton grid2;
    @InjectView(R.id.grid_3)
    RadioButton grid3;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_grid);
        ButterKnife.inject(this);
        grid1.setOnClickListener(this);
        grid2.setOnClickListener(this);
        grid3.setOnClickListener(this);
        mContext = this;
        initView();
        initCheck();
    }


    private void initView() {
        ImageButton mIvBack = (ImageButton) findViewById(R.id.iv_back);
        TextView mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText("网格");
        mIvBack.setOnClickListener(this);
    }

    private void initCheck() {
        int anInt = SPUtil.getInt(mContext, Constant.GRID_SETTING, 1);
        switch (anInt) {
            case 1:
                grid1.setChecked(true);
                break;
            case 3:
                grid2.setChecked(true);
                break;
            case 4:
                grid3.setChecked(true);
                break;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.grid_1:
                SPUtil.putInt(mContext,Constant.GRID_SETTING,1);
                finish();
                break;
            case R.id.grid_2:
                SPUtil.putInt(mContext,Constant.GRID_SETTING,3);
                finish();
                break;
            case R.id.grid_3:
                SPUtil.putInt(mContext,Constant.GRID_SETTING,4);
                finish();
                break;

        }
    }
}
