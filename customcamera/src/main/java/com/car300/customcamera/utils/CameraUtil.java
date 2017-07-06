package com.car300.customcamera.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.SurfaceHolder;
import android.widget.ImageView;

import com.car300.customcamera.AccidentCameraActivity;
import com.car300.customcamera.CameraActivity;
import com.car300.customcamera.LicenseIndentifyCameraActivity;
import com.car300.customcamera.R;
import com.car300.customcamera.data.CameraConstants;
import com.car300.customcamera.data.PhotoInfo;
import com.gengqiquan.result.RxActivityResult;
import com.xhe.photoalbum.PhotoAlbum;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

/**
 * Created by xhe on 2017/4/05.
 */
public class CameraUtil {
    private Camera mCamera;
    private static CameraUtil mCameraUtil;
    private boolean isPreview;
    private int cameraId = -1; //0表示后置，1表示前置
    private Camera.CameraInfo mCameraInfo = new Camera.CameraInfo();

    public static synchronized CameraUtil getInstance() {
        if (mCameraUtil == null) {
            mCameraUtil = new CameraUtil();
        }
        return mCameraUtil;
    }


    /**
     * 打开相册
     * 使用系统相册的时候，小米部分机型会杀掉app的进程,所以使用开源
     */
    public void doOpenSystemPhoto(Activity activity, @NonNull PhotoAlbum.ActivityForResultCallBack resultCallBack) {
        new PhotoAlbum(activity)
                .setLimitCount(1)
                .setTitlebarColor(ContextCompat.getColor(activity, R.color.main))
                .setTitleTextColor(Color.WHITE)
                .startAlbum(resultCallBack);
    }

    /**
     * 打开相机
     */
    public void doOpenCamera(Context context) {
        doStopCamera();
        boolean isFacingBack = Boolean.parseBoolean(PersistUtil.load(context, PersistUtil.CAMERA_FACING_BACK, "true"));
        try {
            if (isFacingBack) {
                cameraId = getFacingCameraId(Camera.CameraInfo.CAMERA_FACING_BACK);
            } else {
                cameraId = getFacingCameraId(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
            Log.d("Camera", "open camera" + cameraId);
            mCamera = Camera.open(cameraId);
            Camera.getCameraInfo(cameraId, mCameraInfo);///这里的mCamerainfo必须是new出来的，不能是个null
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 开启预览
     *
     * @param holder
     */
    public void doStartPreview(SurfaceHolder holder, Context context) {
        stopPreview();
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPictureFormat(ImageFormat.JPEG);//设置照片拍摄后的保存格式
            parameters.setJpegQuality(100);
//            mCamera.setDisplayOrientation(90);

            int flashLigtMode = Integer.parseInt(PersistUtil.load(context, PersistUtil.CAMERA_FLASH_LIGHT, "0"));
            if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);//连续对焦
                //闪光灯
                switch (flashLigtMode) {
                    case -1:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        break;
                    case 0:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                        break;
                    case 1:
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                        break;
                }
            }

            mCamera.setParameters(parameters);

            try {
                mCamera.setPreviewDisplay(holder);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //设置的这两个size必须时支持的size大小，否则时不可以的，会出现setparameters错误
            Camera.Size pictureSize = setPictureSize(CameraConstants.DefaultWidth, CameraConstants.DefaultHeight);
            setPreviewSize(pictureSize.width, pictureSize.height);

            startPreview();
        }
    }

    /**
     * 结束预览
     */
    public void doStopCamera() {
        if (isPreview && mCamera != null) {
            stopPreview();

            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 拍照
     */
    public void doTakePic() {
        if (isPreview && mCamera != null) {
            mCamera.takePicture(new ShutCallBackImpl(), null, new PicCallBacKImpl());
        }
    }

    public void doTakePic(Camera.PictureCallback pictureCallback) {
        if (isPreview && mCamera != null) {
            mCamera.takePicture(new ShutCallBackImpl(), null, pictureCallback);
        }
    }

    /**
     * 拍照后的最主要的返回
     */
    private class PicCallBacKImpl implements Camera.PictureCallback {
        @Override
        public void onPictureTaken(final byte[] data, Camera camera) {
            isPreview = false;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*String filePath = ImageUtil.getSaveImgePath();
                    File file = new File(filePath);
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(file, true);
                        fos.write(data);
                        ImageUtil.saveImage(file, data, filePath);
                        fos.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                }
            }).start();

            startPreview();//重新开启预览 ，不然不能继续拍照
        }
    }

    public void startPreview() {
        if (!isPreview && mCamera != null) {
            mCamera.startPreview();
            isPreview = true;
        }
    }

    public void stopPreview() {
        if (isPreview && mCamera != null) {
            mCamera.stopPreview();
            isPreview = false;
        }
    }

    public void setPreview(boolean preview) {
        isPreview = preview;
    }

    /**
     * 设置闪光灯的模式
     */
    public void setFlashMode(Context context, ImageView imageView) {
        int flashLigtMode = Integer.parseInt(PersistUtil.load(context, PersistUtil.CAMERA_FLASH_LIGHT, "0"));
        Log.d("setFlashMode  ", "" + flashLigtMode);
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            switch (flashLigtMode) {
                case -1:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    imageView.setImageResource(R.drawable.flashlight_off);
                    break;
                case 0:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    imageView.setImageResource(R.drawable.flashlight_auto);
                    break;
                case 1:
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    imageView.setImageResource(R.drawable.flashlight_on);
                    break;
            }
            mCamera.setParameters(parameters);
        } else {
            switch (flashLigtMode) {
                case -1:
                    imageView.setImageResource(R.drawable.flashlight_off);
                    break;
                case 0:
                    imageView.setImageResource(R.drawable.flashlight_auto);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.flashlight_on);
                    break;
            }
        }
    }

    /**
     * 点击聚焦
     *
     * @param autoFocusCallback
     * @return
     */
    public boolean autoFocus(Camera.AutoFocusCallback autoFocusCallback) {
        if (mCamera == null || !isPreview) {
            return false;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        List<String> supportMode = parameters.getSupportedFocusModes();
        if (supportMode != null && supportMode.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            String focusMode = parameters.getFocusMode();
            if (!Camera.Parameters.FOCUS_MODE_AUTO.equals(focusMode)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                mCamera.setParameters(parameters);
            }
            if (autoFocusCallback != null) {
                if (isPreview) {
                    mCamera.autoFocus(autoFocusCallback);
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 设置聚焦的区域
     * 1.有些机型不满足setFocusArea
     * 2.一定要确保area的值在-1000,1000之间
     *
     * @param mContext
     * @param point
     */
    public void setFocusArea(Context mContext, Point point) {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        if (!isSupportFocusArea()) {
            return;
        }
        //值一定要在-1000,1000之间
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
            mCamera.setParameters(parameters);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    /**
     * 是否符合设置对焦区域的SDK版本
     *
     * @return
     */
    private boolean isSupportFocusArea() {
        if (mCamera == null) return false;
        if (Build.VERSION.SDK_INT >= 14) {
            Camera.Parameters parameters = mCamera.getParameters();
            if (parameters == null)
                return false;
            //If the value is 0, focus area is not supported,会使APP卡死
            if (parameters.getMaxNumFocusAreas() <= 0) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 拍照时的动作
     * 默认会有咔嚓一声
     */
    private class ShutCallBackImpl implements Camera.ShutterCallback {
        @Override
        public void onShutter() {

        }
    }

    //预览尺寸
    private void setPreviewSize(int width, int heigh) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> sizeList = parameters.getSupportedPreviewSizes();
        Camera.Size size = getFitPreviewSize(sizeList, width, heigh);
        parameters.setPreviewSize(size.width, size.height);
        mCamera.setParameters(parameters);
    }

    //照片分辨率
    private Camera.Size setPictureSize(int width, int heigh) {
        Camera.Parameters parameters = mCamera.getParameters();
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        Camera.Size size = getFitPictureSize(pictureSizes, width, heigh);
        parameters.setPictureSize(size.width, size.height);
        mCamera.setParameters(parameters);
        return size;
    }

    private Camera.Size getFitPictureSize(List<Camera.Size> sizeList, int width, int height) {
        // 按照尺寸从小到大给size排序
        Comparator comp1 = new SortSizeHeightComparator();
        Collections.sort(sizeList, comp1);
        Comparator comp = new SortSizeWidthComparator();
        Collections.sort(sizeList, comp);
        //寻找一个完美适配的size
        for (Camera.Size s : sizeList) {
            if (width == s.width && height == s.height) {
                return s;
            }
        }

        // 筛选出图像比例和要求的一样的size, 按照尺寸从小到大排列
        List<Camera.Size> goodSizeList = new ArrayList<>();
        for (Camera.Size s : sizeList) {
            if (s.height * width == s.width * height) {
                goodSizeList.add(s);
            }
        }
        // 找到比要求尺寸大的，最接近的尺寸
        for (Camera.Size s : goodSizeList) {
            if (width <= s.width && height <= s.height) {
                Log.d("PictureSize", "getFitPictureSize-----找到比要求尺寸大的，最接近的尺寸--" + s.width + "," + s.height);
                return s;
            }
        }

        // 找到其他尺寸中，高比要求尺寸大的，最接近的尺寸
        for (Camera.Size s : sizeList) {
            if (height <= s.height) {
                Log.d("PictureSize", "getFitPictureSize-----找到其他尺寸中，高比要求尺寸大的，最接近的尺寸--" + s.width + "," + s.height);
                return s;
            }
        }

        return sizeList.get(sizeList.size() - 1);
    }

    private Camera.Size getFitPreviewSize(List<Camera.Size> sizeList, int width, int height) {
        // 按照尺寸从小到大给size排序
        Comparator comp1 = new SortSizeHeightComparator();
        Collections.sort(sizeList, comp1);
        Comparator comp = new SortSizeWidthComparator();
        Collections.sort(sizeList, comp);
        Collections.reverse(sizeList);
        //寻找一个完美适配的size
        for (Camera.Size s : sizeList) {
            if (width == s.width && height == s.height) {
                return s;
            }
        }

        // 筛选出图像比例和要求的一样的size, 按照尺寸从大到小排列
        List<Camera.Size> goodSizeList = new ArrayList<>();
        for (Camera.Size s : sizeList) {
            if (s.height * width == s.width * height) {
                goodSizeList.add(s);
            }
        }
        // 找到比要求尺寸小的，最接近的尺寸
        for (Camera.Size s : goodSizeList) {
            if (width >= s.width && height >= s.height) {
                return s;
            }
        }

        // 找到其他尺寸中，宽比要求尺寸小的，最接近的尺寸
        for (Camera.Size s : sizeList) {
            if (width >= s.width) {
                return s;
            }
        }

        return sizeList.get(sizeList.size() - 1);
    }


    private int getFacingCameraId(int facing) {
        int cameraId = -1;
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == facing) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }


    public int getCameraId() {
        return cameraId;
    }


    public Camera.CameraInfo getmCameraInfo() {
        return mCameraInfo;
    }

    public Camera getCamera() {
        return mCamera;
    }

    public Camera.Parameters getCameraParaters() {
        if (mCamera != null) {
            return mCamera.getParameters();
        }
        return null;
    }

    public class SortSizeWidthComparator implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            Camera.Size a = (Camera.Size) lhs;
            Camera.Size b = (Camera.Size) rhs;
            return (a.width - b.width);
        }
    }

    public class SortSizeHeightComparator implements Comparator {
        @Override
        public int compare(Object lhs, Object rhs) {
            Camera.Size a = (Camera.Size) lhs;
            Camera.Size b = (Camera.Size) rhs;
            return (a.height - b.height);
        }
    }

}
