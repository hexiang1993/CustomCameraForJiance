package com.car300.customcamera;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.car300.customcamera.data.CameraConstants;
import com.car300.customcamera.utils.CameraUtil;
import com.car300.customcamera.utils.FileUtil;
import com.car300.customcamera.utils.ImageLoader;
import com.car300.customcamera.utils.ImageUtil;
import com.car300.customcamera.utils.PersistUtil;
import com.car300.customcamera.utils.PhotoUtil;
import com.car300.customcamera.utils.ToastUtil;
import com.car300.customcamera.view.CameraLayout;
import com.car300.customcamera.vindriver.DriverLicenseHelp;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xhe.photoalbum.PhotoAlbum;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by xhe on 2017/4/12.
 * 行驶证识别相机
 */

public class LicenseIndentifyCameraActivity extends BaseActivity implements Camera.PictureCallback, View.OnClickListener {

    private ImageView btnFlashLight;
    private ImageView btnSystemPhoto;
    private RelativeLayout btnTakePic;
    private RelativeLayout btnRetakePic;
    private TextView btnFinish;
    private ImageView ivPhoto;
    private ImageView ivGuideLine;

    private boolean mIsSaving = false;//是否正在保存图片
    private LinearLayout leftLL;
    private RelativeLayout rightRl;
    private String mPhotoPath;
    private CameraLayout cameraLayout;
    private Bitmap mBitmap;


    @Override
    public int getLayoutID() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.layout_custom_license_camera;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        initView();
        setViewMode();
    }

    private void initView() {
        //闪光灯
        btnFlashLight = (ImageView) findViewById(R.id.iv_flashlight);
        btnFlashLight.setOnClickListener(this);
        //系统相册
        btnSystemPhoto = (ImageView) findViewById(R.id.iv_choose_photo);
        btnSystemPhoto.setOnClickListener(this);
        //拍照
        btnTakePic = (RelativeLayout) findViewById(R.id.rl_shutter_camera);
        btnTakePic.setOnClickListener(this);
        //重拍
        btnRetakePic = (RelativeLayout) findViewById(R.id.rl_re_camera);
        btnRetakePic.setOnClickListener(this);
        // 完成/返回
        btnFinish = (TextView) findViewById(R.id.tv_finish);
        btnFinish.setOnClickListener(this);

        //照片展示
        ivPhoto = (ImageView) findViewById(R.id.iv_photo_show);
        //引导图
        ivGuideLine = (ImageView) findViewById(R.id.iv_guide_line);

        leftLL = (LinearLayout) findViewById(R.id.ll_left);
        rightRl = (RelativeLayout) findViewById(R.id.rl_right);
        cameraLayout = (CameraLayout) findViewById(R.id.camera_layout);

    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        new RxPermissions(this)
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (!granted) {
                            //权限被拒绝了
                            ToastUtil.show(mContext, "您没有授权相机或者文件存储权限，将无法拍照并保存下来，请到设置中打开");
                            return;
                        }
                        dealClickListner(id);
                    }
                });


    }

    private void dealClickListner(int id) {
        if (id == btnTakePic.getId()) {//拍照
            leftLL.setVisibility(View.INVISIBLE);
            rightRl.setVisibility(View.INVISIBLE);
            setButtonClickable(false);//禁止按钮的点击事件
            CameraUtil.getInstance().doTakePic(this);
            return;
        }

        if (id == btnRetakePic.getId()) {//重拍
            ivPhoto.setVisibility(View.GONE);
            checkButtonVisible();
            return;
        }

        if (id == btnFlashLight.getId()) {//闪光灯
            setFlashLightMode();
            return;
        }

        if (id == btnSystemPhoto.getId()) {//系统相册
            CameraUtil.getInstance().doOpenSystemPhoto(LicenseIndentifyCameraActivity.this, new PhotoAlbum.ActivityForResultCallBack() {
                @Override
                public void result(Intent intent) {
                    if (intent == null) {
                        return;
                    }
                    dealPhotoAlbumReturn(intent);
                }
            });
            return;
        }

        if (id == btnFinish.getId()) {
            if (btnFinish.getText().equals("确定")) {
                //图片保存完毕后结束，回传到上一界面
                if (mIsSaving) {
                    ToastUtil.show(mContext, "正在处理图片");
                    return;
                }
                DriverLicenseHelp.vinCallBack.path(mPhotoPath);
            }
            finish();
        }
    }

    /**
     * 控制所有按钮的点击使能
     * 点拍照的时候设为不能点击
     * 照片返回的时候设为可点击
     *
     * @param clickable
     */
    private void setButtonClickable(boolean clickable) {
        btnFinish.setClickable(clickable);
        btnSystemPhoto.setClickable(clickable);
        btnTakePic.setClickable(clickable);
        btnRetakePic.setClickable(clickable);
    }


    /**
     * 设置界面各种view的状态
     */
    private void setViewMode() {

        checkButtonVisible();

        /**引导线*/
        ivGuideLine.setBackgroundResource(R.drawable.icon_guide_line_driver);

        /**闪光灯*/
        CameraUtil.getInstance().setFlashMode(mContext, btnFlashLight);

        /**系统相册*/
        btnSystemPhoto.setVisibility(View.VISIBLE);

    }

    /**
     * 检查按钮的隐藏与显示
     *
     * @author xhe
     * @date 2017/4/10 14:45
     */
    private void checkButtonVisible() {
        //按钮 依据当前照片是否展示
        btnFinish.setVisibility(View.VISIBLE);
        if (ivPhoto.getVisibility() == View.VISIBLE) {//照片展示的时候
            btnFinish.setText("确定");
            btnFlashLight.setVisibility(View.INVISIBLE);
//            btnSystemPhoto.setVisibility(View.INVISIBLE);
            btnTakePic.setVisibility(View.INVISIBLE);
            btnRetakePic.setVisibility(View.VISIBLE);
            cameraLayout.setVisibility(View.INVISIBLE);
        } else {//照片不展示的时候
            btnFinish.setText("返回");
            btnFlashLight.setVisibility(View.VISIBLE);
            btnSystemPhoto.setVisibility(View.VISIBLE);
            btnTakePic.setVisibility(View.VISIBLE);
            btnRetakePic.setVisibility(View.INVISIBLE);
            cameraLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置闪光灯模式
     *
     * @author xhe
     * @date 2017/4/10 16:33
     */
    private void setFlashLightMode() {
        int flashLigtMode = Integer.parseInt(PersistUtil.load(mContext, PersistUtil.CAMERA_FLASH_LIGHT, "0"));
        flashLigtMode++;
        flashLigtMode = flashLigtMode > 1 ? -1 : flashLigtMode;
        PersistUtil.save(mContext, PersistUtil.CAMERA_FLASH_LIGHT, flashLigtMode);
        CameraUtil.getInstance().setFlashMode(mContext, btnFlashLight);
    }

    /**
     * 压缩图片
     *
     * @param inputPath
     * @param subscriber
     * @param delete
     */
    private void scalImage(String inputPath, Subscriber<? super String> subscriber, boolean delete) {
        String outputPath = PhotoUtil.getPhotoSavePath(mContext);
        ImageUtil.scalImageSizeAndSave(mContext, ImageUtil.MAX_SIZE_PHOTO_DRIVER, inputPath, outputPath, delete);
        Log.i("Camera", "图片输出路径：" + outputPath);
        subscriber.onNext(outputPath);
        subscriber.onCompleted();
    }


    /**
     * 图片保存完毕之后的一些控件操作
     */
    private void finishSavePhoto() {
        hideLoading();
        //存储操作做完了，可以进行点击操作了
        leftLL.setVisibility(View.VISIBLE);
        rightRl.setVisibility(View.VISIBLE);
        mIsSaving = false;
        checkButtonVisible();
        setButtonClickable(true);
        Log.i("Camera", "图片存储结束");
    }

    /**
     * 异步保存图片
     *
     * @param observable
     */
    private void savePhoto(Observable<String> observable) {
        observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
//                        ivPhoto.setVisibility(View.VISIBLE);
                        ivGuideLine.setVisibility(View.INVISIBLE);
                        mIsSaving = true;
                        showLoading("处理中");
                    }
                })
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        ivPhoto.setVisibility(View.VISIBLE);
                        mPhotoPath = s;
                        ImageLoader.load(mPhotoPath, ivPhoto);
                        finishSavePhoto();
                        CameraUtil.getInstance().startPreview();
                    }
                });
    }

    /**
     * 拍照返回
     */
    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        if (data == null || data.length <= 0) {
            return;
        }
        CameraUtil.getInstance().setPreview(false);
        //展示照片
        //关闭硬件加速，防止OOM
        ImageUtil.releaseImageViewResouce(ivPhoto);
       /* mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        if (mBitmap != null) {
            ivPhoto.setImageBitmap(mBitmap);
        }*/
        //子线程中执行保存图片操作
        savePhoto(Observable
                .create(new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> subscriber) {
                        String path = PhotoUtil.getPhotoSavePath(mContext);
                        FileUtil.writeByteToSDFile(path, data);
                        scalImage(path, subscriber, true);
                    }
                }));
    }


    String resultPath;

    /**
     * 处理选择相册返回结果
     *
     * @param data
     */
    private void dealPhotoAlbumReturn(Intent data) {
        List<String> photos = PhotoAlbum.parseResult(data);
        if (photos == null || photos.size() <= 0) {
            return;
        }
        resultPath = photos.get(0);//这个路径是照片在相册中的本身路径，需要将图片复制到自己的路径下，再做处理,因为最后上传完数据会把照片也删掉
        //展示照片
        ImageUtil.releaseImageViewResouce(ivPhoto);
        ImageLoader.load(resultPath, ivPhoto);
        Log.i("Camera", "系统相册路径：" + resultPath);
        //异步存储图片
        savePhoto(Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                scalImage(resultPath, subscriber, false);
            }
        }));
    }

    @Override
    protected void onDestroy() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        mBitmap = null;
        super.onDestroy();
    }
}
