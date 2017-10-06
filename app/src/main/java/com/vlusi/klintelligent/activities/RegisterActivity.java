package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.HttpUtils;
import com.vlusi.klintelligent.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.vlusi.klintelligent.R.id.et_email;
import static com.vlusi.klintelligent.R.id.et_pwd;

/**
 * 注册界面
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private TextView mTvRight;
    private EditText mEtEmail;
    private ImageButton mIbCancelEmail;
    private EditText mEtPwd;
    private ImageButton mIbCancelPwd;
    private EditText mEtPwd2;
    private ImageButton mIbCancelPwd2;
    private Button mBtnRegister;
    private boolean hasEmail = false;
    private boolean hasPwd = false;
    private boolean hasPwd2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = this;
        initView();
    }

    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mEtEmail = (EditText) findViewById(et_email);
        mIbCancelEmail = (ImageButton) findViewById(R.id.ib_cancel_email);
        mEtPwd = (EditText) findViewById(et_pwd);
        mIbCancelPwd = (ImageButton) findViewById(R.id.ib_cancel_pwd);
        mEtPwd2 = (EditText) findViewById(R.id.et_pwd2);
        mIbCancelPwd2 = (ImageButton) findViewById(R.id.ib_cancel_pwd2);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        // int register = R.string.Register;
        mTvTitle.setText(R.string.Register);
        mTvRight.setText(R.string.Login);
        /* 邮箱*/
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    hasEmail = false;
                    mIbCancelEmail.setVisibility(View.GONE);
                } else {
                    hasEmail = true;
                    mIbCancelEmail.setVisibility(View.VISIBLE);
                }
                mBtnRegister.setEnabled(hasPwd && hasPwd2 && hasEmail);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        /* 密码*/
        mEtPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    hasPwd = false;
                    mIbCancelPwd.setVisibility(View.GONE);
                } else {
                    hasPwd = true;
                    mIbCancelPwd.setVisibility(View.VISIBLE);
                }
                mBtnRegister.setEnabled(hasEmail && hasPwd2 && hasPwd);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        /*        确认密码*/
        mEtPwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    hasPwd2 = false;
                    mIbCancelPwd2.setVisibility(View.GONE);
                } else {
                    hasPwd2 = true;
                    mIbCancelPwd2.setVisibility(View.VISIBLE);
                }
                mBtnRegister.setEnabled(hasEmail && hasPwd && hasPwd2);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mIvBack.setOnClickListener(this);
        mIbCancelEmail.setOnClickListener(this);
        mIbCancelPwd.setOnClickListener(this);
        mIbCancelPwd2.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back: // 返回
                finish();
                break;
            case R.id.ib_cancel_email: // 删除邮箱输入
                mEtEmail.setText("");
                break;
            case R.id.ib_cancel_pwd: // 删除密码输入
                mEtPwd.setText("");
                break;
            case R.id.ib_cancel_pwd2: // 删除确认密码输入
                mEtPwd2.setText("");
                break;
            case R.id.btn_register: // 注册
                submit();

                break;
            case R.id.tv_right: // 登陆

                startActivity(new Intent(mContext,
                        LoginActivity.class));
                finish();
                break;
        }
    }

    private void submit() {
        //输入的账号信息
        String email = mEtEmail.getText().toString().trim();
        String psw = mEtPwd.getText().toString().trim();
        String psw2 = mEtPwd2.getText().toString().trim();

        //验证邮箱格式是否正确
        if (!isEmail(email)) {
            Toast.makeText(this, "清输入正确的邮箱", Toast.LENGTH_SHORT).show();
            return;
        }
        //验证两次密码输入不一样
        else if (psw.equals(psw2) == false) {
            Toast.makeText(this, "两次密码输入不一样", Toast.LENGTH_SHORT).show();
            return;
        } else {
            //get请求
            HttpUtils.doGet("http://app.fastwheel.com:8800/kuailun?m=home&c=login&a=register&app_name=" + Constant.app_name + "&decive=" + Constant.device_type + "&password=" + psw + "&email=" + email
                    , new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            ToastUtil.showToast(mContext,"注册失败");
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {

                            final String string = response.body().string();
                            Log.i("TAGA",string);
                            int code1 = 20;
                            JSONObject jsonObject = null; // 转换成JSONObject对象
                            try {
                                jsonObject = new JSONObject(string);
                                code1 = jsonObject.getInt("code"); // 获取data的内容
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (code1 == 200) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(RegisterActivity.this, "注册成功");
                                    }
                                });
                                finish();

                            } else if (code1 == 201) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtil.showToast(RegisterActivity.this, "邮箱已经注册！");
                                    }
                                });
                            }
                        }
                    });
        }
    }

    //邮箱验证
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }


}
