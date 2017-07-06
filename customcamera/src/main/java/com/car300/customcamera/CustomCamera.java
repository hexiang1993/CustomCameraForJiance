package com.car300.customcamera;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.car300.customcamera.data.CameraConstants;
import com.car300.customcamera.data.PhotoInfo;
import com.car300.customcamera.utils.CameraUtil;
import com.car300.customcamera.vindriver.DriverLicenseHelp;
import com.gengqiquan.result.RxActivityResult;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;

/**
 * Created by xhe on 2017/7/6.
 */

public class CustomCamera {
    /**
     * 普通相机的返回
     *
     * @param data
     * @return
     */
    public static List<PhotoInfo> getNormalResult(Intent data) {
        if (data == null) {
            return null;
        }
        return data.getParcelableArrayListExtra(CameraConstants.PHOTO_LIST);
    }

    public static String getResultPath(Intent data) {
        if (data == null) {
            return null;
        }
        return data.getStringExtra(CameraConstants.PHOTO_PATH);
    }


    /**
     * 打开行驶证识别相机
     */
    public static void doTakeVinCamera(final Activity activity, @NonNull final DriverLicenseHelp.ICallBack callBack) {
        RxActivityResult.with(activity)
                .startActivityWithResult(new Intent(activity, LicenseIndentifyCameraActivity.class))
                .subscribe(new Subscriber<Intent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Intent intent) {
                        if (intent != null && callBack != null) {
                            String path = intent.getStringExtra(CameraConstants.PHOTO_PATH);
                            DriverLicenseHelp.indentifyVin(activity, path, callBack);
                        }
                    }
                });
    }

    /**
     * 打开事故判定界面的相机
     *
     * @param activity
     * @param path
     * @param resultCallback
     */
    public static void doTakeAccidentCamera(Activity activity, String path, @NonNull final ActivityForResultCallback resultCallback) {
        Intent intent = new Intent(activity, AccidentCameraActivity.class);
        intent.putExtra(CameraConstants.PHOTO_PATH, path);
        RxActivityResult.with(activity)
                .startActivityWithResult(intent)
                .subscribe(new Subscriber<Intent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Intent intent) {
                        if (resultCallback != null) {
                            resultCallback.result(intent);
                        }
                    }
                });
    }

    /**
     * 打开普通照片的相机
     *
     * @param activity
     * @param list
     * @param index
     * @param isAdditional
     * @param resultCallback
     */
    public static void doTakeNormalCamera(Activity activity, ArrayList<PhotoInfo> list, int index, boolean isAdditional, @NonNull final ActivityForResultCallback resultCallback) {
        RxActivityResult.with(activity)
                .startActivityWithResult(getCameraIntent(activity, list, index, isAdditional))
                .subscribe(new Subscriber<Intent>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Intent intent) {
                        if (resultCallback != null) {
                            resultCallback.result(intent);
                        }
                    }
                });
    }


    /**
     * 普通相机界面的intent
     *
     * @param activity
     * @param list
     * @param index        当前一张的index
     * @param isAdditional 是否是附加照片
     */
    private static Intent getCameraIntent(Activity activity, ArrayList<PhotoInfo> list, int index, boolean isAdditional) {
        Intent intent = new Intent(activity, CameraActivity.class);
        intent.putExtra(CameraConstants.CURRENT_INDEX, index);
        intent.putExtra(CameraConstants.IS_ADDITINAL_PHOTO, isAdditional);
        intent.putParcelableArrayListExtra(CameraConstants.PHOTO_LIST, list);
        return intent;
    }

    public interface ActivityForResultCallback {
        void result(Intent data);
    }
}
