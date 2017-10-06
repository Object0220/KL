package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.Locale;

/**
 * 相机设置界面
 * Created by 吴启 on 2017/7/12.
 */

public class CameraActivitySettings extends AppCompatActivity implements View.OnClickListener {

    private ImageButton mIvBack;
    private TextView mTvTitle;
    private Context mContext;
    private Intent intent;

    private TextView videoResolutionText;
    private TextView photoResolutionText;
    private String photo_result;
    private String viedeo_result;
    private ImageView iv_image_grid;
    private ImageView iv_automatic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.caera_activity_settings);

        initView();
    }


    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);

        mTvTitle.setText(R.string.CameraSettings);
        mIvBack.setOnClickListener(this);
        //照片分辨率
        View Photo_resolution = findViewById(R.id.Photo_resolution);
        TextView PhotoResolutionTitle = (TextView) Photo_resolution.findViewById(R.id.tv_left);
        PhotoResolutionTitle.setText(R.string.PhotoSize);
        photoResolutionText = (TextView) Photo_resolution.findViewById(R.id.tv_right);
        photoResolutionText.setText(photo_result);
        Photo_resolution.setOnClickListener(this);
        //视频分辨率
        View Video_resolution = findViewById(R.id.Video_resolution);
        TextView VideoResolutionTitle = (TextView) Video_resolution.findViewById(R.id.tv_left);
        VideoResolutionTitle.setText(R.string.VideoSize);
        videoResolutionText = (TextView) Video_resolution.findViewById(R.id.tv_right);
        videoResolutionText.setText(viedeo_result);
        Video_resolution.setOnClickListener(this);
        //闪光灯
        View Camera_flash = findViewById(R.id.Camera_flash);
        TextView CameraFlashTitle = (TextView) Camera_flash.findViewById(R.id.tv_left);
        CameraFlashTitle.setText(R.string.FlashLamp);
        iv_automatic = (ImageView) Camera_flash.findViewById(R.id.iv_image);
        //iv_automatic.setPadding(5, 0, 5, 0);
        iv_automatic.setImageResource(R.drawable.ic_automatic_always_bright);
        Camera_flash.setOnClickListener(this);
        //网格
        View Camera_grid = findViewById(R.id.Camera_grid);
        TextView CameraGridTitle = (TextView) Camera_grid.findViewById(R.id.tv_left);
        CameraGridTitle.setText(R.string.GridLines);
        iv_image_grid = (ImageView) Camera_grid.findViewById(R.id.iv_image);
        iv_image_grid.setImageResource(R.drawable.ic_grid);
        Camera_grid.setOnClickListener(this);
        //相机恢复出厂设置
        View Camera_reset = findViewById(R.id.Camera_reset);
        TextView CameraResetTitle = (TextView) Camera_reset.findViewById(R.id.tv_left);
        CameraResetTitle.setText(R.string.ResetCamer);
        ImageView iv_right = (ImageView) Camera_reset.findViewById(R.id.iv_right);
        iv_right.setImageResource(R.drawable.ic_confirm);
        iv_right.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viedeo_result = SPUtil.getString(mContext, Constant.VIDEO_TITLE, "720p 30fps");
        videoResolutionText.setText(viedeo_result);
        photo_result = SPUtil.getString(mContext, Constant.PHOTO_TITLE, "1920x1080");
        photoResolutionText.setText(photo_result);
        int grid_setting = SPUtil.getInt(mContext, Constant.GRID_SETTING, 1);
        switch (grid_setting) {
            case 1:
                iv_image_grid.setImageResource(R.drawable.grid_close);
                break;
            case 2:
                iv_image_grid.setImageResource(R.drawable.grid_diagonal_of_mesh_line);
                break;
            case 3:
                iv_image_grid.setImageResource(R.drawable.grid_line);
                break;
            case 4:
                iv_image_grid.setImageResource(R.drawable.grid_center);
                break;
        }
        int flash_setting = SPUtil.getInt(mContext, Constant.FLASH_SETTING, 1);
        switch (flash_setting) {
            case 1:
                iv_automatic.setImageResource(R.drawable.ic_colse_always_bright);
                break;
            case 2:
                iv_automatic.setImageResource(R.drawable.ic_open_always_bright);
                break;
            case 3:
                iv_automatic.setImageResource(R.drawable.ic_automatic_always_bright);
                break;
            case 4:
                iv_automatic.setImageResource(R.drawable.ic_always_bright);
                break;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.Photo_resolution:
                intent = new Intent(mContext, PhotoAesolutionActivity.class);
                startActivityForResult(intent, 100);
                // ToastUtil.showToast(mContext, "照片分辨率");
                break;
            case R.id.Video_resolution:
                //  ToastUtil.showToast(mContext, "视频分辨率");
                intent = new Intent(mContext, VideoResolutionActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.Camera_flash:
                intent = new Intent(mContext, CameraFlashActivity.class);
                startActivity(intent);
                //  ToastUtil.showToast(mContext, "闪光灯");
                break;
            case R.id.Camera_grid:
                intent = new Intent(mContext, CameraGridActivity.class);
                startActivity(intent);
                //ToastUtil.showToast(mContext, "网格");
                break;
            case R.id.iv_right:
                // ToastUtil.showToast(mContext, "恢复相机默认设置");
                showNormalDialog();
                break;

        }
    }

    /**
     * 显示恢复相机出厂设置对话框
     */
    private void showNormalDialog() {
        AlertDialog.Builder normalDiglog = new AlertDialog.Builder(mContext);
        normalDiglog.setMessage(R.string.ResetCamerSettings);
        normalDiglog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //TODO:重置相机
                SPUtil.clearAll(mContext);
                finish();
            }
        });
        normalDiglog.setNegativeButton(R.string.cancel, null);
        normalDiglog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == 1) {
                if (data != null) {
                    photo_result = data.getStringExtra("aaa");
                    SPUtil.putString(mContext, Constant.PHOTO_TITLE, photo_result);
                }
            }
            if (resultCode == 0) {
                if (data != null) {
                    viedeo_result = data.getStringExtra("result");
                    SPUtil.putString(mContext, Constant.VIDEO_TITLE, viedeo_result);
                }
            }


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

