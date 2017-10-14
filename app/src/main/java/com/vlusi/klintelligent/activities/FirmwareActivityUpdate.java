package com.vlusi.klintelligent.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchRequest;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.search.response.SearchResponse;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.Bean.DetailItem;
import com.vlusi.klintelligent.Bean.FirmwareVersionBean;
import com.vlusi.klintelligent.MainActivity;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.adapters.ClientManager;
import com.vlusi.klintelligent.adapters.HardwareVersionAdapterList;
import com.vlusi.klintelligent.utils.CMD;
import com.vlusi.klintelligent.utils.HttpUtils;
import com.vlusi.klintelligent.utils.ReceiveDataManager;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.utils.SendDataManager;
import com.vlusi.klintelligent.utils.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;

/**
 * 固件更新界面
 * Created by 吴启 on 2017/7/12.
 */

public class FirmwareActivityUpdate extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "FirmwareActivityUpdate";
    @InjectView(R.id.pb_update_bar)
    ProgressBar pbUpdateBar;
    private ListView lvVersion;
    private ImageButton mIvBack;
    private TextView mTvTitle;
    private Context mContext;
    private String downloadurl;



    int index = 80;  //从第80个字节开始

    //蓝牙

    private String mMac;
    private UUID mCharacterNotify; //可以接收通知
    private UUID mService;   //服务

    private UUID mCharacterWriteRule; //写基地地址  升级状态
    private UUID mCharacterRead;      //读数据  升级状态
    private UUID mCharacterWriteAPP; //固件升级   升级状态

    private File filePath;
    private FirmwareVersionBean firmwareVersionBean;
    private HardwareVersionAdapterList adapterList;

    private BluetoothClient mClient;
    private ProgressDialog progressdialog;
    private int progressdSum;

    private String file1;
    private String file2;
    private String file3;
    private String file4;
    private byte[] aaa;
    private byte[] bbb;
    private byte[] ccc;
    private byte[] ddd;
    private String codeOK;
    private byte[] values;
    private int code = 0;
    private String checksum;

    private boolean control_file1 = true; //控制第一个文件的传输
    private boolean control_file2 = false;
    private boolean control_file3 = false;
    private boolean control_file4 = false;

    private int file_1_length;  //文件大小  字节个数
    private int file_2_length;
    private int file_3_length;
    private int file_4_length;

    private int file_01;  //文件大小 十进制
    private int file_02;
    private int file_03;
    private int file_04;

    private int residue_1;  //余数
    private int residue_2;
    private int residue_3;
    private int residue_4;
    private Date dt;
    private UUID servieceUUID;
    private UUID writeUUID;
    private UUID notifyUUID;
    private String sn;
    private String mac;
    private boolean isConnect =true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.firmware_activity_update);
        ButterKnife.inject(this);
        mContext = this;
        mClient = ClientManager.getClient(); //获取蓝牙管理员
        initView();
        CheckUpdates();

        mMac = getIntent().getStringExtra("mac");
        Log.i(TAG, "-----" + mMac);
        if (mMac == null) {  //   需要发送更新请求 和  确认更新
            sn = SPUtil.getString(mContext, Constant.sn);
            mac = SPUtil.getString(mContext, Constant.mac);
            String sversion = SPUtil.getString(mContext, Constant.sversion);
            String CharacterWrite = SPUtil.getString(mContext, Constant.CharacterWrite);
            String CharacterNotify = SPUtil.getString(mContext, Constant.CharacterNotify);
            if (sversion != null && CharacterWrite != null && CharacterNotify != null && mac != null) {
                servieceUUID = UUID.fromString(sversion);
                writeUUID = UUID.fromString(CharacterWrite);
                notifyUUID = UUID.fromString(CharacterNotify);
                Log.i(TAG, "-----" + sn + "  " + mac + "   " + servieceUUID + "   " + writeUUID + "   " + notifyUUID);
                Notify(mac, servieceUUID, notifyUUID);
            }

        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().unnotify(mac, servieceUUID, notifyUUID, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.i(TAG, "关闭了通知");
                }
            }
        });
    }


    private void initView() {

        mIvBack = (ImageButton) findViewById(R.id.iv_back);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        lvVersion = (ListView) findViewById(R.id.lv_version);
        mTvTitle.setText(R.string.FirmwareUpdate);
        mIvBack.setOnClickListener(this);

    }

    private void loadDate() {
        if (firmwareVersionBean.getData() != null) {
            adapterList = new HardwareVersionAdapterList(firmwareVersionBean.getData(), mContext);
            lvVersion.setAdapter(adapterList);
        }
        lvVersion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                List<FirmwareVersionBean.DataBean> data = firmwareVersionBean.getData();
                downloadurl = data.get(i).getDownloadurl();
                // showUpdateDialog();
                if (mMac == null) {
                    pbUpdateBar.setVisibility(View.VISIBLE);
                    new Thread(new DownloadApkTask()).start();  //在这里写是为了测试
                } else {
                    pbUpdateBar.setVisibility(View.VISIBLE);
                    Log.i(TAG, "得到主界面传过来的mac_update:" + mMac);
                    new Thread(new DownloadApkTask()).start();
                }
            }
        });
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }


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
        ClientManager.getClient().write(mac, servieceUUID, writeUUID,
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
                    ClientManager.getClient().write(mac, servieceUUID, writeUUID,
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
                    ClientManager.getClient().write(mac, servieceUUID, writeUUID,
                            bytes, WriteRsp);

                /*    try {
                        Thread.sleep(300);
                        searchDeviceMac(getApplicationContext());  //发现固件设备，自动连接云台
                        Log.i(TAG, "..获得了固件更新的mac地址.."+mac);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }*/

                }

            }
        }).start();

    }


    //TODO 接收服务端云台的通知
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

            if (command == CMD.Command_update && state == CMD.STATE_OK) {
                String str = ByteUtils.byteToString(bytes);
                String update = str.substring(1, str.length());
                Log.e(TAG, " 请求更新成功: " + update);
                FirmwareUpdateConfirmation();

            }

            buffer = null;
        }
    }


    /**
     * 蓝牙确认通知的的回调
     */
    private final BleWriteResponse WriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                Log.i(TAG, "    ------已经是固件升级模式-----写入成功");
               searchDeviceMac(getApplicationContext());  //发现固件设备，自动连接云台

            } else {
                //  Log.i(TAG, "    -----------写入失败");
            }
        }
    };

    /**
     * 蓝牙写的响应
     */
    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                Log.i(TAG, "    ------这是普通相应-----写入成功");
            } else {
                //  Log.i(TAG, "    -----------写入失败");
            }
        }
    };


    //TODO:启动通知
    private void startNotify(final String MAC, UUID service, final UUID uuid) {
        mClient.notify(MAC, service, uuid, mNotifyRsp);
    }


    private int progress1 = 0;
    private int progress2 = 0;
    private int progress3 = 0;
    private int progress4 = 0;
    private int progress = 0;
    private long startClock = 0;  //发送基地址 开始时间
    private long endClock = 0;  //得到通知时间  结束时间
    private long AverageTime = 0;  //每包数据的时间
    private long residueTime = 0;  //剩余总的时间

    private int num = 0;
    private final BleNotifyResponse mNotifyRsp = new BleNotifyResponse() {
        @Override
        public void onNotify(UUID service, UUID character, byte[] value) {
            String notice = ByteUtils.byteToString(value);

            if (num == 1) {
                dt = new Date();
                endClock = dt.getTime();
                Log.i(TAG, "结束的时间：" + formatTime(endClock));
                AverageTime = milliSecond(endClock - startClock);
                residueTime = Math.abs((progressdSum) * AverageTime);
                String Average = formatTime(AverageTime);
                String residue = formatTime(residueTime);
                progressdialog.setMessage("剩余时间：" + residue);
                Log.i(TAG, " 总包数  " + progressdSum + "   平均时间：" + Average + "   总的时间" + residue);
                num++;
            } else if (num == 0) {
                dt = new Date();
                startClock = dt.getTime();
                Log.i(TAG, "开始的时间：" + formatTime(startClock));
                num++;
            }

            synchronized (this) {
                codeOK = notice.substring(0, 4); //通知码
                Log.i(TAG, "codeOK：" + codeOK);
                byte[] bytes = ByteUtils.stringToBytes(codeOK);
                byte byteCode[] = new byte[2];
                byteCode[0] = bytes[1];
                byteCode[1] = bytes[0];
                code = Integer.parseInt(ByteUtils.byteToString(byteCode), 16);
                Log.i(TAG, "code：" + code);
                if (control_file4) {
                    if (code < file_04) {  //第四个
                        sendUpdateComplete();
                        Log.i(TAG, "已发送：" + code);
                    } else if (code == file_04) {
                        if (residue_4 != 0) {
                            sendUpdateResidue(residue_4);
                            Log.i(TAG, "剩下的已发送---------：" + code);
                        } else {
                            sendUpdateComplete();
                            Log.i(TAG, "发送完成：" + code);
                            sendNotify04();
                        }
                    } else {
                        sendNotify04();
                    }
                } else if (control_file3) {   //第三个文件
                    if (code < file_03 - 1) {
                        sendUpdateComplete();
                        Log.i(TAG, "已发送：" + code);
                    } else if (code == file_03 - 1) {
                        if (residue_3 != 0) {
                            sendUpdateResidue(residue_3);
                            Log.i(TAG, "剩下的已发送：" + code);
                        } else {
                            sendUpdateComplete();
                            Log.i(TAG, "已发送：" + code);
                        }
                    } else {
                        sendNotify03();
                    }
                } else if (control_file2) {
                    if (code < file_02 - 1) {  //第二个文件
                        sendUpdateComplete();
                        Log.i(TAG, "已发送：" + code);
                    } else if (code == file_02 - 1) {
                        Log.i(TAG, "第个文件的总包数" + file_02 + "");
                        if (residue_2 != 0) {
                            sendUpdateResidue(residue_2);
                            Log.i(TAG, "已发送：" + code);
                        } else {
                            sendUpdateComplete();
                            Log.i(TAG, "剩下的已发送--：" + code);
                        }
                    } else {
                        sendNotify02();
                    }
                } else if (control_file1) {
                    if (code < file_01 - 1) { //第一个文件
                        sendUpdateComplete();
                        Log.i(TAG, "已发送：" + code);
                    } else if (code == file_01 - 1) {
                        if (residue_1 != 0) {
                            sendUpdateResidue(residue_1);
                            Log.i(TAG, "剩下的已发送：" + code);
                        } else {
                            sendUpdateComplete();
                            Log.i(TAG, "已发送：" + code);
                        }
                    } else {
                        sendNotify01();
                    }
                }
            }
            progressdialog.setMessage("剩余时间：" + formatTime(residueTime - (progress * AverageTime)));
            progress = progress1 + progress2 + progress3 + progress4 + code;
            progressdialog.setProgress(progress);  //进度条的值
        }

        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                Log.i(TAG, " 通知---请求成功");
                sendBaseAddress( mac);  //发送基地地址
            } else {
                Log.i(TAG, " 通知---请求失败");
            }
        }
    };

    private void sendNotify04() {
        control_file4 = false;
        //  Log.i(TAG, "--第四个文件传输完成--");
        progress4 = code;
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

    }

    private void sendNotify03() {
        control_file3 = false;
        control_file4 = true;
        Log.i(TAG, "--第三个文件传输完成--");
        progress3 = code;
        senAddress3();//发送 第二个基地地址
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RebootNotify();
    }

    private void sendNotify02() {
        control_file2 = false;
        control_file3 = true;
        Log.i(TAG, "--第二个文件传输完成--");
        progress2 = code;
        senAddress2();//发送 第三个基地地址
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RebootNotify();
    }

    private void sendNotify01() {
        progress1 = code;
        control_file1 = false;
        control_file2 = true;
        Log.i(TAG, "--第一个文件传输完成--");
        senAddress1();//发送 第二个基地地址
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        RebootNotify(); //使能通知
    }

    /**
     * 使能通知
     */
    private void RebootNotify() {
        mClient.notify(mMac, mService, mCharacterNotify, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {

            }

            @Override
            public void onResponse(int code) {

            }
        });
    }


    /**
     * 检查更新
     */
    private void CheckUpdates() {
        String http2 = "http://app.fastwheel.com:8800/kuailun/?m=home&c=firmware&a=firmware&app_name=fastwheel&device_type=android&hversion=YUN00100&sversion=IES035";
        HttpUtils.doGet(http2, new Callback() {

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
                    //final String msg = firmwareVersionBean.getMsg();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadDate();
                        }
                    });

                }
            }
        });


    }


    private class DownloadApkTask implements Runnable {

        @Override
        public void run() {
            FileOutputStream fos = null;
            InputStream inputStream = null;
            try {
                // 1.去具体的网络接口去下载apk文件
                HttpURLConnection conn = (HttpURLConnection) new URL(downloadurl)
                        .openConnection();

                // 设置超时
                conn.setConnectTimeout(2 * 1000);
                conn.setReadTimeout(2 * 1000);
                // 新的apk文件流
                inputStream = conn.getInputStream();
                // 指定输出的apk文件,sdcard下
                File file = new File(Constant.filefolderpath,
                        "yuntai" + ".bin");
                // 写到文件中
                fos = new FileOutputStream(file);
                int len = -1;
                byte[] buffer = new byte[22024];

                // 反复的读写输入流
                while ((len = inputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    Thread.sleep(15);
                }
                // 下载完成
                filePath = file;
                //把下载的文件转换成功byte数组
                values = ReceiveDataManager.getfilePathtoByte(filePath);
                Log.i(TAG, "下载完成....跳出兑换框，是否进入升级模式");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pbUpdateBar.setVisibility(View.GONE);
                        showSafeUpdateDialog();


                    }
                });


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                StreamUtils.closeIO(inputStream);
                StreamUtils.closeIO(fos);
            }

        }
    }


    private void showSafeUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setCancelable(false);// 点击其他区域不可取消dialog
        // 设置title
        builder.setTitle("下载完成，是否进入升级模式");

        // 设置button
        builder.setNegativeButton(R.string.Cancle, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        builder.setPositiveButton(R.string.Sure, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (mMac == null) { //mMac是null 需要自己去搜索
                    isConnect=true;
                     FirmwareUpdateRequest();
                } else {
                    Log.i(TAG, "下载完成之后连接....");
                    connectDevice(mMac);
                }

            }
        });

        builder.show();
    }


    public void searchDeviceMac(final Context mContext) {

        final BluetoothClient mClient = ClientManager.getClient();
        SearchRequest request = new SearchRequest.Builder()
                .searchBluetoothLeDevice(3000, 1)  // 先扫BLE设备3次，每次3s
                .build();

        mClient.search(request, new SearchResponse() {

            @Override
            public void onSearchStarted() {
            }

            @Override
            public void onDeviceFounded(SearchResult device) {
                String address = device.getAddress();
                if (address != null) {
                    if (device.getName().contains("OTA")) {
                        ClientManager.getClient().stopSearch();
                        if (isConnect){
                            mMac=address;
                            connectDevice(address);
                            Log.i(TAG, "快获取...."+address);
                            isConnect=false;
                        }


                    }
                }
            }

            @Override
            public void onSearchStopped() {

            }

            @Override
            public void onSearchCanceled() {

            }
        });


    }


    /**
     * 建立更新固件连接
     *
     * @param mac
     */
    private void connectDevice(final String mac) {
        pbUpdateBar.setVisibility(View.VISIBLE);
        // Log.i(TAG, "正在连接");
        //蓝牙连接选项
        BleConnectOptions options = new BleConnectOptions.Builder()
                .build(); //构建
        mClient.connect(mac, options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {

                //返回的code表示操作状态，包括成功，失败或超时等，
                if (code == REQUEST_SUCCESS) {
                    //数据都存到了集合里
                    List<DetailItem> items = new ArrayList<DetailItem>();

                    List<BleGattService> services = data.getServices();
                    for (BleGattService service : services) {
                        items.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), null));
                        List<BleGattCharacter> characters = service.getCharacters();
                        for (BleGattCharacter character : characters) {
                            items.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
                        }
                    }
                    Log.i(TAG, "------------------连接成功");
                    setDataList(items,mac);
                }
            }
        });

    }

    private void setDataList(List<DetailItem> items, String mac) {
        List<UUID> uuidArrayList = new ArrayList<>();

        for (DetailItem item : items) {
            if (item.type == DetailItem.TYPE_SERVICE) {
                UUID service = item.uuid;  //服务
                uuidArrayList.add(service);
                Log.i(TAG, "----服务： " + service);
            } else {
                UUID Characteristic = item.uuid; //特征值
                uuidArrayList.add(Characteristic);
                Log.i(TAG, "----特征值： " + Characteristic);
            }
        }

        mService = uuidArrayList.get(0);       //服务
        mCharacterWriteRule = uuidArrayList.get(1); //写基地址
        mCharacterRead = uuidArrayList.get(2); //可读，用于查询可用设备的空间大小
        mCharacterWriteAPP = uuidArrayList.get(3);  //特征值   固件升级发送 传输数据
        mCharacterNotify = uuidArrayList.get(4);   //特征值   可以app端接收通知
        // Log.i(TAG, "0----" + mService);
        //  Log.i(TAG, "1----" + mCharacterWriteRule.toString());
        // Log.i(TAG, "2----" + mCharacterRead.toString());
        //  Log.i(TAG, "3----" + mCharacterWriteAPP.toString());
        //  Log.i(TAG, "4----" + mCharacterNotify.toString());
        //startNotify(mac, mService, mCharacterNotify);
        startNotify(mac, mService, mCharacterNotify);
    }


    private void sendBaseAddress(String mac) {
        byte[] values = null;
        try {
            values = ReceiveDataManager.getfilePathtoByte(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        final int perPagSize = 16;
        int index = 0;
        for (int i = 0; i < 5; i++) {
            byte[] bytes = new byte[perPagSize];
            if (i == 1) {
                System.arraycopy(values, index, bytes, 0, perPagSize);
                // Log.i(TAG, "  2  解析bin文件：" + ByteUtils.byteToString(bytes));
                String s002 = ByteUtils.byteToString(bytes);
                file1 = s002.substring(24, 32);
                //  Log.i(TAG, " 1 文件大小" + file1);
                aaa = ByteUtils.stringToBytes(file1);

                byte[] bytess = ByteUtils.stringToBytes(file1);
                byte byteCode[] = new byte[3];
                byteCode[0] = bytess[2];
                byteCode[1] = bytess[1];
                byteCode[2] = bytess[0];
                file_1_length = Integer.parseInt(ByteUtils.byteToString(byteCode), 16);//文件的所有字节
                file_01 = file_1_length / 16;  //包数
                residue_1 = file_1_length % 16; //最后一包字节数
                if (residue_1 != 0) {
                    file_01 += 1;
                }

                //  Log.i(TAG, "转换后的文件大小：" + file_01 + "   原数据：" + ByteUtils.byteToString(byteCode));
            } else if (i == 2) {
                System.arraycopy(values, index, bytes, 0, perPagSize);
                //  Log.i(TAG, "  3  解析bin文件：" + ByteUtils.byteToString(bytes));
                String s003 = ByteUtils.byteToString(bytes);
                file2 = s003.substring(24, 32);
                bbb = ByteUtils.stringToBytes(file2);
                //  Log.i(TAG, " 2 文件大小" + file2);
                byte[] bytess = ByteUtils.stringToBytes(file2);
                byte byteCode[] = new byte[3];
                byteCode[0] = bytess[2];
                byteCode[1] = bytess[1];
                byteCode[2] = bytess[0];
                file_2_length = Integer.parseInt(ByteUtils.byteToString(byteCode), 16);
                file_02 = file_2_length / 16;
                residue_2 = file_2_length % 16;
                if (residue_2 != 0) {
                    file_02 += 1;
                }
                //  Log.i(TAG, "转换后的文件大小：" + file_02 + "   原数据：" + ByteUtils.byteToString(byteCode));
            } else if (i == 3) {
                System.arraycopy(values, index, bytes, 0, perPagSize);
                //  Log.i(TAG, "  4  解析bin文件：" + ByteUtils.byteToString(bytes));
                String s004 = ByteUtils.byteToString(bytes);
                file3 = s004.substring(24, 32);
                ccc = ByteUtils.stringToBytes(file3);
                //  Log.i(TAG, " 3 文件大小" + file3);
                byte[] bytess = ByteUtils.stringToBytes(file3);
                byte byteCode[] = new byte[3];
                byteCode[0] = bytess[2];
                byteCode[1] = bytess[1];
                byteCode[2] = bytess[0];
                file_3_length = Integer.parseInt(ByteUtils.byteToString(byteCode), 16);
                file_03 = file_3_length / 16;
                residue_3 = file_3_length % 16;
                if (residue_3 != 0) {
                    file_03 += 1;
                }
                //TODO: 取余保留两位
                //  Log.i(TAG, "转换后的文件大小：" + file_03 + "   原数据：" + ByteUtils.byteToString(byteCode));

            } else if (i == 4) {
                System.arraycopy(values, index, bytes, 0, perPagSize);
                String s005 = ByteUtils.byteToString(bytes);
                file4 = s005.substring(24, 32);
                ddd = ByteUtils.stringToBytes(file4);
                //   Log.i(TAG, " 4 文件大小" + file4);
                byte[] bytess = ByteUtils.stringToBytes(file4);
                byte byteCode[] = new byte[3];
                byteCode[0] = bytess[2];
                byteCode[1] = bytess[1];
                byteCode[2] = bytess[0];
                file_4_length = Integer.parseInt(ByteUtils.byteToString(byteCode), 16);
                file_04 = file_4_length / 16;
                residue_4 = file_4_length % 16;
                if (residue_4 != 0) {
                    file_04 += 1;
                }
                // Log.i(TAG, "转换后的文件大小：" + file_04 + "   原数据：" + ByteUtils.byteToString(byteCode));
            }
            index += perPagSize;
        }

        //文件数据包的总和
        progressdSum = (5 + file_01 + file_02 + file_03 + file_04) - 6;

        // 弹出进度的dialog
        progressdialog = new ProgressDialog(this);
        progressdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressdialog.setTitle(R.string.update);
        progressdialog.setMessage("剩余时间：");
        progressdialog.setCancelable(false);
        progressdialog.show();
        progressdialog.setMax(progressdSum);



      /*  try {
            Thread.sleep(4000);
            senAddress0(); //第一个基地地址
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
*/
        senAddress0(); //第一个基地地址

        pbUpdateBar.setVisibility(View.GONE);
        RebootNotify(); //使能通知

    }


    private void senAddress0() {
        byte[] bytes = new byte[11];
        bytes[0] = 0x01;  //默认值
        bytes[1] = aaa[0];
        bytes[2] = aaa[1];
        bytes[3] = aaa[2];
        bytes[4] = aaa[3];
        bytes[5] = 0x00;
        bytes[6] = 0x00;
        bytes[7] = 0x05;
        bytes[8] = 0x10;
        bytes[9] = 0x00;
        bytes[10] = 0x04;
        Log.i(TAG, " 发送基地地址：" + ByteUtils.byteToString(bytes));
        mClient.writeNoRsp(mMac, mService, mCharacterWriteRule,
                bytes, mWriteRsp);
    }

    public void senAddress1() {
        byte[] bytes = new byte[11];
        bytes[0] = 0x01;  //默认值
        bytes[1] = bbb[0];
        bytes[2] = bbb[1];
        bytes[3] = bbb[2];
        bytes[4] = bbb[3];
        bytes[5] = 0x00;
        bytes[6] = 0x00;
        bytes[7] = 0x05;
        bytes[8] = 0x10;
        bytes[9] = 0x01;
        bytes[10] = 0x04;
        //   Log.i(TAG, " 发送基地地址：" + ByteUtils.byteToString(bytes));
        mClient.writeNoRsp(mMac, mService, mCharacterWriteRule,
                bytes, mWriteRsp);
    }

    public void senAddress2() {
        byte[] bytes = new byte[11];
        bytes[0] = 0x01;  //默认值
        bytes[1] = ccc[0];
        bytes[2] = ccc[1];
        bytes[3] = ccc[2];
        bytes[4] = ccc[3];
        bytes[5] = 0x00;
        bytes[6] = 0x00;
        bytes[7] = 0x05;
        bytes[8] = 0x10;
        bytes[9] = 0x02;
        bytes[10] = 0x04;
        // Log.i(TAG, " 发送基地地址：" + ByteUtils.byteToString(bytes));
        mClient.writeNoRsp(mMac, mService, mCharacterWriteRule,
                bytes, mWriteRsp);
    }

    public void senAddress3() {
        byte[] bytes = new byte[11];
        bytes[0] = 0x01;  //默认值
        bytes[1] = ddd[0];
        bytes[2] = ddd[1];
        bytes[3] = ddd[2];
        bytes[4] = ddd[3];
        bytes[5] = 0x00;
        bytes[6] = 0x00;
        bytes[7] = 0x05;
        bytes[8] = 0x10;
        bytes[9] = 0x03;
        bytes[10] = 0x04;
        //   Log.i(TAG, " 发送基地地址：" + ByteUtils.byteToString(bytes));
        mClient.writeNoRsp(mMac, mService, mCharacterWriteRule,
                bytes, mWriteRsp);
    }


    /**
     * 固件更新  发送完整的数
     */
    private void sendUpdateComplete() {
        final int perPagSize = 16;
        // 发送整包
        if (index < values.length) {
            byte[] bytes = new byte[perPagSize];
            System.arraycopy(values, index, bytes, 0, perPagSize);
            index += perPagSize;
            // Log.i(TAG, "当前文件的索引:" + index);
            sendUpdateData(bytes);
        }
    }

    /**
     * 固件更新   发送多余的
     * residue  余数
     */
    private void sendUpdateResidue(int residue) {
        final int perPagSize = 16;
        if (index < values.length) {
            byte[] bytes = new byte[perPagSize];
            System.arraycopy(values, index, bytes, 0, residue);
            index += residue;
            sendUpdateData(bytes);
        }
    }


    /**
     * app发送固件数据
     *
     * @param values 16个byte 固件的数据
     */
    private void sendUpdateData(final byte[] values) {
        String data = ByteUtils.byteToString(values) + "00" + codeOK;
        checksum = makeChecksum(data);
        if (checksum.length() == 1) {
            checksum = "0" + checksum;
            //     Log.i(TAG, "异或和" + checksum);
            //    Log.i(TAG, "发送固件更新的数据：" + checksum + data);
            byte[] bytes = ByteUtils.stringToBytes(checksum + data);
            mClient.writeNoRsp(mMac, mService, mCharacterWriteAPP,
                    bytes, mWriteRsp);
        } else {
            //  Log.i(TAG, checksum);
            //  Log.i(TAG, "发送固件更新的数据：" + checksum + data);
            byte[] bytes = ByteUtils.stringToBytes(checksum + data);
            mClient.writeNoRsp(mMac, mService, mCharacterWriteAPP,
                    bytes, mWriteRsp);
        }

    }


    /**
     * 毫秒转化时分秒毫秒
     */
    public static String formatTime(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuffer sb = new StringBuffer();
        if (day > 0) {
            sb.append(day + "天");
        }
        if (hour > 0) {
            sb.append(hour + "小时");
        }
        if (minute > 0) {
            sb.append(minute + "分");
        }
        if (second > 0) {
            sb.append(second + "秒");
        }
        if (milliSecond > 0) {
            sb.append(milliSecond + "毫秒");
        }
        return sb.toString();
    }


    public static Long milliSecond(Long ms) {
        Integer ss = 1000;
        Integer mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        Long minute = (ms - day * dd - hour * hh) / mi;
        Long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;
        return milliSecond;
    }

    /**
     * 异或和
     *
     * @param content
     * @return
     */
    public static String makeChecksum(String content) {
        content = change(content);
        String[] b = content.split(" ");
        int a = 0;
        for (int i = 0; i < b.length; i++) {
            a = a ^ Integer.parseInt(b[i], 16);
        }
        if (a < 10) {
            StringBuffer sb = new StringBuffer();
            sb.append("0");
            sb.append(a);
            return sb.toString();
        }
        return Integer.toHexString(a);
    }

    public static String change(String content) {
        String str = "";
        for (int i = 0; i < content.length(); i++) {
            if (i % 2 == 0) {
                str += " " + content.substring(i, i + 1);
            } else {
                str += content.substring(i, i + 1);
            }
        }
        return str.trim();
    }

   /* @Override
    protected void onDestroy() {
        super.onDestroy();
        mClient.disconnect(mMac);
    }*/
}

