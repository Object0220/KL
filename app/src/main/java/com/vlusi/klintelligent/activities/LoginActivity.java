package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.Bean.LoginInofBean;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.HttpUtils;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.utils.ShareSDKutils;
import com.vlusi.klintelligent.utils.ToastUtil;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.sharesdk.facebook.Facebook;
import cn.sharesdk.google.GooglePlus;
import cn.sharesdk.tencent.qq.QQ;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.vlusi.klintelligent.R.id.et_email;
import static com.vlusi.klintelligent.R.id.et_pwd;

/**
 * 登陆页面
 * create on 2017/1/20 15:29
 *
 * @author suoyo
 */
public class LoginActivity extends AppCompatActivity implements View.OnClickListener,
        ShareSDKutils.OnSetData {

    private String TAG = "LoginActivity";
    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private EditText mEtEmail;
    private ImageButton mIbCancelEmail;
    private EditText mEtPwd;
    private ImageButton mIbCancelPwd;
    private Button mBtnLogin;
    private TextView mTvRegister;
    private TextView mTvForgetPwd;
    private Button mBtnWeChat;
    private Button mBtnQq;
    private Button mBtnFacebook;
    private Button mBtnGoogle;

    private boolean hasEmail = false;
    private boolean hasPwd = false;

    private ShareSDKutils shareSDKutils = new ShareSDKutils();
    private LoginInofBean mLoginInof;
    private String email;
    private String psw;
    //handler
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    SPUtil.putString(mContext, Constant.alias, mLoginInof.getData().getAlias()); //别名
                    SPUtil.putString(mContext, Constant.email, mLoginInof.getData().getEmail()); //邮箱
                    SPUtil.putString(mContext, Constant.userId, mLoginInof.getData().getId()); //userid
                    SPUtil.putString(mContext, Constant.userToken, mLoginInof.getData().getUsertoken()); //Usertoken
                    SPUtil.putString(mContext, Constant.headimageurl, mLoginInof.getData().getHeadimageurl());
                  /*  Log.i(TAG,mLoginInof.getData().getUsertoken());
                    Log.i(TAG,mLoginInof.getData().getId());
                    Log.i(TAG,mLoginInof.getData().getHeadimageurl());*/

                    break;
                case 1:
                    Log.i(TAG, "登陆成功");
                    SPUtil.putString(mContext, Constant.line, "wuqi");
                    finish();
                    break;
                case 2:
                    Toast.makeText(getApplicationContext(), "邮箱未注册", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(getApplicationContext(), "密码输入错误", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;
        initView();
    }


    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mEtEmail = (EditText) findViewById(et_email);
        mIbCancelEmail = (ImageButton) findViewById(R.id.ib_cancel_email);
        mEtPwd = (EditText) findViewById(et_pwd);
        mIbCancelPwd = (ImageButton) findViewById(R.id.ib_cancel_pwd);
        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mTvRegister = (TextView) findViewById(R.id.tv_register);
        mTvForgetPwd = (TextView) findViewById(R.id.tv_forget_pwd);
        mBtnWeChat = (Button) findViewById(R.id.btn_we_chat);
        mBtnQq = (Button) findViewById(R.id.btn_qq);
        mBtnFacebook = (Button) findViewById(R.id.btn_facebook);
        mBtnGoogle = (Button) findViewById(R.id.btn_google);

        // 标题
        mTvTitle.setText(R.string.Login);
        // 账户
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    mIbCancelEmail.setVisibility(View.VISIBLE);    //删除按钮  显示
                    hasEmail = true;
                } else {
                    mIbCancelEmail.setVisibility(View.GONE);  ////删除按钮  影藏
                    hasEmail = false;
                }
                mBtnLogin.setEnabled(hasPwd && hasEmail);  //登陆按钮
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        }); /* 密码*/
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence.toString())) {
                    mIbCancelPwd.setVisibility(View.VISIBLE);
                    hasPwd = true;
                } else {
                    mIbCancelPwd.setVisibility(View.GONE);
                    hasPwd = false;
                }
                mBtnLogin.setEnabled(hasEmail && hasPwd);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mIvBack.setOnClickListener(this);
        mIbCancelEmail.setOnClickListener(this);
        mIbCancelPwd.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mBtnWeChat.setOnClickListener(this);
        mBtnQq.setOnClickListener(this);
        mBtnFacebook.setOnClickListener(this);
        mBtnGoogle.setOnClickListener(this);
        mTvRegister.setOnClickListener(this);
        mTvForgetPwd.setOnClickListener(this);

        shareSDKutils.setDataListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: /* 返回*/
                finish();
                break;
            case R.id.ib_cancel_email: /* 删除账户输入*/
                mEtEmail.setText("");
                break;
            case R.id.ib_cancel_pwd: /* 删除密码输入*/
                mEtPwd.setText("");
                break; /* 登陆*/
            case R.id.btn_login:
                submit(); /*  finish();*/
                break;
            case R.id.tv_register: /* 注册*/
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
            case R.id.tv_forget_pwd: /* 忘记密码*/
                startActivity(new Intent(mContext, ForgetPasswordActivity.class));
                break;
            case R.id.btn_we_chat:  /*微信登陆*/
                //   shareSDKutils.Login(Wechat.NAME);
                break;
            case R.id.btn_qq:  /*qq登陆*/
                shareSDKutils.Login(QQ.NAME);
                break;
            case R.id.btn_facebook:  /*facebook登陆*/
                shareSDKutils.Login(Facebook.NAME);
                break;
            case R.id.btn_google:  /*twitter登陆*/
                shareSDKutils.Login(GooglePlus.NAME);
                break;
        }
    }

    /*重写方法来得到三方登陆返回的信息*/
    @Override
    public void sendData(String id, String icon, String name, String login_type) {
        //QQ
        //Facebook
        //GooglePlus
        SubmitData(id, icon, name, login_type);
    }


    /**
     * @param id
     * @param icon
     * @param name
     * @param login_type
     */
    private void SubmitData(final String id, final String icon, final String name, final String login_type) {
        Log.e(LoginActivity.class.getSimpleName(), "userId:" + id + "\n userIcon:" + icon + "\n userName:" + name);

        String http = "http://app.fastwheel.com:8800/kuailun/index.php/Home/ThirdLogin/thirdlogin?" +
                "&app_name=" + Constant.app_name + "&device_type=" + Constant.device_type + "android&login_type=" + login_type +
                "&openid=" + id + "&headimageurl=" + icon + "&alias=" + name;
        // get请求
        HttpUtils.doGet(http, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", "登录失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();

                QQprocessData(string);


            }
        });
    }

    private void QQprocessData(String response) {
        Gson gson = new Gson();
        mLoginInof = gson.fromJson(response, LoginInofBean.class);
        int code = mLoginInof.getCode();
        if (code == 200) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    SPUtil.putString(mContext, Constant.line, "wuqi"); //登陆成功
                    SPUtil.putString(mContext, Constant.email, mLoginInof.getData().getEmail());
                    SPUtil.putString(mContext, Constant.alias, mLoginInof.getData().getAlias());
                    SPUtil.putString(mContext, Constant.headimageurl, mLoginInof.getData().getHeadimageurl());
                    SPUtil.putString(mContext, Constant.userId, mLoginInof.getData().getId());
                    SPUtil.putString(mContext, Constant.userToken, mLoginInof.getData().getUsertoken());
                    ToastUtil.showToast(LoginActivity.this, "登陆成功");
                    finish();
                }
            });
        } else if (code == 211) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(LoginActivity.this, "邮箱未注册！");
                }
            });
        } else if (code == 218) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ToastUtil.showToast(LoginActivity.this, "请将参数填写完整！");
                }
            });
        }
    }

    /*邮箱登陆信息*/
    private void submit() {
        /*账号信息*/
        email = mEtEmail.getText().toString().trim();
        psw = mEtPwd.getText().toString().trim();
        /*验证邮箱格式是否正确*/
        if (!isEmail(email)) {
            Toast.makeText(this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
            return;
        } else {
            String httt1 = "http://app.fastwheel.com:8800/kuailun?m=home&c=Login&a=login&app_name=" +
                    Constant.app_name + "&device_type=" + Constant.device_type + "&loginype=Email&email=" + email + "&password=" + psw;
            HttpUtils.doGet(httt1, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String string = response.body().string();
                    processData(string);
                }
            });
        }
    }


    //处理数据
    private void processData(String response) {
        Gson gson = new Gson();
        mLoginInof = gson.fromJson(response, LoginInofBean.class);
        int code = mLoginInof.getCode();
        if (code == 200) {
            Message ms = Message.obtain();
            ms.what = 0;
            mHandler.sendMessage(ms);
            Log.i(TAG, "请求成功");
        } else if (code == 211) {
            Message ms = Message.obtain();
            ms.what = 2;
            mHandler.sendMessage(ms);
            Log.i(TAG, "邮箱未注册");
        } else {
            Message ms = Message.obtain();
            ms.what = 3;
            mHandler.sendMessage(ms);
            Log.i(TAG, "密码输入错误");
        }
        if (mLoginInof.getMsg().equals("登录成功")) {

            Message ms = Message.obtain();
            ms.what = 1;
            mHandler.sendMessage(ms);
            Log.i(TAG, "登录成功");
        }
    }

    //邮箱验证
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(" +
                "([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }


}