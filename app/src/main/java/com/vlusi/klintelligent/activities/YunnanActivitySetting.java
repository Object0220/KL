package com.vlusi.klintelligent.activities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.adapters.ClientManager;
import com.vlusi.klintelligent.utils.CMD;
import com.vlusi.klintelligent.utils.ReceiveDataManager;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.utils.SendDataManager;

import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * 云台设置界面
 * Created by qi022 on 2017/7/10.
 */

public class YunnanActivitySetting extends Activity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    @InjectView(R.id.iv_back)
    ImageButton ivBack;
    @InjectView(R.id.tv_title)
    TextView tvTitle;
    @InjectView(R.id.tv_right)
    TextView tvRight;

    @InjectView(R.id.button1)
    RelativeLayout button1;
    @InjectView(R.id.text1)
    TextView text1;
    @InjectView(R.id.button2)
    RelativeLayout button2;
    @InjectView(R.id.text2)
    TextView text2;
    @InjectView(R.id.btsale)
    RadioButton fast; //快
    @InjectView(R.id.btcomment)
    RadioButton centre;//中
    @InjectView(R.id.bttime)
    RadioButton slow;//慢
    @InjectView(R.id.button3)
    RelativeLayout button3;
    @InjectView(R.id.text3)
    TextView text3;
    @InjectView(R.id.btsale1)
    RadioButton Horizontal_vertical; //水平竖直
    @InjectView(R.id.bttime3)
    RadioButton Free_direction;  //自由方向
    @InjectView(R.id.button4)
    RelativeLayout button4;
    @InjectView(R.id.text4)
    TextView text4;
    @InjectView(R.id.button5)
    RelativeLayout button5;
    @InjectView(R.id.text5)
    TextView text5;
    @InjectView(R.id.button6)
    RelativeLayout button6;
    @InjectView(R.id.button7)
    RelativeLayout button7;
    @InjectView(R.id.text6)
    TextView text6;
    @InjectView(R.id.button8)
    RelativeLayout button8;
    @InjectView(R.id.text7)
    TextView text7;
    @InjectView(R.id.button9)
    RelativeLayout button9;
    @InjectView(R.id.text8)
    TextView text8;
    @InjectView(R.id.button10)
    RelativeLayout button10;
    @InjectView(R.id.pitch_axis)
    Switch pitchAxis;  //俯仰轴锁定
    @InjectView(R.id.reverse)
    Switch reverse;  //反向平移
    @InjectView(R.id.pitch)
    Switch pitch;    //反向俯仰
    @InjectView(R.id.Radio_1)
    RadioGroup Radio1;
    @InjectView(R.id.Radio_2)
    RadioGroup Radio2;
    @InjectView(R.id.bt1)
    Button bt1;
    @InjectView(R.id.bt2)
    Button bt2;
    @InjectView(R.id.bt3)
    Button bt3;

    private ImageButton mIvBack;
    private TextView mTvTitle;
    private Context mContext;


    //蓝牙
    private String mMac;
    private UUID mCharacterWrite; //可以读取数据
    private UUID mCharacterNotify; //可以接收通知
    private UUID mServiece;
    private UUID mCharacterRead;
    private boolean state_1 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ynnan_setting_activity);
        ButterKnife.inject(this);
        mContext = this;
        initView();
    }


    @Override
    protected void onResume() {
        super.onResume();
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
    }


    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().unnotify(mMac, mServiece, mCharacterNotify, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.i("guanbi","关闭了通知");
                }
            }
        });
    }

    private void initView() {
        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);


        mTvTitle.setText(R.string.YunnanSetting);
        mIvBack.setOnClickListener(this);
        bt1.setOnClickListener(this);
        bt2.setOnClickListener(this);
        bt3.setOnClickListener(this);


        pitchAxis.setOnCheckedChangeListener(this);
        reverse.setOnCheckedChangeListener(this);
        pitch.setOnCheckedChangeListener(this);

        Radio1.setOnCheckedChangeListener(this);
        Radio2.setOnCheckedChangeListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt1:
                // ToastUtil.showToast(mContext,"开始校正");
                StartCorrection();
                break;
            case R.id.bt2:
                // ToastUtil.showToast(mContext,"自动校准");
                AutomaticCalibration();
                break;
            case R.id.bt3:
                //  ToastUtil.showToast(mContext,"回复云台出厂设置");
                showNormalDialog();
                break;


        }
    }

    /**
     * 显示恢复相机出厂设置对话框
     */
    private void showNormalDialog() {
        AlertDialog.Builder normalDiglog = new AlertDialog.Builder(mContext);
        normalDiglog.setMessage(R.string.ResetCamerSettings);
        normalDiglog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FactorySettings();
                finish();
            }
        });
        normalDiglog.setNegativeButton(R.string.cancel, null);
        normalDiglog.show();
    }


    /**
     * 回复云台出厂设置
     */
    private void FactorySettings() {
        //蓝牙协议
        byte[] bytes = new byte[6];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x02;     //数据的长度
        bytes[2] = 0x03;     //纯指令          从2开始
        bytes[3] = 0x01;     //固件版本
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;
        bytes[5] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("通知2", "发送的数据" + ByteUtils.byteToString(bytes));
    }

    /**
     * 自动校准
     */
    private void AutomaticCalibration() {
        //蓝牙协议
        byte[] bytes = new byte[6];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x02;     //数据的长度
        bytes[2] = 0x03;     //纯指令          从2开始
        bytes[3] = 0x33;     //开始校准
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;
        bytes[5] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("通知2", "发送的数据" + ByteUtils.byteToString(bytes));
    }

    /**
     * 开始校正
     */
    private void StartCorrection() {
        //蓝牙协议
        byte[] bytes = new byte[6];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x02;     //数据的长度
        bytes[2] = 0x03;     //纯指令          从2开始
        bytes[3] = 0x32;     //开始校准
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;
        bytes[5] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("通知2", "发送的数据" + ByteUtils.byteToString(bytes));
    }


    /**
     * 批量查询
     */
    private void BatchQuery() {
        //蓝牙协议
        byte[] bytes = new byte[6];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x02;     //数据的长度
        bytes[2] = 0x01;     //获取类           从2开始
        bytes[3] = CMD.BATCHQUERY;     //程序版本
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;
        bytes[5] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);
        Log.i("通知2", "发送的数据" + ByteUtils.byteToString(bytes));
    }


    //TODO:  设置遥感响应速度 快

    /**
     * 设置遥感响应速度 快
     */
    private void SetupSpeedFast() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x81; //设置遥感响应速度 快
        bytes[4] = 0x01;
        bytes[5] = 0x02;   // 快
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }
    //TODO:  设置遥感响应速度 中

    /**
     * 设置遥感响应速度 中
     */
    private void SetupSpeedCentre() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x81; //设置遥感响应速度
        bytes[4] = 0x01;
        bytes[5] = 0x01;   // 中
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }
    //TODO:  设置遥感响应速度 慢

    /**
     * 设置遥感响应速度 慢
     */
    private void SetupSpeedSlow() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x81; //设置遥感响应速度
        bytes[4] = 0x01;
        bytes[5] = 0x00;   // 慢
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }
    //TODO:  设置遥感应方向 水平竖直

    /**
     * 设置遥感应方向 水平竖直
     */
    private void SetupRemoteSensingHorizontal() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x82; //设置遥感应方向
        bytes[4] = 0x01;
        bytes[5] = 0x00;   // 水平竖直
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }
    //TODO:  设置遥感应方向 自由方向

    /**
     * 设置遥感应方向 自由方向
     */
    private void SetupRemoteSensingFreeDirection() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x82; //设置遥感应方向
        bytes[4] = 0x01;
        bytes[5] = 0x01;   // 自由方向
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }

    //TODO:  设置反向平移 关闭

    /**
     * 设置反向平移 关闭
     */
    private void SetupBackwardTranslationCloss() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x83; //设置反向平移
        bytes[4] = 0x01;
        bytes[5] = 0x00;   // 关闭
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }
    //TODO:  设置反向平移 开启

    /**
     * 设置反向平移 开启
     */
    private void SetupBackwardTranslationOpen() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x83; //设置反向平移
        bytes[4] = 0x01;
        bytes[5] = 0x01;   //开启
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }
    //TODO:  设置反向俯仰 关闭

    /**
     * 设置反向俯仰 关闭
     */
    private void SetupReversePitchingCloss() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x84; //设置反向俯仰
        bytes[4] = 0x01;
        bytes[5] = 0x00;   //关闭
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }
    //TODO:  设置反向俯仰 开启

    /**
     * 设置反向俯仰 开启
     */
    private void SetupReversePitchingOpen() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x84; //设置反向俯仰
        bytes[4] = 0x01;
        bytes[5] = 0x01;   //开启
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));
    }


    //设置俯仰轴锁定 关闭
    private void SetupPitchAxisClose() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x80; //设置俯仰轴锁定
        bytes[4] = 0x01;
        bytes[5] = 0x00;   //关闭
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));

    }


    //设置俯仰轴锁定 关闭
    private void SetupPitchAxisOpen() {
        //蓝牙协议
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x04;     //数据的长度
        bytes[2] = 0x02;     //设置类           从2开始
        bytes[3] = (byte) 0x80; //设置俯仰轴锁定
        bytes[4] = 0x01;
        bytes[5] = 0x01;   //打开
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("wuqi1", "发送的数据" + ByteUtils.byteToString(bytes));

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
                    Log.i("连接", " -------请求成功");
                  //    ToastUtil.showToast(getApplicationContext(), "连接成功");
                    //TODO:批量查询
                    BatchQuery();
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
            Log.e("mmmm", "解包前 " + ByteUtils.byteToString(buffer));
            //接收数据管理器--->解包     第一个参数： 数据源， 第二个参数：数据源的长度
            byte[] bytes = ReceiveDataManager.unPackage(buffer, buffer.length);
            Log.e("mmmm", "解包后 " + ByteUtils.byteToString(bytes));
            int index = 0;
            byte command = 0;
            byte head = bytes[index++];
            if (head == CMD.CMD_READ) { //获取类 0x01
                command = bytes[index++]; // -114 设置俯仰轴锁定
            } else if (head == CMD.CMD_WRITE) { //设置类
                command = bytes[index++]; //-128
            }

            byte state = bytes[index++]; //状态  1
            //Log.e("JJJJJ", " 状态" +Integer.toHexString(command)+"   "+connectState );  //72   1
            if (command == CMD.BATCHQUERY && state == CMD.STATE_OK) { //批量查询
                byte byteType = bytes[index++];
                Log.i("JJJJJ", " byteType1 " + byteType + ""); //
                if (byteType == CMD.DATATYPE_CHAR) { //char   数据类型
                    String str = ByteUtils.byteToString(bytes);
                    String str_string = str.substring(6, str.length());

                    String pitch_axis = str_string.substring(0, 4); //俯仰轴锁定
                    String operating_lever_speed = str_string.substring(4, 8); //遥感响应速度
                    String operating_lever_direction = str_string.substring(8, 12); //遥感应方向
                    String reverse_translation = str_string.substring(12, 16); //遥感方向
                    String reverse_pitch = str_string.substring(16, 20); //反向俯仰


                    //俯仰轴锁定
                    if (pitch_axis.equals("0101")) {  //打开
                        pitchAxis.setChecked(true);
                    } else if (pitch_axis.equals("0100")) {  //关闭
                        pitchAxis.setChecked(false);
                    }
                    //遥感响应速度
                    if (operating_lever_speed.equals("0100")) {
                        fast.setChecked(true);
                    } else if (operating_lever_speed.equals("0101")) {
                        centre.setChecked(true);
                    } else if (operating_lever_speed.equals("0102")) {
                        slow.setChecked(true);
                    }

                    //遥感应方向
                    if (operating_lever_direction.equals("0100")) {
                        Horizontal_vertical.setChecked(true);
                    } else if (operating_lever_direction.equals("0101")) {
                        Free_direction.setChecked(true);
                    }
                    //反向平移
                    if (reverse_translation.equals("0100")) {
                        reverse.setChecked(false);
                    } else if (reverse_translation.equals("0101")) {
                        reverse.setChecked(true);
                    }

                    //反向俯仰
                    if (reverse_pitch.equals("0100")) {
                        pitch.setChecked(false);
                    } else if (reverse_pitch.equals("0101")) {
                        pitch.setChecked(true);
                    }


                }
            } else if (command == CMD.SET_PITCH_AXIS_LOCK && state == CMD.STATE_OK) {
                byte byteType = bytes[index++];
                Log.i("JJJJJ", " byteType2 " + byteType + ""); // 7D 转换成十六进制
                if (byteType == CMD.DATATYPE_HEX) { //char   数据类型
                    String str = ByteUtils.byteToString(buffer);
                    Log.i("JJJJJ", "------------" + str + "    -----");
                }
            }

            buffer = null;
            state_1 = true;

        }
    }


    /**
     * 蓝牙写的响应
     */
    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                Log.i("TAG", "    -----------写入成功");
            } else {
                Log.i("TAG", "    -----------写入失败");

            }
        }
    };


    //Switch状态监听
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        int id = compoundButton.getId();
        switch (id) {
            case R.id.pitch_axis: //俯仰轴锁定

                if (isChecked) {
                    Log.i("wuqi1", "俯仰轴锁定开启");
                    SetupPitchAxisOpen();
                } else {
                    Log.i("wuqi1", "俯仰轴锁定关闭");
                    SetupPitchAxisClose();
                }


                break;
            case R.id.reverse:  //反向平移
                if (state_1) {
                    if (isChecked) {
                        Log.i("wuqi1", "反向平移开启");
                        SetupBackwardTranslationOpen();

                    } else {
                        Log.i("wuqi1", "反向平移关闭");
                        SetupBackwardTranslationCloss();
                    }
                }

                break;
            case R.id.pitch: //反向俯仰
                if (state_1) {
                    if (isChecked) {
                        Log.i("wuqi1", "反向俯仰开启");
                        SetupReversePitchingOpen();
                    } else {
                        Log.i("wuqi1", "反向俯仰关闭");
                        SetupReversePitchingCloss();
                    }
                    break;
                }
        }


    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.btsale:
                fast.setChecked(true);
                Log.i("WUQI", i + "快");
                SetupSpeedSlow();
                break;
            case R.id.btcomment:
                centre.setChecked(true);
                Log.i("WUQI", i + "中");
                SetupSpeedCentre();
                break;
            case R.id.bttime:
                slow.setChecked(true);
                Log.i("WUQI", i + "慢");
                SetupSpeedFast();
                break;
            case R.id.btsale1:
                Horizontal_vertical.setChecked(true);
                Log.i("WUQI", i + "水平竖直");
                SetupRemoteSensingHorizontal();
                break;
            case R.id.bttime3:
                Free_direction.setChecked(true);
                Log.i("WUQI", i + "自由方向");
                SetupRemoteSensingFreeDirection();
                break;
        }
    }
}
