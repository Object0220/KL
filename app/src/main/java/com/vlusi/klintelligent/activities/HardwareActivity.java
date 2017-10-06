package com.vlusi.klintelligent.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.Bean.FirmwareVersionBean;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.adapters.ClientManager;
import com.vlusi.klintelligent.dialog.AlertDialog;
import com.vlusi.klintelligent.utils.CMD;
import com.vlusi.klintelligent.utils.HttpUtils;
import com.vlusi.klintelligent.utils.ReceiveDataManager;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.utils.SendDataManager;

import java.io.IOException;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * 作者： 吴启  on 2017/8/28.
 * 功能：
 */
public class HardwareActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "HardwareActivity";
    @InjectView(R.id.hardware)
    TextView mHardware;
    @InjectView(R.id.oftware)
    TextView mSoftware;
    @InjectView(R.id.upgrade)
    Button upgrade;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private Context mContext;
    private FirmwareVersionBean firmwareVersionBean;

    //蓝牙
    private String mMac;
    private UUID mCharacterWrite; //可以读取数据
    private UUID mCharacterNotify; //可以接收通知
    private UUID mServiece;
    private String sn;
    private String hardware1;
    private String software1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hardware);
        ButterKnife.inject(this);
        mContext = this;
        initView();

        mMac = SPUtil.getString(mContext, Constant.mac);
        String sversion = SPUtil.getString(mContext, Constant.sversion);
        String CharacterWrite = SPUtil.getString(mContext, Constant.CharacterWrite);
        String CharacterNotify = SPUtil.getString(mContext, Constant.CharacterNotify);
        if (sversion != null && CharacterWrite != null && CharacterNotify != null) {
            mServiece = UUID.fromString(sversion);
            mCharacterWrite = UUID.fromString(CharacterWrite);
            mCharacterNotify = UUID.fromString(CharacterNotify);
        }
        if (mMac != null && mServiece != null && mCharacterWrite != null) {
            Notify(mMac, mServiece, mCharacterNotify);
        }
        ReadVersion();//读取硬件版本和软件版本
    }



    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().unnotify(mMac, mServiece, mCharacterNotify, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.i(TAG,"关闭了通知");
                }
            }
        });
    }

    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvTitle.setText(R.string.FirmwareUpdate);
        mIvBack.setOnClickListener(this);
        upgrade.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;

            case R.id.upgrade:
                FirmwareUpdateRequest();
                Intent intent = new Intent(getApplication(), FirmwareActivityUpdate.class);
                startActivity(intent);

                break;
        }
    }


    //TODO:读取软件版本信息  读取硬件版本信息

    /**
     * 读取软件版本信息
     */
    private void ReadVersion() {
        //蓝牙协议
        byte[] bytes = new byte[10];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x06;     //数据的长度
        bytes[2] = 0x01;     //获取类           从2开始
        bytes[3] = (byte) 0xF0;
        bytes[4] = 0x03;
        bytes[5] = 0x70;
        bytes[6] = 0x72;
        bytes[7] = 0x73;
        bytes[8] = (byte) 0xB6;
        bytes[9] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);
        Log.i(TAG, "读取软件硬件版本信息" + ByteUtils.byteToString(bytes));
        ObtainSN();
    }


    /**
     * 获取云台sn
     */
    private void ObtainSN() {
        //蓝牙协议
        byte[] bytes = new byte[6];
        bytes[0] = CMD.STR;
        bytes[1] = 0x02;
        bytes[2] = 0x01;
        bytes[3] = CMD.SN;
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;
        bytes[5] = CMD.END;
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);
        Log.i(TAG, "发送SN指令" + ByteUtils.byteToString(bytes));
    }


    //TODO: 读取固件更新请求

    /**
     * 固件更新请求
     */
    private void FirmwareUpdateRequest() {
        //蓝牙协议
        byte[] bytes = new byte[7];
        bytes[0] = CMD.STR;
        bytes[1] = 0x03;
        bytes[2] = CMD.CMD_COMMAND; //纯指令
        bytes[3] = CMD.Command_update; //更新
        bytes[4] = CMD.Command_dev_main;
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[5] = (byte) ck;
        bytes[6] = CMD.END;
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i(TAG, "发送固件更新请求" + ByteUtils.byteToString(bytes));
    }


    //TODO: 固件更新确认

    /**
     * 固件更新确认      //AA 11 03 A6 30 0C 41 42 43 44 45 46 47 48 49 4A 4B 4D 01 CC AC
     */
    private void FirmwareUpdateConfirmation() {
        Log.e(TAG, "发送oad 升级请求命令  SN:" + sn);
        byte[] bytesSN = ByteUtils.stringToBytes(sn);
        byte[] bytes = new byte[bytesSN.length + 4 + 4 + 1];
        bytes[0] = CMD.STR;
        bytes[1] = (byte) (bytesSN.length + 4 + 1); //长度 17   0x11;
        bytes[2] = CMD.CMD_COMMAND;  //纯命令 03
        bytes[3] = CMD.Command_updateOK; //更新A6
        bytes[4] = CMD.DATATYPE_FLOW;  //数据流 30

        int ck = 0;
        bytes[5] = (byte) bytesSN.length;  //0C
        for (int i = 0; i < bytesSN.length; i++) {
            bytes[i + 6] = bytesSN[i];
        }
        bytes[bytes.length - 3] = CMD.Command_dev_main;  //0x01
        ck = SendDataManager.getCK(bytes, 2, bytesSN.length + 4 + 1);

        bytes[bytes.length - 2] = (byte) ck;
        bytes[bytes.length - 1] = CMD.END;
        Log.i(TAG, "固件更新确认：" + ByteUtils.byteToString(bytes));
        try {
            sendUpdateConfirm(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 更新确认分包处理
     *
     * @param values
     * @throws IOException
     */
    private void sendUpdateConfirm(final byte[] values) throws IOException {

        new Thread(new Runnable() {
            @Override
            public void run() {
                final int perPagSize = 20;
                final int pageNum = (values.length / perPagSize);
                int index = 0;

                // 发送整包
                for (int i = 0; i < pageNum; i++) {
                    byte[] bytes = new byte[perPagSize];
                    System.arraycopy(values, index, bytes, 0, perPagSize);
                    ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                            bytes, mWriteRsp);
                    index += perPagSize;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // 发送 分包
                if (values.length % perPagSize > 0) {
                    byte[] bytes = new byte[values.length % perPagSize];
                    System.arraycopy(values, pageNum * 20, bytes, 0,
                            values.length % perPagSize);
                    ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                            bytes, mWriteRsp);
                }

            }
        }).start();

    }


    //接收服务端云台的通知
    private void Notify(String MAC, UUID service, UUID uuid) {
        ClientManager.getClient().notify(MAC, service, uuid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {
                handlerDatae(value);
            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.i(TAG, " -------连接成功");
                }
            }
        });
    }


    //处理蓝牙的数据
    byte[] buffer;

    private void handlerDatae(byte[] value) {
        if (buffer == null) {
            buffer = new byte[0];
        }
        value = ReceiveDataManager.replaceESC(value);
        buffer = ReceiveDataManager.appendBytes(buffer, value);

        if (ReceiveDataManager.checkData(buffer)) {
            byte[] bytes = ReceiveDataManager.unPackage(buffer, buffer.length);
            int index = 0;
            byte command = 0;
            byte head = bytes[index++];
            if (head == CMD.CMD_READ) { //获取类 0x01
                command = bytes[index++]; // -114 设置俯仰轴锁定
            } else if (head == CMD.CMD_WRITE) { //设置类
                command = bytes[index++]; //-128
            }
            byte state = bytes[index++]; //状态  1

            if (command == CMD.Client && state == CMD.STATE_OK) { //查询软件硬件版本
                byte byteType = bytes[index++];
                if (byteType == CMD.CMD_READ) { //  数据流
                    String str = ByteUtils.byteToString(bytes);
                    String hardware = str.substring(20, 30);
                    String software = str.substring(40, 50);
                    String[] split2 = hardware.split("3");
                    String[] split1 = software.split("3");
                    StringBuffer sb1 = new StringBuffer();
                    for (int i = 0; i <split1.length ; i++) {
                         sb1.append(split1[i]);
                    }
                    StringBuffer sb2 = new StringBuffer();
                    for (int i = 0; i < split2.length; i++) {
                        sb2.append(split2[i]);
                    }
                    hardware1 = "YUN" + sb2.toString();
                    software1 = "YUN" + sb1.toString();
                    mHardware.setText(hardware1);
                    mSoftware.setText(software1);
                    CheckUpdates(hardware1,software1);
                }
            } else if (command == CMD.SN && state == CMD.STATE_OK) { //查询SN
                byte byteType = bytes[index++];
                if (byteType == CMD.DATATYPE_FLOW) { //  数据流
                    String str = ByteUtils.byteToString(bytes);
                    sn = str.substring(10, str.length());
                    Log.e(TAG, " 获得SN: " + sn);
                    SPUtil.putString(mContext, Constant.sn, sn);
                }
            } else if (command == CMD.Command_update && state == CMD.STATE_OK) {
                String str = ByteUtils.byteToString(bytes);
                String update = str.substring(1, str.length());
                Log.e(TAG, " 请求更新成功: " + update);
                FirmwareUpdateConfirmation();

            }

            buffer = null;
        }
    }

    /**
     * 蓝牙写的响应
     */
    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                // Log.i(TAG, "    -----------写入成功");
            } else {
                Log.i(TAG, "    -----------写入失败");
            }
        }
    };



    /**
     * 检查更新
     */
    private void CheckUpdates(String h, String s) {
        String http = "http://app.fastwheel.com:8800/kuailun/?m=home&c=firmware&a=firmware&app_name=fastwheel&device_type=android&hversion="+h+"&sversion="+s;
        HttpUtils.doGet(http, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("onFailure", "获取失败");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String string = response.body().string();
                Gson gson = new Gson();
                firmwareVersionBean = gson.fromJson(string, FirmwareVersionBean.class);

                int code = firmwareVersionBean.getCode();
                if (code == 200) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                          new AlertDialog(mContext).builder().setMsg(getString(R.string.Firmware_has_new_version)).show();
                        }
                    });

                }
            }
        });


    }


}
