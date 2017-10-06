package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.CodeUtil;
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

/**
 * 登陆，忘记密码界面
 */
public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private EditText mEtEmail;
    private ImageButton mIbCancelEmail;
    private EditText mEtCode;
    private ImageView mIvCode;
    private Button mBtnSubmit;
    private String mCode;
    private boolean hasEmail = false;
    private boolean hasCode = false;
    private ImageButton mIbCancelCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        mContext = this;
        initView();
    }


    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mEtEmail = (EditText) findViewById(et_email);
        mIbCancelEmail = (ImageButton) findViewById(R.id.ib_cancel_email);
        mEtCode = (EditText) findViewById(R.id.et_code);
        mIvCode = (ImageView) findViewById(R.id.iv_code);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mIbCancelCode = (ImageButton) findViewById(R.id.ib_cancel_code);

        mTvTitle.setText(R.string.account_forget_pwd);

        //        邮箱
        mEtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    mIbCancelEmail.setVisibility(View.GONE);
                    hasEmail = false;
                } else {
                    mIbCancelEmail.setVisibility(View.VISIBLE);
                    hasEmail = true;
                }
                mBtnSubmit.setEnabled(hasCode && hasEmail);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        /*        密码*/
        mEtCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence.toString())) {
                    mIbCancelCode.setVisibility(View.GONE);
                    hasCode = false;
                } else {
                    mIbCancelCode.setVisibility(View.VISIBLE);
                    hasCode = true;
                }
                mBtnSubmit.setEnabled(hasEmail && hasCode);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mIvBack.setOnClickListener(this);
        mIbCancelEmail.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mIvCode.setOnClickListener(this);
        mIbCancelCode.setOnClickListener(this);
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
            case R.id.btn_submit: // 下一步
                submit();
                break;
            case R.id.iv_code: // 图片验证码
                generateCode();
                break;
            case R.id.ib_cancel_code: // 删除验证码输入
                mEtCode.setText("");
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        generateCode();
    }

    /**
     * 生成验证码
     */
    private void generateCode() {
        mIvCode.setImageBitmap(CodeUtil.getInstance().createBitmap());
        mCode = CodeUtil.getInstance().returnCode();
    } /*忘记密码*/

    private void submit() { /* 验证码*/
        String code = mEtCode.getText().toString().trim();
        String email = mEtEmail.getText().toString().trim();
        if (!TextUtils.equals(mCode.toLowerCase(), code.toLowerCase())) {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.account_error_code));
            return;
        }
        /*验证邮箱格式是否正确*/
        else if (!isEmail(email)) {
            Toast.makeText(this, "清输入正确的邮箱", Toast.LENGTH_SHORT).show();
            return;
        } else
            HttpUtils.doGet("http://app.fastwheel.com:8800/kuailun/index.php/home/Index/sendMail?&app_name=" +
                    Constant.app_name + "&device_type=" + Constant.device_type + "&email=" + email, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String string = response.body().string();
                    int code1 = 20;
                    JSONObject jsonObject = null; /* 转换成JSONObject对象*/
                    try {
                        jsonObject = new JSONObject(string);
                        code1 = jsonObject.getInt("code"); /* 获取code的内容*/
                        if (code1 == 0) {
                            /*  Log.i("-----", "onResponse: " + string);*/
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(ForgetPasswordActivity.this, "请求成功");
                                }
                            });
                            finish();
                        } else if (code1 == 203) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(ForgetPasswordActivity.this, "邮箱未注册！");
                                }
                            });
                        } else if (code1 == 1) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(ForgetPasswordActivity.this, "邮件发送失败请重新发送");
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
    }

    //邮箱验证
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }
}
