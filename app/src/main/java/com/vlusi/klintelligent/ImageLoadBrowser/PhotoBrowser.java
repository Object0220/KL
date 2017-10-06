package com.vlusi.klintelligent.ImageLoadBrowser;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;

import com.vlusi.klintelligent.Bean.GridItem;
import com.vlusi.klintelligent.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： 吴启  on 2017/8/22.
 * 功能： 图片查看界面
 */

public class PhotoBrowser extends AppCompatActivity implements View.OnClickListener {

    private ViewPager vp;
    private List<GridItem> list;
    private int CurrenPosition;
    private int Currenstate = 0;
    private ViewpagerAdapter vpAdapter;
    private List<Integer> number2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // this.requestWindowFeature(Window.FEATURE_NO_TITLE);
       // this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_photo_view);
        initView();
        initData();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            initData();
        } else {
            initData();
        }

    }


    private void initView() {
        setStatusBarColor(R.color.black);
        vp = (ViewPager) findViewById(R.id.viewPager);
        ImageButton IV_Orientation = (ImageButton) findViewById(R.id.iv_Orientation);
        IV_Orientation.setOnClickListener(this);
    }

    private void initData() {
        number2 = new ArrayList<>();
        if (list == null) {
            list = (List<GridItem>) getIntent().getSerializableExtra("albumList");
            List<Fragment> Fragmentlist = new ArrayList<>();
            for (int i = 0; i < list.size(); i++) {
                Fragmentlist.add(new PhotoViewFragment(list.get(i).getPath()));
            }
            vpAdapter = new ViewpagerAdapter(getSupportFragmentManager(), Fragmentlist, null);
            CurrenPosition = getIntent().getIntExtra("pic_position", 0);
            vp.setAdapter(vpAdapter);
            vp.setCurrentItem(CurrenPosition);

        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_Orientation:
                orientation(Currenstate);
                break;
        }
    }

    private void orientation(int state) {
        switch (state) {
            case 0:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                Currenstate = 1;
                break;
            case 1:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                Currenstate = 0;
                break;
        }
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
