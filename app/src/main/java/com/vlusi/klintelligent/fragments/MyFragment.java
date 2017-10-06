package com.vlusi.klintelligent.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.activities.AboutActivity;
import com.vlusi.klintelligent.activities.LoginActivity;
import com.vlusi.klintelligent.activities.NickNameActivity;
import com.vlusi.klintelligent.activities.RegisterActivity;
import com.vlusi.klintelligent.activities.SettingActivity;
import com.vlusi.klintelligent.dialog.AlertDialog;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.widget.RoundImageView;

import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyFragment extends Fragment implements View.OnClickListener {


    private Context mContext;
    private TextView mTvName;
    private RoundImageView mIvAvatar;
    private Button mBtnRegister;
    private Button mBtnLogin;
    private LinearLayout mLlAccount;
    private Button mBtnSetting;
    private Button mBtnAbout;
    private Button mBtnExit;
    private SharedPreferences sp;
    SharedPreferences.Editor editor;
    private Boolean save=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        changeAppLanguage();
        mContext = getContext();
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        mIvAvatar = (RoundImageView) view.findViewById(R.id.iv_avatar);
        mIvAvatar.setOnClickListener(this);
        mBtnRegister = (Button) view.findViewById(R.id.btn_register);
        mBtnRegister.setOnClickListener(this);
        mBtnLogin = (Button) view.findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this);
        mLlAccount = (LinearLayout) view.findViewById(R.id.ll_account);
        mLlAccount.setOnClickListener(this);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mBtnSetting = (Button) view.findViewById(R.id.btn_setting);
        mBtnSetting.setOnClickListener(this);
        mBtnAbout = (Button) view.findViewById(R.id.btn_about);
        mBtnAbout.setOnClickListener(this);
        mBtnExit = (Button) view.findViewById(R.id.btn_exit);
        mBtnExit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_register: // 注册
                startActivity(new Intent(mContext,
                        RegisterActivity.class));
                break;
            case R.id.btn_login: // 登录
                startActivity(new Intent(mContext,
                        LoginActivity.class));
                break;
            case R.id.btn_setting: // 设置
                startActivity(new Intent(mContext,
                        SettingActivity.class));
                break;
            case R.id.btn_about: // 关于
                startActivity(new Intent(mContext,
                        AboutActivity.class));
                break;
            case R.id.btn_exit: // 退出

                new AlertDialog(getContext()).builder().setTitle(getString(R.string.Exit_current_account))
                        .setMsg(getString(R.string.Sure_you_want_to_exit))
                        .setPositiveButton(getString(R.string.Sure), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 每次返回界面都执行，所以每次token都能重新获取值
                                mTvName.setVisibility(View.GONE);   //用户名
                                mBtnLogin.setVisibility(View.VISIBLE); //登陆按钮
                                mBtnRegister.setVisibility(View.VISIBLE);//注册按钮
                                mBtnExit.setVisibility(View.GONE);
                                SPUtil.removeKey(mContext, Constant.line);
                                SPUtil.removeKey(mContext,Constant.headimageurl);
                                // 使用资源图片
                                mIvAvatar.setImageResource(R.drawable.img_default_avatar); //vv    图片资源
                            }
                        }).setNegativeButton(getString(R.string.Cancle), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }).show();
                break;
            case R.id.iv_avatar: // 头像
                String state = SPUtil.getString(mContext, Constant.line, "");    //登陆成功状态
                if ("wuqi".equals(state)) {
                    Intent intent = new Intent(mContext, NickNameActivity.class);
                    startActivity(intent);
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String state = SPUtil.getString(mContext, Constant.line, "");    //登陆成功状态

        if ("wuqi".equals(state)) {
            String alias = SPUtil.getString(mContext, Constant.alias);
            String email = SPUtil.getString(mContext, Constant.email);
            String headimageurl = SPUtil.getString(mContext, Constant.headimageurl);

            Log.i("WUMI",  alias+"------------"+email+"  "+headimageurl);
            if (!alias.isEmpty()){
                mTvName.setText(alias);
            }else{
                mTvName.setText(email);
            }

                Glide.with(mContext)
                        .load(headimageurl)
                        .placeholder(R.drawable.img_default_avatar).centerCrop().into(mIvAvatar);


            mTvName.setVisibility(View.VISIBLE);
            mBtnLogin.setVisibility(View.GONE);
            mBtnRegister.setVisibility(View.GONE);
            mBtnExit.setVisibility(View.VISIBLE);
        } else {
            mBtnExit.setVisibility(View.GONE);
            mTvName.setVisibility(View.GONE);
            mBtnLogin.setVisibility(View.VISIBLE);
            mBtnRegister.setVisibility(View.VISIBLE);
        }
    }



    /**
     * 改变语言的切换
     */
    public void changeAppLanguage() {
        String lanString = "zh";
        //得到语言设置的返回值

        lanString = SPUtil.getString(getContext(), "language", lanString);
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
