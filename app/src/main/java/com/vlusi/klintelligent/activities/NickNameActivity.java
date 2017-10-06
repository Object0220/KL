package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.utils.HttpUtils;
import com.vlusi.klintelligent.utils.PhotoUtils;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 昵称界面
 */
public class NickNameActivity extends AppCompatActivity implements View.OnClickListener {

    private Context mContext;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private TextView mTvRight;
    private EditText mEtNickname;
    private ImageButton mIbClear;
    SharedPreferences.Editor editor;

    private TextView mSetting_nick;
    private String newNc;
    private LinearLayout mNc;//修改昵称
    private RelativeLayout mIcon;//修改图像图像
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    protected static String imgpath = "";
    private ImageView iv_tou;
    private Uri apkUri;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nick_name);
        mContext = this;
        initView();
    }

    private void initView() {
        iv_tou = (ImageView) findViewById(R.id.iv_tou);
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvRight = (TextView) findViewById(R.id.tv_right);
        mEtNickname = (EditText) findViewById(R.id.et_nickname);
        mIbClear = (ImageButton) findViewById(R.id.ib_clear);
        mIcon = (RelativeLayout) findViewById(R.id.layout_icon);
        mNc = (LinearLayout) findViewById(R.id.layout1);
        mTvTitle.setText(R.string.personal_info);
        mTvRight.setText(R.string.Sure);
        mTvRight.setTextColor(getResources().getColor(R.color.text_blue));

        mEtNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                newNc = mEtNickname.getText().toString().trim();
                if (!TextUtils.isEmpty(newNc)) {
                    mEtNickname.setSelection(newNc.length());
                    mIbClear.setVisibility(View.VISIBLE);
                } else {
                    mIbClear.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mIvBack.setOnClickListener(this);
        mIbClear.setOnClickListener(this);
        mTvRight.setOnClickListener(this);
        mIcon.setOnClickListener(this);
        mNc.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        // 如果已登录就显示图标，否则默认
        String state = SPUtil.getString(getApplicationContext(), Constant.line, "");    //登陆成功状态
        String ic_path = SPUtil.getString(getApplicationContext(), Constant.headimageurl, "");
        String alias = SPUtil.getString(mContext, Constant.alias);
        if ("wuqi".equals(state)) {
            mEtNickname.setText(alias);
            Glide.with(getApplicationContext()).load(ic_path).into(iv_tou);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back://返回
                finish();
                break;
            case R.id.ib_clear: // 清除输入
                mEtNickname.setText("");
                break;
            case R.id.tv_right: // 确定

                if ("wuqi".equals(SPUtil.getString(mContext, Constant.line))) {
                    submitNc();
                } else
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.Please_Login));
                break;
            case R.id.layout_icon://图像
                if ("wuqi".equals(SPUtil.getString(mContext, Constant.line))) {
                    submitIcon();
                } else
                    ToastUtil.showToast(getApplicationContext(), getString(R.string.Please_Login));
                break;
            case R.id.layout1://昵称
                break;
        }
    }

    //修改昵称
    private void submitNc() {
        if (TextUtils.isEmpty(newNc)) {
            ToastUtil.showToast(getApplicationContext(), getString(R.string.nickname_empty));
            return;
        }
        {
            String http1 = "http://app.fastwheel.com:8800/kuailun?m=home&c=Info&a=nickname" +
                    "&app_name=fastwheel&device_type=android&alias=" + newNc + "&userid=" +
                    SPUtil.getString(mContext, Constant.userId) + "&usertoken=" + SPUtil.getString(mContext, Constant.userToken);
            HttpUtils.doGet(http1, new Callback() {
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
                        if (code1 == 200) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    SPUtil.putString(mContext, Constant.alias, newNc);
                                }
                            });
                            finish();
                        } else if (code1 == 213) runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(NickNameActivity.this, "新昵称不能和原来的的昵称一致！");
                            }
                        });
                        else if (code1 == 207) runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(NickNameActivity.this, "昵称修改失败");
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });


        }

    }

    //修改图像
    private void submitIcon() {
//      点击弹出选择拍照或者从相册选
        if (SPUtil.getString(mContext, Constant.userToken) == null) {
            Toast.makeText(getApplicationContext(), "请登录 ", Toast.LENGTH_SHORT).show();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] Items = {"从相册选取", "拍照"};
        builder.setItems(Items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                switch (i) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照

                        File file = new File(Environment.getExternalStorageDirectory(), "image.png");
                        if (Build.VERSION.SDK_INT >= 24) {
                            apkUri = FileProvider.getUriForFile(mContext, "com.wuqi.fileprovider", file);
                        } else {
                            apkUri = Uri.fromFile(file);
                        }
                        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, apkUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }

            }
        });
        builder.setCancelable(true);
        AlertDialog dialog = builder.create();
        dialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 103 && resultCode == RESULT_OK) {
            mSetting_nick.setText(data.getStringExtra("newNick"));
        }
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case TAKE_PICTURE:
                    startPhotoZoom(apkUri);
                    break;
                case CHOOSE_PICTURE:
                    startPhotoZoom(data.getData()); //开始对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data);       //让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;

            }
        }
    }


    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    protected void startPhotoZoom(Uri uri) {
        if (uri == null) {
            Log.i("tag", "url不存在");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= 24) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }


    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data");
            String name = "image.png";
            imgpath = PhotoUtils.savePhoto(photo, Environment.getExternalStorageDirectory().getAbsolutePath(), name);
            uploadPic();
        }
    }

    //上传图片
    private void uploadPic() {
        if (imgpath != null) {
            String url = "http://app.fastwheel.com:8800/kuailun/index.php/Home/info/upload";
            Map<String, Object> map = new HashMap<>();
            map.put("app_name", Constant.app_name);
            map.put("device_type", Constant.device_type);
            map.put("userid", SPUtil.getString(mContext, Constant.userId));
            map.put("usertoken", SPUtil.getString(mContext, Constant.userToken));

            HttpUtils.doFile(url, map, new File(imgpath), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final String string = response.body().string();
                    int code1 = 20;
                    JSONObject jsonObject = null; //* 转换成JSONObject对象*//*
                    try {
                        jsonObject = new JSONObject(string);
                        code1 = jsonObject.getInt("code"); //* 获取code的内容*//*
                        if (code1 == 200) {
                            JSONObject data = new JSONObject(jsonObject.getString("data"));
//                            传递data里面图像与昵称
                            final String userIcon = data.getString("img");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.showToast(NickNameActivity.this, "请求成功");
                                    //TODO: 图片的地址
                                    SPUtil.putString(mContext, Constant.headimageurl, userIcon);


                                }
                            });
                            finish();
                        } else if (code1 == 211) runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(NickNameActivity.this, "邮箱未注册！");
                            }
                        });
                        else if (code1 == 218) runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showToast(NickNameActivity.this, "参数填写完整！");
                            }
                        });
                    } catch (
                            JSONException e)

                    {
                        e.printStackTrace();
                    }

                }
            });

        }
    }


}


