package com.vlusi.klintelligent.activities;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.vlusi.klintelligent.R;

import cn.jzvd.JZVideoPlayer;
import cn.jzvd.JZVideoPlayerStandard;

/**
 * 视频播放界面
 */

public class VideoPlayActivity extends AppCompatActivity {
    private String mExtraPath;
    private ImageView iv_focus;
    private ImageView iv_focus2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.vediaplayvitamio);
        iv_focus = (ImageView) findViewById(R.id.iv_focus);
        iv_focus2 = (ImageView) findViewById(R.id.iv_focus2);
        setStatusBarColor(R.color.black);
        //获取传来的url
        mExtraPath = getIntent().getStringExtra("video_path");
        JZVideoPlayerStandard jzVideoPlayerStandard = (JZVideoPlayerStandard) findViewById(R.id.videoplayer);
        jzVideoPlayerStandard.setUp(mExtraPath
                , JZVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "");
       Glide.with(getApplicationContext()).load(mExtraPath).into(jzVideoPlayerStandard.thumbImageView);
        jzVideoPlayerStandard.startVideo();


        iv_focus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        iv_focus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (JZVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        JZVideoPlayer.releaseAllVideos();
    }


    /**
     * 设置状态栏颜色
     *
     * @param resId
     */
    private void setStatusBarColor(int resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(resId));
        }
    }

}