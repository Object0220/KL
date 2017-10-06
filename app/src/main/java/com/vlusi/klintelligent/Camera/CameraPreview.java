package com.vlusi.klintelligent.Camera;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * 自定义视图的视频预览
 *
 * @author anthorlop
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = CameraPreview.class.getSimpleName();

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;


    //构造相机预览的创建
    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mContext = context;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }


    public void surfaceCreated(SurfaceHolder holder) { //外观的改变
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder); //设置预览显示
                ConfigurePreview(mCamera); //优化相机的尺寸
                // ConfigurePreview(mCamera);
                mCamera.startPreview(); //开启预览
            }
        } catch (IOException e) {
            Log.d(TAG, "开启相机的预览错误" + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {  //刷新相机
        if (mHolder.getSurface() == null) {
            return;
        }
        camera.stopPreview(); //停止预览
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (camera != null) {
                camera.setDisplayOrientation(90); //设置显示方向
            }
        }

        setCamera(camera); //把配置好的相机赋值给成员变量
        try {
            camera.setPreviewDisplay(mHolder); //给相机设置预览显示，传入SurfaceHolder对象

            ConfigurePreview(mCamera); //优化相机的尺寸

            camera.startPreview();  //启动预览
        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    //表面改变
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        refreshCamera(mCamera);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) { //表面破坏
        // mCamera.release();
    }

    /**
     * 配置相机的预览
     *
     * @param mCamera
     */
    private void ConfigurePreview(Camera mCamera) {

        final int width = ScreensUtils.getScreenWidth(mContext);   // 获取屏幕的宽度
        final int height = ScreensUtils.getScreenHeight(mContext);  // 获取屏幕的高度

        final List<Camera.Size> mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
        Camera.Size mPreviewSize; //预览的大小
        if (mSupportedPreviewSizes != null) {
            //得到最优的预览的大小
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
            float ratio;
            if (mPreviewSize.height >= mPreviewSize.width)
                ratio = (float) mPreviewSize.height / (float) mPreviewSize.width;
            else
                ratio = (float) mPreviewSize.width / (float) mPreviewSize.height;

            //根据比例测量
            setMeasuredDimension(width, (int) (width * ratio));

            Camera.Parameters parameters = mCamera.getParameters(); //得到相机参数配置的对象
            parameters.setPictureFormat(ImageFormat.JPEG);  //图片格式
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);//设置预览的宽高
            //parameters.setPictureSize(1280, 720);
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 得到最优的预览大小
     */
    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (w > h)
            targetRatio = (double) w / h;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {

            double ratio = (double) size.width / size.height;
            if (size.height >= size.width)
                ratio = (float) size.height / size.width;

            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }


    /**
     * 手动聚焦
     *
     * @param point 触屏坐标
     */
    public void onFocus(Point point, Camera.AutoFocusCallback callback) {
        Camera.Parameters parameters = mCamera.getParameters();
        //不支持设置自定义聚焦，则使用自动聚焦，返回
        if (parameters.getMaxNumFocusAreas() <= 0) {
            mCamera.autoFocus(callback);
            return;
        }
        List<Camera.Area> areas = new ArrayList<Camera.Area>();
        int left = point.x - 300;
        int top = point.y - 300;
        int right = point.x + 300;
        int bottom = point.y + 300;
        left = left < -1000 ? -1000 : left;
        top = top < -1000 ? -1000 : top;
        right = right > 1000 ? 1000 : right;
        bottom = bottom > 1000 ? 1000 : bottom;
        areas.add(new Camera.Area(new Rect(left, top, right, bottom), 100));
        parameters.setFocusAreas(areas);
        try {
            //使用的小米手机在设置聚焦区域的时候经常会出异常，看日志发现是框架层的字符串转int的时候出错了，
            //目测是小米修改了框架层代码导致，在此try掉，对实际聚焦效果没影响
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        mCamera.autoFocus(callback);
    }
}