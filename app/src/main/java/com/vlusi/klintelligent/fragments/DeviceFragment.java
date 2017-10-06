package com.vlusi.klintelligent.fragments;

import android.animation.ObjectAnimator;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.inuker.bluetooth.library.BluetoothClient;
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.listener.BluetoothStateListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.Bean.DetailItem;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.activities.AboutActivity;
import com.vlusi.klintelligent.activities.Camera_Activity;
import com.vlusi.klintelligent.activities.FirmwareActivityUpdate;
import com.vlusi.klintelligent.activities.LoginActivity;
import com.vlusi.klintelligent.activities.NickNameActivity;
import com.vlusi.klintelligent.adapters.ClientManager;
import com.vlusi.klintelligent.adapters.DeviceAdapterIist;
import com.vlusi.klintelligent.adapters.DeviceImageAdapter;
import com.vlusi.klintelligent.utils.BlueToothSearchUtils;
import com.vlusi.klintelligent.utils.SPUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.vlusi.klintelligent.R.id.spinner;

public class DeviceFragment extends Fragment implements View.OnClickListener {
    private Spinner mSpinner;
    private LinearLayout mIndicator;
    private ViewPager mViewPager;
    private int[] mImgsId;
    private ImageView mIvMy;
    private TextView mIvScan;
    private Button mBtnSubmit;
    private TextView mTvLearnMore; //了解更多
    private LinearLayout mLlChooseDevice;
    private PopupWindow mPopupWindow;
    private ImageButton mIbExit;
    private ImageView mIvRefresh;
    private DeviceAdapterIist mDeviceAdapterIist;
    private List<SearchResult> mDevices;
    private static final String TAG = "DeviceFragment";
    private ListView mListView;
    private TextView tv_camern;
    private BluetoothClient mClient;

    private String mMac;           //蓝牙地址
    private UUID mCharacterWrite;  //可以写取数据（读）
    private UUID mCharacterNotify; //可以接收通知
    private UUID mServiece;        //服务
    private UUID mCharacterRead;
    private Intent intent;
    private int hashCode;
    private boolean mConnected; //连接状态
    private BluetoothDevice mDevice;  //远程的设备
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        changeAppLanguage();
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        initView(view);
        mClient = ClientManager.getClient();  //得到蓝牙管理员
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDevices = new ArrayList<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        ClientManager.getClient().registerBluetoothStateListener(mBluetoothStateListener);
    }

    private final BluetoothStateListener mBluetoothStateListener = new BluetoothStateListener() {
        @Override
        public void onBluetoothStateChanged(boolean openOrClosed) {
        }
    };

    private void initView(View view) {
        //我的登陆
        mIvMy = (ImageView) view.findViewById(R.id.iv_my);
        //二维码扫描
        mIvScan = (TextView) view.findViewById(R.id.iv_scan);
        //连接设备
        mBtnSubmit = (Button) view.findViewById(R.id.btn_submit);
        //了解更多
        mTvLearnMore = (TextView) view.findViewById(R.id.tv_learn_more);
        //设备选择
        mLlChooseDevice = (LinearLayout) view.findViewById(R.id.ll_choose_device);
        /* 设置Spinner*/
        mSpinner = (Spinner) view.findViewById(spinner);
        /* 设置ViewPager*/
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        //指示器
        mIndicator = (LinearLayout) view.findViewById(R.id.ll_indicator);

        setSpinner();
        setViewPager();
        mIvMy.setOnClickListener(this);
        mIvScan.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mLlChooseDevice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mSpinner.performClick();
            }
        });


        initPopupWindow();
    }

    /**
     * 设置Spinner
     */
    private void setSpinner() { /* 建立数据源*/
        String[] spItems = getResources().getStringArray(R.array.device_list); /* 建立Adapter并绑定数据源*/
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item_red, spItems);
        spAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item); /* 绑定Adapter到控件*/
        mSpinner.setAdapter(spAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mViewPager.setCurrentItem(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    /**
     * 设置ViewPager
     */
    private void setViewPager() {
        float scale = getContext().getResources().getDisplayMetrics().density;
        int marginDp = (int) (8 * scale + 0.5f);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, marginDp, 0);
        for (int i = 0; i < 1; i++) {
            ImageView imageView = new ImageView(getContext());
            imageView.setLayoutParams(layoutParams);
            if (i == 0) imageView.setImageResource(R.drawable.ic_point_s);
            else imageView.setImageResource(R.drawable.ic_point_n);
            mIndicator.addView(imageView);
        }
        mImgsId = new int[]{R.drawable.img_c_one/*, R.drawable.img_f0, R.drawable.img_ring*/};
        mViewPager.setAdapter(new DeviceImageAdapter(mImgsId, getContext()));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < 1/*mIndicator.getChildCount()*/; i++)
                    if (i == position)
                        ((ImageView) mIndicator.getChildAt(i)).setImageResource(R.drawable.ic_point_s);
                    else
                        ((ImageView) mIndicator.getChildAt(i)).setImageResource(R.drawable.ic_point_n);
                mSpinner.setSelection(0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    /**
     * 蓝牙搜索界面
     * 初始化Popwindow
     */
    private void initPopupWindow() {
        View contentview = LayoutInflater.from(getContext()).inflate(R.layout.ppw_bluetooth, null, false);
        mPopupWindow = new PopupWindow();
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewPager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(contentview);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);

        tv_camern = (TextView) contentview.findViewById(R.id.tv_camern);
        mIbExit = (ImageButton) contentview.findViewById(R.id.ib_exit);
        mIvRefresh = (ImageView) contentview.findViewById(R.id.iv_refresh);
        mListView = (ListView) contentview.findViewById(R.id.list_device);
        progressBar = (ProgressBar) contentview.findViewById(R.id.progressbar);
        mListView.setDivider(null); //去listViwe下划线
        mIbExit.setOnClickListener(this);
        mIvRefresh.setOnClickListener(this);
        tv_camern.setOnClickListener(this);

    }

    private void initData() {
        // 设置背景颜色变暗
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        //消失的时候设置窗体背景变亮
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);

            }
        });

        //设置列表显示蓝牙设备listview

        mDeviceAdapterIist = new DeviceAdapterIist(getActivity(), mDevices);
        mListView.setAdapter(mDeviceAdapterIist);
        startSearch();
        mDeviceAdapterIist.setMacListener(new DeviceAdapterIist.MacListener() {
            @Override
            public void onMAC(String mac, String name) {
              if (mConnected) {  //是连接的时候就return
                  //ToastUtil.showToast(getContext(),getString(R.string.connect_Connected));
                  //mClient.disconnect(mac);
                  progressBar.setVisibility(View.GONE);
                    return;
                }
                if (mac != null) {
                    progressBar.setVisibility(View.VISIBLE);

                    if (name != null && name.contains("OTA")) {
                        hashCode = name.hashCode();
                        Intent intent = new Intent(getContext(), FirmwareActivityUpdate.class);
                        intent.putExtra("mac", mac);
                        startActivity(intent);
                    } else if (name != null && name.contains("YUN")) {
                        Log.i("连接状态：","----"+mac);
                        hashCode = name.hashCode();
                        //得到远程的设备
                        mDevice = BluetoothUtils.getRemoteDevice(mac);
                        //注册监听连接状态
                        ClientManager.getClient().registerConnectStatusListener(mDevice.getAddress(), mConnectStatusListener);
                        connectDeviceIfNeeded(mac);
                    }


                }

            }
        });
    }


    private void connectDeviceIfNeeded(String mac) {
        if (!mConnected) { // 不连接---》就连接          连接就--》不连接
            connectDevice(mac);
        }
    }


    private final BleConnectStatusListener mConnectStatusListener = new BleConnectStatusListener() {
        @Override
        public void onConnectStatusChanged(String mac, int status) {
            mMac = mac;
            mConnected = (status == STATUS_CONNECTED); //相等代表是连接状态
            Log.i("连接状态","设备界面连接状态："+mConnected);
            //connectDeviceIfNeeded(mac);
            connectDeviceState(); //如果需要连接设备
        }
    };

    private void connectDeviceState() {
        if (mConnected) {
            try {
                SPUtil.putInt(getContext(), Constant.connectState, hashCode);
                mDeviceAdapterIist.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mPopupWindow.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                SPUtil.removeKey(getContext(), Constant.connectState);
                startSearch();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void connectDevice(final String mac) {
        //蓝牙连接选项
        BleConnectOptions options = new BleConnectOptions.Builder()
                .build();
        ClientManager.getClient().connect(mDevice.getAddress(), options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {

                if (code == REQUEST_SUCCESS) {

                    List<DetailItem> items = new ArrayList<DetailItem>();

                    List<BleGattService> services = data.getServices();
                    for (BleGattService service : services) {
                        items.add(new DetailItem(DetailItem.TYPE_SERVICE, service.getUUID(), null));
                        List<BleGattCharacter> characters = service.getCharacters();
                        for (BleGattCharacter character : characters) {
                            items.add(new DetailItem(DetailItem.TYPE_CHARACTER, character.getUuid(), service.getUUID()));
                        }
                    }
                    setDataList(items, mac);
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
        mServiece = uuidArrayList.get(0);   //服务
        mCharacterWrite = uuidArrayList.get(1); //特征值   用来读数据
        mCharacterNotify = uuidArrayList.get(2);//特征值   可以接收通知
        mCharacterRead = uuidArrayList.get(3);  //特征值   用来写数据


        SPUtil.putString(getContext(), Constant.mac, mac);
        SPUtil.putString(getContext(), Constant.sversion, mServiece.toString());
        SPUtil.putString(getContext(), Constant.CharacterWrite, mCharacterWrite.toString());
        SPUtil.putString(getContext(), Constant.CharacterNotify, mCharacterNotify.toString());

        intent = new Intent(getContext(), Camera_Activity.class);
        startActivity(intent);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //连接设备
            case R.id.btn_submit:

                //检查蓝牙是否打开
                boolean bluetoothOpened = mClient.isBluetoothOpened();
                if (bluetoothOpened) {
                    submit(mViewPager.getCurrentItem(), v);
                } else {
                    mClient.openBluetooth();
                    submit(mViewPager.getCurrentItem(), v);
                }
                break;
            case R.id.iv_my:
                String state = SPUtil.getString(getContext(), Constant.line, "");    //登陆成功状态
                if ("wuqi".equals(state)) {
                    intent = new Intent(getActivity(), NickNameActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                }
                break;
            case R.id.ib_exit:
                ClientManager.getClient().stopSearch();
                mPopupWindow.dismiss();
                break;
            // 刷新
            case R.id.iv_refresh:
                startSearch();
                break;
            //直接打开相机
            case R.id.tv_camern:
                if (mMac != null) {
                    mClient.refreshCache(mMac);
                }
                intent = new Intent(getContext(), Camera_Activity.class);
                startActivity(intent);
                mPopupWindow.dismiss();
                break;
            case R.id.iv_scan: //关于
                intent = new Intent(getContext(), AboutActivity.class);
                startActivity(intent);
                break;

        }
    }

    /**
     * 开始搜索
     */
    private void startSearch() {
        mDevices.clear();
        mDeviceAdapterIist.notifyDataSetChanged();
        startAnimation();
//        搜索蓝牙设备
        BlueToothSearchUtils.searchDevice(mDevices, mDeviceAdapterIist);
    }

    /**
     * 旋转动画
     */
    private void startAnimation() {
        ObjectAnimator rotation = ObjectAnimator.ofFloat(mIvRefresh, "rotation", 0f, 360f);
        rotation.setDuration(1000); // 设置动画时长1
        rotation.setRepeatCount(2); // 设置重复次数
        rotation.start();
    }


    private void submit(int currentItem, View v) {
        switch (currentItem) {
            case 0:
                initData();
                mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case 1:
                initData();
                mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
            case 2:
                initData();
                mPopupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //如果已登录就显示图标，否则默认
        String state = SPUtil.getString(getContext(), Constant.line, "");    //登陆成功状态
        String ic_path = SPUtil.getString(getContext(), Constant.headimageurl, "");
        if ("wuqi".equals(state)) {
            // 我的
            Glide.with(getContext()).load(ic_path).into(mIvMy);
        } else {
            mIvMy.setImageResource(R.drawable.ic_my);
        }
        mPopupWindow.dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        ClientManager.getClient().stopSearch();
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
