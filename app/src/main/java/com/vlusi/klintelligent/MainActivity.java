package com.vlusi.klintelligent;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.mob.MobSDK;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.fragments.AlbumFragment;
import com.vlusi.klintelligent.fragments.DeviceFragment;
import com.vlusi.klintelligent.fragments.MyFragment;
import com.vlusi.klintelligent.utils.CheckPermissionUtils;
import com.vlusi.klintelligent.utils.ClientManager;
import com.vlusi.klintelligent.utils.SPUtil;

import java.io.File;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private String TAG = "MainActivity";
    private Context mContext;
    private RadioGroup mRgMain;
    private DeviceFragment mDeviceFragment;
    private MyFragment mMyFragment;
    private AlbumFragment mAlbumFragment;
    String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath();
    String folder_name = "FastWheel";
    private String mac;



    static {
        System.loadLibrary("opencv_java3");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        changeAppLanguage();
        initPermission();
        CreateSDfolder();
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
        MobSDK.init(mContext, "1d94eda496f7d");

        Enteralbum();

        Log.i("MainActivity", "c++调试成功" + stringFromJNI());

    }


    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();


    //进入相册
    private void Enteralbum() {
        boolean album = getIntent().getBooleanExtra("album", false);
        if (album) {
            findViewById(R.id.rb_album).performClick();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        mac = SPUtil.getString(mContext, Constant.mac);
    }

    //初始化权限
    private void initPermission() {
        String[] permission = CheckPermissionUtils.checkPermission(this);
        if (permission.length == 0) {  //permission==0代表里面没有权限需要申请权限
            //权限都申请了
        } else {
            //申请权限
            ActivityCompat.requestPermissions(this, permission, 100);
        }
    }


    //创建文件夹
    private void CreateSDfolder() {
        Constant.filefolderpath = folder_path + File.separator + folder_name;
        File dir = new File(Constant.filefolderpath);
        if (!dir.exists()) {
            try {
                dir.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Log.e("folder", "exist");
        }
    }


    private void initView() {
        mRgMain = (RadioGroup) findViewById(R.id.rg_main);
    }

    /**
     * 初始化Fragment
     */
    private void initFragment() {
        mRgMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                resetFragment(transaction);
                switch (i) {
                    case R.id.rb_device: /* 设备*/
                        setStatusBarColor(R.color.colorPrimaryDark);
                        mDeviceFragment = (DeviceFragment) fm.findFragmentByTag("DeF");
                        if (mDeviceFragment == null) {
                            mDeviceFragment = new DeviceFragment();
                            transaction.add(R.id.fragment, mDeviceFragment, "DeF");
                        } else transaction.show(mDeviceFragment);
                        break;
                    case R.id.rb_album:/*相册*/
                        setStatusBarColor(R.color.colorPrimaryDark);
                        mAlbumFragment = (AlbumFragment) fm.findFragmentByTag("AlF");
                        if (mAlbumFragment == null) {
                            mAlbumFragment = new AlbumFragment();
                            transaction.add(R.id.fragment, mAlbumFragment, "AlF");
                        } else transaction.show(mAlbumFragment);
                        break;
                    case R.id.rb_my: /* 我的*/
                        setStatusBarColor(R.color.my_status_bar);
                        mMyFragment = (MyFragment) fm.findFragmentByTag("MyF");
                        if (mMyFragment == null) {
                            mMyFragment = new MyFragment();
                            transaction.add(R.id.fragment, mMyFragment, "MyF");
                        } else transaction.show(mMyFragment);
                        break;
                    default:
                        break;
                }
                transaction.commit();
            }
        }); /*主动触发控件*/
        findViewById(R.id.rb_device).performClick();
    }

    /**
     * 重置Fragment @param transaction
     */
    private void resetFragment(FragmentTransaction transaction) {
        if (mDeviceFragment != null) transaction.hide(mDeviceFragment);
        if (mMyFragment != null) transaction.hide(mMyFragment);
        if (mAlbumFragment != null) transaction.hide(mAlbumFragment);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
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


    /**
     * 改变语言的切换
     */
    public void changeAppLanguage() {
        String   lanString = SPUtil.getString(mContext, "language", "zh");
        Log.e("---" + TAG, lanString);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ClientManager.getClient().disconnect(mac);
    }


}
