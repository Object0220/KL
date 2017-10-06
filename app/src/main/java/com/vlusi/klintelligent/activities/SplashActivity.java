package com.vlusi.klintelligent.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.MainActivity;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.HttpUtils;
import com.vlusi.klintelligent.utils.NetWorkUtils;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.utils.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 欢迎界面
 * Created by suoyo on 2016/12/14.
 */

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100:

                    break;
            }
        }
    };
    private int appVersion = 1;
    private static final int REQUEST_CODE_INSTALL = 100;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        SPUtil.removeKey(mContext,Constant.connectState);
        SPUtil.removeKey(mContext, Constant.sversion);
        SPUtil.removeKey(mContext, Constant.CharacterWrite);
        SPUtil.removeKey(mContext, Constant.CharacterNotify);
        SPUtil.removeKey(mContext, Constant.mac);
        SPUtil.removeKey(mContext,Constant.sn);
        SPUtil.removeKey(mContext,Constant.mac_updata);
        getAPPVersion();

        int apnType = NetWorkUtils.getAPNType(mContext);
        if (apnType==0){
            enter();
        }else {
            submitUpdate();

        }
    }




    private void enter() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();

            }
        }, 1000);
    }


    //点击检查更新
    public void submitUpdate() {

        HttpUtils.doGet("http://app.fastwheel.com:8800/beibaoshi/appinfo_gimbal/edition.txt", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                if (string==null){
                    return;
                }
                int NetVersion = 1;
                NetVersion = Integer.parseInt(string);
                //如果当前版本已为最新

                if (appVersion >= NetVersion) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            enter();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showSafeUpdateDialog();
                        }
                    });
                }
            }
        });
    }

    //获取app版本号
    private void getAPPVersion() {
        PackageManager pm = this.getPackageManager();//得到PackageManager对象
        try {
            PackageInfo pi = pm.getPackageInfo(this.getPackageName(), 0);//得到PackageInfo对象，封装了一些软件包的信息在里面
            appVersion = pi.versionCode;//获取清单文件中versionCode节点的值
            Log.d("appVersion", "appVersion=" + appVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void showSafeUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);// 点击其他区域不可取消dialog
        // 设置title
        builder.setTitle(R.string.VersionUpdateReminder);
        // 设置message,由服务器指定的
        builder.setMessage(R.string.CheckNewVersion);

        // 设置button
        builder.setNegativeButton(R.string.Later, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                enter();
            }
        });

        builder.setPositiveButton(R.string.UpdateNow, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloadNewApk();
            }
        });

        builder.show();
    }

    private void downloadNewApk() {
        // 弹出进度的dialog
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setCancelable(false);
        dialog.show();

        // 去网络下载
        new Thread(new DownloadApkTask(dialog)).start();
    }


    private class DownloadApkTask implements Runnable {
        private ProgressDialog dialog;

        public DownloadApkTask(ProgressDialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            FileOutputStream fos = null;
            InputStream inputStream = null;
            try {
                // 1.去具体的网络接口去下载apk文件
                String url = Constant.dawnloadAapk;//
                HttpURLConnection conn = (HttpURLConnection) new URL(url)
                        .openConnection();

                // 设置超时
                conn.setConnectTimeout(2 * 1000);
                conn.setReadTimeout(2 * 1000);

                // 新的apk文件流
                inputStream = conn.getInputStream();

                // 获得要下载文件的大小
                int contentLength = conn.getContentLength();
                dialog.setMax(contentLength);

                // 指定输出的apk文件,sdcard下
                File filePath = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".apk");
                // 写到文件中
                fos = new FileOutputStream(filePath);

                int len = -1;
                byte[] buffer = new byte[20240];

                int progress = 0;
                // 反复的读写输入流
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    progress += len;
                    // 设置进度
                    dialog.setProgress(progress);
                    Thread.sleep(10);
                }

                // 下载完成
                // dialog消失
                dialog.dismiss();
                // 提示安装
                installApk(filePath);

            } catch (IOException e) {
                e.printStackTrace();
                // notifyError("Error:101");
            } catch (InterruptedException e) {
                e.printStackTrace();
                // notifyError("Error:103");
            } finally {
                StreamUtils.closeIO(inputStream);
                StreamUtils.closeIO(fos);
            }

        }
    }


    private void installApk(File file) {
        // 发送隐式意图去安装apk
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");

        if (Build.VERSION.SDK_INT >= 24) {
            Uri apkUri = FileProvider.getUriForFile(mContext, "com.wuqi.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.setDataAndType(apkUri,
                    "application/vnd.android.package-archive");
            startActivityForResult(intent, REQUEST_CODE_INSTALL);
        } else {
            intent.setDataAndType(Uri.fromFile(file),
                    "application/vnd.android.package-archive");
            startActivityForResult(intent, REQUEST_CODE_INSTALL);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_INSTALL) {
            // 响应的是安装的请求结果
            if (resultCode == Activity.RESULT_OK) {
                // 确定
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // 取消
                Log.d("wuqi", "用户点击了取消");
                load2Home();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    private void load2Home() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // 此方法是执行在主线程中的
                Log.d("wuqi", "thread : " + Thread.currentThread().getName());
                // 结束当前页面，进入主页面
                finish();
                Intent intent = new Intent(SplashActivity.this,
                        SplashActivity.class);
                startActivity(intent);
            }
        }, 1000);

    }



}
