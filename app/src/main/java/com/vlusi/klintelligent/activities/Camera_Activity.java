package com.vlusi.klintelligent.activities;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener;
import com.inuker.bluetooth.library.connect.options.BleConnectOptions;
import com.inuker.bluetooth.library.connect.response.BleConnectResponse;
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleUnnotifyResponse;
import com.inuker.bluetooth.library.connect.response.BleWriteResponse;
import com.inuker.bluetooth.library.model.BleGattCharacter;
import com.inuker.bluetooth.library.model.BleGattProfile;
import com.inuker.bluetooth.library.model.BleGattService;
import com.inuker.bluetooth.library.search.SearchResult;
import com.inuker.bluetooth.library.utils.BluetoothUtils;
import com.inuker.bluetooth.library.utils.ByteUtils;
import com.vlusi.klintelligent.Bean.Constant;
import com.vlusi.klintelligent.Bean.DetailItem;
import com.vlusi.klintelligent.Camera.CameraPreview;
import com.vlusi.klintelligent.Camera.FocusImageView;
import com.vlusi.klintelligent.Camera.FoucsView;
import com.vlusi.klintelligent.MainActivity;
import com.vlusi.klintelligent.R;
import com.vlusi.klintelligent.adapters.ClientManager;
import com.vlusi.klintelligent.adapters.DeviceAdapterIist;
import com.vlusi.klintelligent.nativeJni.NdkProc;
import com.vlusi.klintelligent.utils.BlueToothSearchUtils;
import com.vlusi.klintelligent.utils.CMD;
import com.vlusi.klintelligent.utils.CameraUtil;
import com.vlusi.klintelligent.utils.MessageEvent;
import com.vlusi.klintelligent.utils.ReceiveDataManager;
import com.vlusi.klintelligent.utils.SPUtil;
import com.vlusi.klintelligent.utils.SendDataManager;
import com.vlusi.klintelligent.utils.ToastUtil;
import com.vlusi.klintelligent.view.GridViewLine.CameraLine;
import com.vlusi.klintelligent.view.ProgerssBtn;

import org.greenrobot.eventbus.EventBus;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS;
import static com.inuker.bluetooth.library.Constants.STATUS_CONNECTED;
import static com.vlusi.klintelligent.R.id.ib_home;

/**
 * 自定义相机
 * Created by 吴启 on 2017/7/14.
 */
public class Camera_Activity extends AppCompatActivity implements View.OnClickListener {


    String folder_path = Environment.getExternalStorageDirectory().getAbsolutePath();
    String folder_name = "FastWheel.jpg";
    String path = folder_path + File.separator + folder_name;
    @InjectView(R.id.camera_view)
    FrameLayout cameraView;  //相机的容器
    @InjectView(ib_home)
    ImageButton ibHome; //房子
    @InjectView(R.id.ib_bluetooth)
    ImageButton ibBluetooth;  //蓝牙
    @InjectView(R.id.ib_camera_battery)
    ProgerssBtn ibCameraBattery;  //相机的电池
   /* @InjectView(R.id.ib_single_photo)
    ImageButton ibSinglePhoto0s;  //滤镜*/
    @InjectView(R.id.ib_switch_camera)
    ImageButton ibSwitchCamera;   //切换摄像头
    @InjectView(R.id.ib_setting)
    ImageButton ibSetting;   //相机设置
    @InjectView(R.id.left_bar)
    LinearLayout leftBar;
    @InjectView(R.id.ll_tracker)
    LinearLayout llTracker;
    @InjectView(R.id.ib_single_photo1)
    ImageButton ibSinglePhoto; //模式
    @InjectView(R.id.switch_capture)
    SwitchCompat switchCapture;  // 切换拍照还是录像
    @InjectView(R.id.ib_photo_capture)
    ImageButton ibPhotoCapture;    //拍照
    @InjectView(R.id.ib_video_capture)
    ImageButton ibVideoCapture;   //录像
    @InjectView(R.id.ib_camera_gallery)
    ImageButton ibCameraGallery;  //相册
    @InjectView(R.id.ll_right_bar)
    RelativeLayout llRightBar;
    @InjectView(R.id.activity_camera)
    FrameLayout activityCamera;
    @InjectView(R.id.delayed) //显示延时配设的view
            TextView delayedText;
    @InjectView(R.id.button_capture)
    ImageView buttonCapture;  //录像计时图标
    @InjectView(R.id.textChrono)
    Chronometer textChrono;//计时文本
    /*@InjectView(R.id.Photograph)
    ImageButton Photograph;   //  单拍
    @InjectView(R.id.panorama)
    ImageButton panorama;  //全景*/
    private DeviceAdapterIist mDeviceAdapterIist;

    private boolean isOpen = true;
    private int hashCode = 0;

    private ListView mListView;
    private TextView tv_camern;
    private ImageButton mIbExit;
    private ImageView mIvRefresh;
    private List<SearchResult> mDevices;
    private ProgressBar progressBar;

    private Camera mCamera; //相机
    private CameraPreview mCameraPreview;  //相机预览
    private MediaRecorder mediaRecorder; // 媒体记录器
    private PopupWindow mPopupWindow;

    private File file;
    private Context mContext;
    private ContentValues values;
    private int layout_width;
    private int fouce_size;
    private boolean SelectCamera = true;
    private FoucsView mFoucsView; //自定义对焦的View
    boolean controlSwich = false;  //控制切换
    private PopupWindow mPopWindow0;
    private PopupWindow mPopWindow1;
    private PopupWindow mPopWindow2;
    //自定义网格
    CameraLine mCameraLine;
    private boolean mConnected =false; //连接状态


    //录制视频比特率
    public static final int MEDIA_QUALITY_HIGH = 20 * 100000;
    public static final int MEDIA_QUALITY_MIDDLE = 16 * 100000;
    public static final int MEDIA_QUALITY_LOW = 12 * 100000;
    public static final int MEDIA_QUALITY_POOR = 8 * 100000;
    public static final int MEDIA_QUALITY_FUNNY = 4 * 100000;
    public static final int MEDIA_QUALITY_DESPAIR = 2 * 100000;
    public static final int MEDIA_QUALITY_SORRY = 1 * 80000;
    //设置闪光灯
    private String flashMode = Camera.Parameters.FLASH_MODE_OFF;  //关闭闪光灯
    private FocusImageView mFocusImageView;
    private Camera.Parameters mParameters;

    //控制相机的常量
    private static int delayed = 0;  //延时拍摄

    //蓝牙
    private String mMac;
    private UUID mCharacterWrite; //可以读取数据
    private UUID mCharacterNotify; //可以接收通知
    private UUID mServiece;
    private UUID mCharacterRead;

    private long countUp;
    private String TAG = "Camera_Activity";
    private MyRunnable myRunnable;
    //更新延时拍照的UI
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2000:
                    delayedText.setVisibility(View.GONE);
                    break;

                case 1:
                    delayedText.setText(msg.what + "");
                    break;
                case 1000:
                    takePicture();
                    break;
                case 2:
                    delayedText.setText(msg.what + "");
                    break;
                case 3:
                    delayedText.setText(msg.what + "");
                    break;
                case 4:
                    delayedText.setText(msg.what + "");
                    break;
                case 5:
                    delayedText.setText(msg.what + "");
                    break;
                case 6:
                    delayedText.setText(msg.what + "");
                    break;
                case 7:
                    delayedText.setText(msg.what + "");
                    break;
                case 8:
                    delayedText.setText(msg.what + "");
                    break;
                case 9:
                    delayedText.setText(msg.what + "");
                    break;
                case 10:
                    delayedText.setText(msg.what + "");
                    break;
                case 500:
                    ToastUtil.showToast(mContext, "拍摄失败");
                    break;
                case 600:
                    //TODO ：当前是全景莫斯
                    ToastUtil.showToastLong(mContext, getString(R.string.Panorama_mode));
                    break;
            }
        }
    };
    private View contentView;
    private boolean isPanoramic = false;
    ArrayList<Mat> clickedImages = new ArrayList<>();
    private int PanoramaSize = 0;
    private Mat srcRes;  //合成的全景图片
    private BluetoothDevice remoteDevice;
    private boolean isClose = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //无标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  //全屏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mContext = this;
        changeAppLanguage();
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mDevices = new ArrayList<>();  //搜索设备的集合
        CameraUtil.init(this);
        ButterKnife.inject(this);
        initData();
        initialize();

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
            //得到远程的设备
           BluetoothDevice remoteDevice = BluetoothUtils.getRemoteDevice(mMac);
            ClientManager.getClient().registerConnectStatusListener(remoteDevice.getAddress(), mBleConnectStatusListener);
            ibBluetooth.setImageResource(R.drawable.bluetooth_select);
            ClientManager.getClient().refreshCache(mMac);
            Notify(mMac, mServiece, mCharacterNotify);
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
        //闪光灯的设置
        int flash_setting = SPUtil.getInt(mContext, Constant.FLASH_SETTING, 1);
        switch (flash_setting) {

            case 1:
                flashMode = Camera.Parameters.FLASH_MODE_OFF;   //关闭闪光灯
                break;
            case 2:
                flashMode = Camera.Parameters.FLASH_MODE_ON;   //打开闪光灯
                break;
            case 3:
                flashMode = Camera.Parameters.FLASH_MODE_AUTO;   //闪光灯自动
                break;
            case 4:
                flashMode = Camera.Parameters.FLASH_MODE_TORCH;   //闪光灯常亮
                break;
        }

        InitializeCamera();

        //相机网格的设置
        int grid_setting = SPUtil.getInt(mContext, Constant.GRID_SETTING, 1);
        switch (grid_setting) {
            case 1:
                mCameraLine.changeLineStyle(2);//关闭

                break;
            case 3:
                mCameraLine.changeLineStyle(1); //窄

                break;
            case 4:
                mCameraLine.changeLineStyle(0); //宽
                break;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        ClientManager.getClient().unnotify(mMac, mServiece, mCharacterNotify, new BleUnnotifyResponse() {
            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    Log.i(TAG, "关闭了通知");
                }
            }
        });
    }


    private void initData() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        layout_width = outMetrics.widthPixels;
        fouce_size = layout_width / 8;    //设置对焦框边长为屏幕宽度4分之一

    }

    private void initialize() {
        ibHome.setOnClickListener(this);  //房子按钮
        ibBluetooth.setOnClickListener(this); //蓝牙按钮
        ibSinglePhoto.setOnClickListener(this);
        ibSwitchCamera.setOnClickListener(this); //切换摄像头
        ibSetting.setOnClickListener(this); //设置
        ibPhotoCapture.setOnClickListener(this); //拍照片
        ibVideoCapture.setOnClickListener(this); //录像
        ibCameraGallery.setOnClickListener(this); // 相册
        switchCapture.setOnClickListener(this); //录像 和 拍照 的切换
        //焦点
        mFocusImageView = (FocusImageView) findViewById(R.id.focusImageView);
        //切换拍照与录像
        switchCapture.setThumbResource(R.drawable.selector_camera);
        switchCapture.setTrackResource(R.drawable.bg_camera_button);
        //录像 和 拍照 的切换的监听

        switchCapture.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if (!isPanoramic) {
                    if (b) {
                        ibVideoCapture.setVisibility(View.VISIBLE);
                        ibPhotoCapture.setVisibility(View.GONE);
                    } else {
                        ibPhotoCapture.setVisibility(View.VISIBLE);
                        ibVideoCapture.setVisibility(View.GONE);
                    }
                } else {
                    ToastUtil.showToast(mContext, getString(R.string.Panorama_mode));
                    switchCapture.setChecked(false);
                }


            }
        });

        // mCameraPreview--->   cameraView   添加到容器
        mCameraPreview = new CameraPreview(this, mCamera); //相机预览
        cameraView.addView(mCameraPreview);

        //mFoucsView 拍照按下是显示的对焦的View
        mFoucsView = new FoucsView(mContext, fouce_size);
        FrameLayout.LayoutParams foucs_param = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        foucs_param.gravity = Gravity.CENTER;
        mFoucsView.setLayoutParams(foucs_param);
        mFoucsView.setVisibility(View.INVISIBLE);
        //cameraView   是一个FrameLayout
        cameraView.addView(mFoucsView);  //添加到相机的容器


        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //设置聚焦
                        Point point = new Point((int) event.getX(), (int) event.getY());
                        mCameraPreview.onFocus(point, autoFocusCallback);
                        mFocusImageView.startFocus(point);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return true;
            }
        });
        mCameraLine = (CameraLine) findViewById(R.id.id_cl);

    }


    private final Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {

        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            //聚焦之后根据结果修改图片
            if (success) {
                mFocusImageView.onFocusSuccess();
            } else {
                //聚焦失败显示的图片，由于未找到合适的资源，这里仍显示同一张图片
                mFocusImageView.onFocusFailed();

            }
        }
    };


    /**
     * 初始化相机
     */
    private void InitializeCamera() {

        if (SelectCamera) { //true 后置  false  前置
            releaseCamera();//清空相机
            int cameraId = findBackFacingCamera();
            mCamera = Camera.open(cameraId);
            mCameraPreview.refreshCamera(mCamera);
            InitializeBackFacing(mCamera);
        } else {
            releaseCamera();//清空相机
            int cameraId = findFrontFacingCamera(); //找前置摄像头， -1代表没找到
            mCamera = Camera.open(cameraId);
            mCameraPreview.refreshCamera(mCamera);
            InitializeFrontFacing(mCamera);

        }


    }

    /**
     * 清空相机
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    /**
     * 初始化前置摄像头的参数
     */
    private void InitializeFrontFacing(Camera camera) {
        if (camera != null) {
            // 获取mCamera的参数对象
            Camera.Parameters mParameters = camera.getParameters();
            mParameters.setFocusMode(Camera.Parameters.ANTIBANDING_AUTO);   //设置自动对焦
            mParameters.setPictureSize(1920, 1080);
            camera.setParameters(mParameters);  //配置相机的参数
        }

    }

    /**
     * 初始化后置摄像头的参数
     */
    private void InitializeBackFacing(Camera camera) {
        if (camera != null) {
            // 获取mCamera的参数对象
            mParameters = camera.getParameters();
            mParameters.setFocusMode(Camera.Parameters.ANTIBANDING_AUTO);   //设置自动对焦
            mParameters.setFlashMode(flashMode); //闪光模式
            int photo_aesolution = SPUtil.getInt(mContext, Constant.PHOTO_AESOLUTION, 2);
            switch (photo_aesolution) {
                case 2:
                    //3264x1840  16:9   6M  1920×1080
                    mParameters.setPictureSize(1920, 1080);// 设置图片分辨
                    break;
                case 1:
                    //3264x2448  4:3    8M
                    mParameters.setPictureSize(3264, 2448);// 设置图片分辨  //小米6支持
                    break;
                case 3:
                    //3104x3104   1:1   10M  1280×720
                    mParameters.setPictureSize(1280, 720);// 设置图片分辨
                    break;
                case 4:
                    //4160x2336   16:9   10M
                    mParameters.setPictureSize(4160, 2336);// 设置图片分辨
                    break;
                case 5:
                    //4160x3120   4:3    13M
                    mParameters.setPictureSize(4160, 3120);// 设置图片分辨
                    break;
            }
            camera.setParameters(mParameters);  //配置相机的参数
        }


    }


    /**
     * TODO 拍照方法
     */
    private void takePicture() {

        EventBus.getDefault().post(new MessageEvent(true));

        mFoucsView.setVisibility(View.VISIBLE);
        if (mCamera != null) {
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean b, Camera camera) {
                    if (b) {
                        if (isPanoramic) {
                            mCamera.takePicture(null, null, mPanoramicCallback);
                        } else {
                            mCamera.takePicture(null, null, mPictureCallback);
                        }
                    } else {
                        mFoucsView.setVisibility(View.INVISIBLE);

                    }
                }
            });
        }
    }

    /**
     * (全景)拍摄成功后对图片的处理
     */

    Camera.PictureCallback mPanoramicCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mFoucsView.setVisibility(View.INVISIBLE);
            ///storage/emulated/0/FastWheel/1507209334286.jpg
        /*  Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            try {
                File file=new File(path);
                FileOutputStream   fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                fos.flush();
                Mat srcImag= Imgcodecs.imread(path);
                clickedImages.add(srcImag);
            }  catch (Exception e) {
                e.printStackTrace();
            }*/


            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            Mat srcImag = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
            Imgproc.resize(srcImag, srcImag, new Size(srcImag.rows() / 4, srcImag.cols() / 4));
            Utils.bitmapToMat(bitmap, srcImag);
            Imgproc.cvtColor(srcImag, srcImag, Imgproc.COLOR_BGR2RGB);
            clickedImages.add(srcImag);
            releaseCamera();  //清空相机
            InitializeCamera(); //初始化摄像头
            //TODO: 发送拍照指令
            shotPanoramic();
            if (clickedImages.size() == PanoramaSize) { //TODO 数字是可变的   6， 9 ， 10
                createPanorama();
            }

        }
    };


    private void createPanorama() {

        new AsyncTask<Void, Void, Bitmap>() {
            private Bitmap bitmap;
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(Camera_Activity.this, null, "合成中");
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                try {
                    if (PanoramaSize == 6) {
                        srcRes = NdkProc.stitchImage1(clickedImages.get(0), clickedImages.get(1), clickedImages.get(2), clickedImages.get(3), clickedImages.get(4), clickedImages.get(5));
                    } else if (PanoramaSize == 9) {
                        srcRes = NdkProc.stitchImage2(clickedImages.get(0), clickedImages.get(1), clickedImages.get(2), clickedImages.get(3), clickedImages.get(4), clickedImages.get(5), clickedImages.get(6), clickedImages.get(7), clickedImages.get(8));
                    } else if (PanoramaSize == 10) {
                        srcRes = NdkProc.stitchImage3(clickedImages.get(0), clickedImages.get(1), clickedImages.get(2), clickedImages.get(3), clickedImages.get(4), clickedImages.get(5), clickedImages.get(6), clickedImages.get(7), clickedImages.get(8), clickedImages.get(9));
                    }
                    Imgproc.cvtColor(srcRes, srcRes, Imgproc.COLOR_BGR2RGBA);
                    bitmap = Bitmap.createBitmap(srcRes.cols(), srcRes.rows(), Bitmap.Config.ARGB_8888);
                    Utils.matToBitmap(srcRes, bitmap);

                } catch (Exception e) {
                    Message ms = Message.obtain();
                    ms.what = 500;
                    mHandler.sendMessage(ms);
                }

                return bitmap;

            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                if (bitmap != null) {
                    long currentTimeMillis = System.currentTimeMillis();
                    String fileName = currentTimeMillis + ".jpg";
                    File file = new File(Constant.filefolderpath, fileName);
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(file);
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        if (clickedImages.size() == 6 || clickedImages.size() == 10) {
                            width = (int) (width * 0.94);
                            height = (int) (height * 0.7);
                            Bitmap NewBitmap = bitmap.createBitmap(bitmap, 50, 210, width, height);
                            NewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                        } else {
                            width = (int) (width * 0.8);
                            height = (int) (height * 0.7);
                            Bitmap NewBitmap = bitmap.createBitmap(bitmap, 400, 210, width, height);
                            NewBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        }

                        fos.flush();
                        fos.close();
                        values = new ContentValues();
                        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                        values.put(MediaStore.Images.Media.DATE_TAKEN, currentTimeMillis);
                        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                    clickedImages.clear();
                }
            }
        }.execute();
    }


    private void saveBitmap(Bitmap bitmap) {

        long currentTimeMillis = System.currentTimeMillis();
        String fileName = currentTimeMillis + ".jpg";
        File file = new File(Constant.filefolderpath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
            releaseCamera();  //清空相机
            InitializeCamera(); //初始化摄像头
        } catch (Exception e) {
            e.printStackTrace();
            releaseCamera();  //清空相机
            InitializeCamera(); //初始化摄像头
            Log.i("全景", "图片保存失败");
        }

        values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
        values.put(MediaStore.Images.Media.DATE_TAKEN, currentTimeMillis);
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
    }


    /**
     * (单拍)拍摄成功后对图片的处理
     */
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @SuppressLint("LongLogTag")
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            mFoucsView.setVisibility(View.INVISIBLE);

            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            saveBitmap(bitmap);
        }
    };


    /**
     * 找前置摄像头,没有则返回-1
     *
     * @return cameraId
     */
    private int findFrontFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    /**
     * 找后置摄像头,没有则返回  -1
     *
     * @return cameraId
     */
    private int findBackFacingCamera() {
        int cameraId = -1;
        //获取摄像头个数
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_home: // 房子建
                finish();
                break;
            case R.id.ib_bluetooth:  //TODO:蓝牙
                    initPopupWindow();
                    mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                break;
            case R.id.ib_camera_gallery: //相册
                gallery();
                break;
            case R.id.ib_photo_capture:
                if (isPanoramic) {
                    if (isClose){
                        ToastUtil.showToastLong(mContext, getString(R.string.Panorama_mode));
                        isClose=false;
                    }
                    //TODO 发送全景拍照指令
                    ButtonCommandPanoramic();
                } else {
                    //单拍 延时
                    if (delayed != 0) {
                        delayedCamera();
                    } else {
                        takePicture();
                    }
                }
                break;
            case R.id.ib_video_capture: //录像
                videoRecording();
                break;
            case R.id.ib_switch_camera: //切换摄像头
                switchCamera();
                break;
            case R.id.ib_setting://设置
                Intent intent = new Intent(this, CameraSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ib_single_photo1:    //TODO: 开启 全景  单拍
                showPopupWindow0(view);
                break;

            case R.id.Single_shot: //TODO:单拍模式
                ToastUtil.showToastLong(mContext, getString(R.string.Single_shot_mode));
                isPanoramic = false;
                enterSingleTake();
                showPopupWindow1();
                isClose = false;
                break;

            case R.id.panoramic:  //TODO 全景模式
                ToastUtil.showToastLong(mContext, getString(R.string.Panorama_mode));
                isPanoramic = true;
                showPopupWindow2();
                break;
            case R.id.delayShooting:
                ToastUtil.showToast(mContext, getString(R.string.Turn_off_delay_shooting));
                delayed = 0;
                mPopWindow1.dismiss();
                break;
            case R.id.camera_1s:
                delayed = 2;
                ToastUtil.showToast(mContext, getString(R.string.s1));
                mPopWindow1.dismiss();
                mPopWindow0.dismiss();
                break;
            case R.id.camera_5s:
                delayed = 5;
                ToastUtil.showToast(mContext, getString(R.string.s2));
                mPopWindow1.dismiss();
                mPopWindow0.dismiss();
                break;
            case R.id.camera_10s:
                delayed = 10;
                ToastUtil.showToast(mContext, getString(R.string.s3));
                mPopWindow1.dismiss();
                mPopWindow0.dismiss();
                break;

            case R.id.panoramicPhoto:
                mPopWindow2.dismiss();
                break;

            case R.id.one_frame11:
                ToastUtil.showToast(mContext, getString(R.string.degree1));
                //TODO:  180
                enter180Take();
                mPopWindow2.dismiss();
                mPopWindow0.dismiss();
                break;
            case R.id.camera_improvement11:
                ToastUtil.showToast(mContext, getString(R.string.degree2));
                //TODO:  330
                enterPanoramicTake();
                mPopWindow2.dismiss();
                mPopWindow0.dismiss();
                break;
            case R.id.long_exposure11:
                ToastUtil.showToast(mContext, getString(R.string.rectangle));
                //TODO:  Rectangle
                enterRectangleTake();
                mPopWindow2.dismiss();
                mPopWindow0.dismiss();
                break;
            // 刷新
            case R.id.iv_refresh:
                startSearch();
                break;
            case R.id.ib_exit:
                mPopupWindow.dismiss();
                break;

        }
    }


    //延时拍照
    private void delayedCamera() {
        delayedText.setVisibility(View.VISIBLE);


        new Thread(new Runnable() {
            Message ms;

            @Override
            public void run() {

                for (int j = delayed; j > 0; j--) {
                    try {
                        ms = Message.obtain();
                        ms.what = j;
                        mHandler.sendMessage(ms);
                        Thread.sleep(1000);
                        if (j == 1) {
                            ms = Message.obtain();
                            ms.what = 2000;  //隐藏延时布局
                            mHandler.sendMessage(ms);
                        }
                        Log.i("TAG", "一秒更新一次UI   " + j);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                ms = Message.obtain();
                ms.what = 1000;  //拍照
                mHandler.sendMessage(ms);

            }
        }).start();


    }


    /**
     * 切换相机
     */
    private void switchCamera() {
        if (!recording) {
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                releaseCamera();
                chooseCamera(); //选择摄像头
            } else {
                //只有一个摄像头不允许切换
                Toast.makeText(getApplicationContext(), "只有一个摄像头不允许切换", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 选择摄像头
     */
    public void chooseCamera() {
        if (SelectCamera) {

            int cameraId = findBackFacingCamera();
            if (cameraId >= 0) {  //后置
                SelectCamera = false;
                InitializeCamera();
            } else {
                //没有找到后置摄像头
            }
        } else {
            int cameraId = findFrontFacingCamera();
            if (cameraId >= 0) {
                SelectCamera = true;
                InitializeCamera();
            } else {
                //没有找到前置摄像头
            }
        }
    }

    /**
     * 视频录制
     */
    boolean recording = false;  //记录

    private void videoRecording() {
        if (recording) { //true  正在录制   false : 不在录制
            //正在录制点击这个按钮表示录制完成
            try {
                mediaRecorder.stop();
                stopChronometer();  //停止计时器
            } catch (Exception e) {

            }

            ibVideoCapture.setImageResource(R.drawable.btn_record_n);
            releaseMediaRecorder(); //释放媒体记录器
            recording = false; //不录制了
            releaseCamera(); //释放相机
            ToastUtil.showToast(mContext, "录制成功");

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            //不写广播依然插入系统并更新，但是本地也并没有更新
            mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            InitializeCamera();//初始化相机

        } else {
            //准备开始录制视频
            if (!prepareMediaRecorder()) {
                Log.i("TT", "-----相机初始化失败---");
                //释放相机
                releaseCamera();
                //释放媒体记录器
                releaseMediaRecorder();
                return;
            }
            //开始录制 -----开启子线程录制视频
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {
                        mediaRecorder.start(); //媒体记录器启动
                        startChronometer();//计时
                        ibVideoCapture.setImageResource(R.drawable.btn_record_s);
                    } catch (Exception e) {
                        //释放相机
                        releaseCamera();
                    }
                }
            });
            recording = true;  //正在录制
        }
    }


    /**
     * 开始计时
     */
    private void startChronometer() {
        buttonCapture.setVisibility(View.VISIBLE);
        textChrono.setVisibility(View.VISIBLE);
        final long startTime = SystemClock.elapsedRealtime();
        textChrono.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer arg0) {
                countUp = (SystemClock.elapsedRealtime() - startTime) / 1000;
                if (countUp % 2 == 0) {
                    buttonCapture.setVisibility(View.VISIBLE);
                } else {
                    buttonCapture.setVisibility(View.INVISIBLE);
                }
                switchCapture.setChecked(true);
                String asText = String.format("%02d", countUp / 60) + ":" + String.format("%02d", countUp % 60);
                textChrono.setText(asText);
            }
        });
        textChrono.start();
    }


    /**
     * 计时停止，对应的按钮图片隐藏
     */
    private void stopChronometer() {
        textChrono.stop();
        buttonCapture.setVisibility(View.INVISIBLE);
        textChrono.setVisibility(View.INVISIBLE);
    }


    /**
     * 准备媒体记录器
     *
     * @return
     */
    private boolean prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);
        //音频
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        //视频
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //设置视频编码比特率
        mediaRecorder.setVideoEncodingBitRate(MEDIA_QUALITY_HIGH);
        //设置录制视频的分辨率
        int anInt = SPUtil.getInt(mContext, Constant.VIDEORESOLUTION, 2);
        switch (anInt) {
            case 1:
                mediaRecorder.setProfile(CamcorderProfile.get(Constant.quality_1080p));
                break;
            case 2:
                mediaRecorder.setProfile(CamcorderProfile.get(Constant.quality_720p));
                break;
            case 3:
                mediaRecorder.setProfile(CamcorderProfile.get(Constant.quality_480p));
                break;
        }


        //定义视频保存位置，文件夹必须存在  //写到指定文件夹
        file = new File(Constant.filefolderpath);
        if (!file.exists()) {
            file.mkdirs();
        }
        long mTimeStamp = System.currentTimeMillis();
        file = new File(Constant.filefolderpath, mTimeStamp + ".mp4");
        String filepath = file.getAbsolutePath();
        //写视频
        mediaRecorder.setOutputFile(filepath);
        //根据视频全路径 时间，插入多媒体数据库
        values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, filepath);
        values.put(MediaStore.Video.Media.DATE_TAKEN, mTimeStamp);

        try {
            mediaRecorder.prepare(); //准备
        } catch (IllegalStateException e) {
            e.printStackTrace();
            releaseMediaRecorder();  //释放媒体记录器
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            releaseMediaRecorder(); //释放媒体记录器
            return false;
        }
        return true;
    }

    /**
     * 释放媒体记录器
     */
    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            if (mCamera != null) {
                mCamera.lock();
            }

        }
    }

    /**
     * 跳转到相册
     */
    public void gallery() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("album", true);
        startActivity(intent);
        finish();
    }

    //TODO: 单拍 全景
    private void showPopupWindow0(View v) {
        //设置contentView
        contentView = LayoutInflater.from(this).inflate(R.layout.camera_pop0, null);
        mPopWindow0 = new PopupWindow(contentView,
                ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT, true);
        mPopWindow0.setContentView(contentView);

        // 设置背景，触摸框外区域也可以关闭弹出框
        mPopWindow0.setBackgroundDrawable(new ColorDrawable());
        mPopWindow0.setOutsideTouchable(true);
        mPopWindow0.setFocusable(true);
        mPopWindow0.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopWindow0.dismiss();
                    return true;
                }
                return false;
            }
        });


        //设置各个控件的点击响应
        ImageView Single_shot = (ImageView) contentView.findViewById(R.id.Single_shot);
        ImageView panoramic = (ImageView) contentView.findViewById(R.id.panoramic);
        //全景 ，单拍 点击事件
        Single_shot.setOnClickListener(this);
        panoramic.setOnClickListener(this);

        int[] location = new int[2];
        //获取控件相对于父窗口的坐标
        v.getLocationOnScreen(location);
        //显示在左方
        mPopWindow0.showAtLocation(v, Gravity.NO_GRAVITY, location[0] - mPopWindow0.getWidth() + 125, location[1]);

    }

    /**
     * 单拍
     */
    private void showPopupWindow1() {

        //设置contentView
        contentView = LayoutInflater.from(this).inflate(R.layout.camera_pop, null);
        mPopWindow1 = new PopupWindow(contentView,
                ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT, true);
        mPopWindow1.setContentView(contentView);

        // 设置背景，触摸框外区域也可以关闭弹出框
        mPopWindow1.setBackgroundDrawable(new ColorDrawable());
        mPopWindow1.setOutsideTouchable(true);
        mPopWindow1.setFocusable(true);
        mPopWindow1.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopWindow1.dismiss();
                    return true;
                }
                return false;
            }
        });
        //设置各个控件的点击响应
        ImageView tv1 = (ImageView) contentView.findViewById(R.id.delayShooting);
        ImageView tv2 = (ImageView) contentView.findViewById(R.id.camera_1s);
        ImageView tv3 = (ImageView) contentView.findViewById(R.id.camera_5s);
        ImageView tv4 = (ImageView) contentView.findViewById(R.id.camera_10s);

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);

        mPopWindow1.showAtLocation(contentView, Gravity.NO_GRAVITY, 100, 140);


    }


    /**
     * 全景
     */
    private void showPopupWindow2() {

        //设置contentView
        contentView = LayoutInflater.from(this).inflate(R.layout.camera_pop1, null);
        mPopWindow2 = new PopupWindow(contentView,
                ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT, true);
        mPopWindow2.setContentView(contentView);

        // 设置背景，触摸框外区域也可以关闭弹出框
        mPopWindow2.setBackgroundDrawable(new ColorDrawable());
        mPopWindow2.setOutsideTouchable(true);
        mPopWindow2.setFocusable(true);
        mPopWindow2.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    mPopWindow2.dismiss();
                    return true;
                }
                return false;
            }
        });
        //设置各个控件的点击响应
        ImageView tv1 = (ImageView) contentView.findViewById(R.id.one_frame11);
        ImageView tv2 = (ImageView) contentView.findViewById(R.id.camera_improvement11);
        ImageView tv3 = (ImageView) contentView.findViewById(R.id.long_exposure11);
        ImageView tv4 = (ImageView) contentView.findViewById(R.id.panoramicPhoto);
        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
        tv3.setOnClickListener(this);
        tv4.setOnClickListener(this);

        //显示PopupWindow
        mPopWindow2.showAtLocation(contentView, Gravity.NO_GRAVITY, 100, 140);

    }


    /**
     * 初始化Popwindow
     */
    private void initPopupWindow() {
        View contentview = LayoutInflater.from(mContext).inflate(R.layout.ppw_bluetooth, null, false);
        tv_camern = (TextView) contentview.findViewById(R.id.tv_camern);
        mIbExit = (ImageButton) contentview.findViewById(R.id.ib_exit);
        mIvRefresh = (ImageView) contentview.findViewById(R.id.iv_refresh);
        mListView = (ListView) contentview.findViewById(R.id.list_device);
        mPopupWindow = new PopupWindow();
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewPager.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setContentView(contentview);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mIbExit.setOnClickListener(this);
        mIvRefresh.setOnClickListener(this);
        tv_camern.setOnClickListener(this);
        tv_camern.setVisibility(View.GONE);
        mListView = (ListView) contentview.findViewById(R.id.list_device);
        progressBar = (ProgressBar) contentview.findViewById(R.id.progressbar);
        mListView.setDivider(null); //去listViwe下划线
        //设置列表显示蓝牙设备listview
        mDeviceAdapterIist = new DeviceAdapterIist(mContext, mDevices);//TODO: 做更改
        mListView.setAdapter(mDeviceAdapterIist);
        mDeviceAdapterIist.setMacListener(new DeviceAdapterIist.MacListener() {
            @Override
            public void onMAC(String mac, String name) {
                if (mConnected) {  //是连接的时候就return
                 //  ToastUtil.showToast(mContext,getString(R.string.connect_Connected));
                   // ClientManager.getClient().disconnect(mac); //断开连接
                    return;
                }
                if (mac != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    if (name != null && name.contains("OTA")) {
                        startActivity(new Intent(getApplicationContext(), HardwareActivity.class));
                    } else {
                        hashCode = name.hashCode();
                        remoteDevice = BluetoothUtils.getRemoteDevice(mac);
                        ClientManager.getClient().registerConnectStatusListener(remoteDevice.getAddress(), mBleConnectStatusListener);
                        connectDevice(mac);

                    }
                }


            }
        });
        contentview.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE

        );
        mPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        startSearch();
    }


    private void connectDevice(final String mac) {


        //蓝牙连接选项
        BleConnectOptions options = new BleConnectOptions.Builder()
                 .setConnectRetry(3)  //设置连接重试
                 .setConnectTimeout(20000)  //设置连接超时
               .setServiceDiscoverRetry(3)  //设置服务器发现重试
               .setServiceDiscoverTimeout(10000)  //设置服务器发现超时*/
                .build(); //构建
        ClientManager.getClient().connect(remoteDevice.getAddress(), options, new BleConnectResponse() {
            @Override
            public void onResponse(int code, BleGattProfile data) {
                //返回的code表示操作状态，包括成功，失败或超时等，
                if (code == REQUEST_SUCCESS) {
                    mMac = mac;
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
                    setDataList(items, mac);
                    Log.i(TAG, "------------------连接成功");
                    SPUtil.putString(mContext, Constant.mac, mac);

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
                // Log.i(TAG, "----服务： " + service);
            } else {
                UUID Characteristic = item.uuid; //特征值
                uuidArrayList.add(Characteristic);
                // Log.i(TAG, "----特征值： " + Characteristic);
            }
        }
        mServiece = uuidArrayList.get(0);   //服务
        mCharacterWrite = uuidArrayList.get(1); //特征值   用来读数据
        mCharacterNotify = uuidArrayList.get(2);//特征值   可以接收通知
        mCharacterRead = uuidArrayList.get(3);  //特征值   用来写数据
        String Serviece = mServiece.toString();
        String CharacterWrite = mCharacterWrite.toString();
        String CharacterNotify = mCharacterNotify.toString();
        SPUtil.putString(mContext, Constant.sversion, Serviece);
        SPUtil.putString(mContext, Constant.CharacterWrite, CharacterWrite);
        SPUtil.putString(mContext, Constant.CharacterNotify, CharacterNotify);
        SPUtil.putString(mContext, Constant.mac, mac);
        ClientManager.getClient().refreshCache(mac);
        Notify(mac, mServiece, mCharacterNotify);
    }


    //TODO:接收服务端云台的通知
    private void Notify(final String MAC, UUID service, UUID uuid) {
        ClientManager.getClient().notify(MAC, service, uuid, new BleNotifyResponse() {
            @Override
            public void onNotify(UUID service, UUID character, byte[] value) {

                handlerData(value);

            }

            @Override
            public void onResponse(int code) {
                if (code == REQUEST_SUCCESS) {
                    mConnected=true;
                    ReadBatteryCapacity();
                    myRunnable = new MyRunnable();
                    mHandler.postDelayed(myRunnable, 60000);
                }
            }
        });
    }


    class MyRunnable implements Runnable {
        @Override
        public void run() {

            ReadBatteryCapacity();
            mHandler.postDelayed(myRunnable, 60000);
        }
    }


    /**
     * 拍摄照片
     */
    private void shotPanoramic() {
        byte[] bytes = new byte[7];
        bytes[0] = CMD.STR;
        bytes[1] = 0x02;
        bytes[2] = CMD.CMD_COMMAND;
        bytes[3] = 0x35;
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;
        bytes[5] = CMD.END;
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);
        Log.i("全景", "发送拍照指令：" + ByteUtils.byteToString(bytes));
    }


    /**
     * 进入全景拍摄模式
     */
    private void ButtonCommandPanoramic() {
        byte[] bytes = new byte[7];
        bytes[0] = CMD.STR;
        bytes[1] = 0x02;
        bytes[2] = CMD.CMD_COMMAND; //0x03
        bytes[3] = 0x36;
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;
        bytes[5] = CMD.END;
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);
        Log.i("全景", "进入全景拍摄模式，并且发送拍照指令：" + ByteUtils.byteToString(bytes));
    }


    /**
     * //TODO:进入 单拍模式
     */
    private void enterSingleTake() {
        clickedImages.clear();
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;
        bytes[1] = 0x04;
        bytes[2] = 0x02;
        bytes[3] = (byte) 0x90;
        bytes[4] = 0x00;
        bytes[5] = 0x00;
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;  //校验
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("全景", "单拍指令发送：" + ByteUtils.byteToString(bytes));
    }

    /**
     * //TODO:进入 180
     */
    private void enter180Take() {
        clickedImages.clear();
        PanoramaSize = 6;

        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;
        bytes[1] = 0x04;
        bytes[2] = 0x02;
        bytes[3] = (byte) 0x90;
        bytes[4] = 0x01;
        bytes[5] = 0x06;
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;  //校验
        bytes[7] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("全景", "180：" + ByteUtils.byteToString(bytes));
    }

    /**
     * //TODO:进入全景  330
     */
    private void enterPanoramicTake() {
        clickedImages.clear();
        PanoramaSize = 10;

        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;
        bytes[1] = 0x04;
        bytes[2] = 0x02;
        bytes[3] = (byte) 0x90;
        bytes[4] = 0x02;
        bytes[5] = 0x0A;
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END;
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("全景", "360：" + ByteUtils.byteToString(bytes));
    }

    /**
     * //TODO:进入全景  矩形
     */
    private void enterRectangleTake() {
        clickedImages.clear();
        PanoramaSize = 9;
        byte[] bytes = new byte[8];
        bytes[0] = CMD.STR;
        bytes[1] = 0x04;
        bytes[2] = 0x02;
        bytes[3] = (byte) 0x90;
        bytes[4] = 0x03;
        bytes[5] = 0x09;
        int ck = SendDataManager.getCK(bytes, 2, 5);
        bytes[6] = (byte) ck;
        bytes[7] = CMD.END;
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);

        Log.i("全景", "矩形：" + ByteUtils.byteToString(bytes));
    }

    /**
     * 读取电池容量
     */
    private void ReadBatteryCapacity() {
        //蓝牙协议
        byte[] bytes = new byte[6];
        bytes[0] = CMD.STR;  //起始标志
        bytes[1] = 0x02;     //数据的长度
        bytes[2] = 0x01;     //获取类           从2开始
        bytes[3] = 0x19;     //电池容量
        int ck = SendDataManager.getCK(bytes, 2, 3);
        bytes[4] = (byte) ck;  //校验
        bytes[5] = CMD.END; //结束标志
        ClientManager.getClient().write(mMac, mServiece, mCharacterWrite,
                bytes, mWriteRsp);


    }

    //处理数据
    byte[] buffer;

    private void handlerData(byte[] value) {
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
                command = bytes[index++]; //114
            }
            int Battery = 99; //电池容量默认99
            byte state = bytes[index++]; //状态  1
            if (command == CMD.BATTERY_CAPACITY && state == CMD.STATE_OK) {
                byte byteType = bytes[index++];
                if (byteType == CMD.DATATYPE_CHAR) { //char   数据类型
                    String str = ByteUtils.byteToString(bytes);
                    String substring = str.substring(8, str.length());
                    Battery = Integer.parseInt(substring, 16);

                    char c = substring.charAt(0);
                    char[] charArray = {c};
                    int intNum = Integer.parseInt(new String(charArray));
                    if (intNum == 0) {
                        String substring1 = substring.substring(1, 2);
                        Battery = Integer.parseInt(substring1, 16);
                    }
                }
                initBattery(Battery);
            }

            switch (bytes[1]) {

                case CMD.NOTIFY_CAPTURE:  //拍照或者录像

                    if (!isPanoramic) {
                        if (controlSwich) {  //录像
                            videoRecording();
                            switchCapture.setChecked(true);
                            ibVideoCapture.setVisibility(View.VISIBLE);
                            ibPhotoCapture.setVisibility(View.GONE);
                        } else {
                            if (delayed != 0) {
                                delayedCamera();
                            } else {
                                takePicture();
                            }
                            switchCapture.setChecked(false);
                            ibVideoCapture.setVisibility(View.GONE);
                            ibPhotoCapture.setVisibility(View.VISIBLE);
                        }
                    } else {
                        ButtonCommandPanoramic();
                    }
                    break;
                case CMD.NOTIFY_SWITCH_START: //切换摄像方式
                    if (!isPanoramic) {
                        if (controlSwich) {
                            switchCapture.setChecked(false);
                            ibVideoCapture.setVisibility(View.GONE);
                            ibPhotoCapture.setVisibility(View.VISIBLE);
                            controlSwich = false;
                            //拍照
                        } else {
                            switchCapture.setChecked(true);
                            ibVideoCapture.setVisibility(View.VISIBLE);
                            ibPhotoCapture.setVisibility(View.GONE);
                            controlSwich = true;
                            //录像
                        }
                    } else {
                        //提示用户当前是全景模式
                        Message message = Message.obtain();
                        message.what = 600;
                        mHandler.sendMessage(message);
                    }
                    break;
                case CMD.NOTIFY_SWITCH_CAMERA:  //切换前后摄像头
                    switchCamera();
                    break;


            }


            String code = ByteUtils.byteToString(bytes);
            if (code.equals("019001")) {
                Log.i("全景", "------------开启xxx模式");
            }
            if (code.equals("013601")) {    //03 36
                Log.i("全景", "------------进入完毕");
            }

            if (code.equals("0417")) {    // 03 35
                Log.i("全景", "--接收到云台拍照指令----------拍照");
                takePicture();
            }

            if (code.equals("0419")) {  //结束拍照
                //  isPanoramic = false;
                Log.i("全景", "-------结束拍照");
            }
            buffer = null;
        }
    }


    /**
     * 初始化电池
     */
    private void initBattery(int Battery) {
        ibCameraBattery.setProgressEnable(true);  //允许设置进度条
        ibCameraBattery.setProgressMax(100); //进度最大值
        ibCameraBattery.setmCurrProgress(Battery);
    }


    /**
     * 搜索蓝牙
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


    /**
     * 蓝牙写的响应
     */
    private final BleWriteResponse mWriteRsp = new BleWriteResponse() {
        @Override
        public void onResponse(int code) {
            if (code == REQUEST_SUCCESS) {
                //  Log.i("TAG", "    -----------写入成功");
            } else {
                Log.i("TAG", "    -----------写入失败");
            }
        }
    };


    private final BleConnectStatusListener mBleConnectStatusListener = new BleConnectStatusListener() {

        @Override
        public void onConnectStatusChanged(String mac, int status) {

            mMac = mac;
            mConnected = (status == STATUS_CONNECTED); //相等代表是连接状态
            connectDeviceState(); //如果需要连接设备
            Log.i("连接状态","相机界面连接状态："+mConnected);
        }
    };



    private void connectDeviceState() {
        if (mConnected) {
            try {
                ibBluetooth.setImageResource(R.drawable.bluetooth_select);
                SPUtil.putInt(mContext, Constant.connectState, hashCode);
                mDeviceAdapterIist.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mPopupWindow.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            try {
                ibBluetooth.setImageResource(R.drawable.bluetooth_closs);
                SPUtil.removeKey(mContext, Constant.connectState);
                startSearch();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * 改变语言的切换
     */
    public void changeAppLanguage() {
        String lanString = SPUtil.getString(mContext, "language", "zh");
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

        SPUtil.putString(mContext, "language", lanString);
    }


}
