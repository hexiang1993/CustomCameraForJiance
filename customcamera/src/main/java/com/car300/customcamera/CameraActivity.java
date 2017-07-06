package com.car300.customcamera;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.car300.customcamera.data.CameraConstants;
import com.car300.customcamera.data.PhotoInfo;
import com.car300.customcamera.utils.CameraUtil;
import com.car300.customcamera.utils.FileUtil;
import com.car300.customcamera.utils.ImageLoader;
import com.car300.customcamera.utils.ImageUtil;
import com.car300.customcamera.utils.LegendHelp;
import com.car300.customcamera.utils.PersistUtil;
import com.car300.customcamera.utils.PhotoUtil;
import com.car300.customcamera.utils.StringUtil;
import com.car300.customcamera.utils.ToastUtil;
import com.car300.customcamera.view.CameraLayout;
import com.tbruyelle.rxpermissions.RxPermissions;
import com.xhe.photoalbum.PhotoAlbum;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class CameraActivity extends BaseActivity implements Camera.PictureCallback, View.OnClickListener {

    private ImageView btnLegend;
    private ImageView btnFlashLight;
    private ImageView btnSystemPhoto;
    private RelativeLayout btnTakePic;
    private RelativeLayout btnRetakePic;
    private TextView btnFinish;
    private TextView btnPrePhoto;
    private TextView btnNextPhoto;
    private ImageView ivPhoto;
    private ImageView ivGuideLine;
    private TextView tvName;
    private TextView tvTips;
    private TextView tvIndex;
    private TextView tvRemindFocus;
    private CheckBox cbGuide;

    private List<PhotoInfo> mListPhotos = new ArrayList<>();
    private static int mCurrentIndex = 0;
    private boolean mIsAdditinalPic = false;//是否为附加照片
    private boolean mIsSaving = false;//是否正在保存图片
    private LinearLayout leftLL;
    private RelativeLayout rightRl;
    private CameraLayout cameraLayout;

    private static final String TAG = "CameraActivity";
    private Bitmap mBitmap;


    @Override
    public int getLayoutID() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return R.layout.layout_custom_camera;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null) {
            mCurrentIndex = intent.getIntExtra(CameraConstants.CURRENT_INDEX, 0);
            mIsAdditinalPic = intent.getBooleanExtra(CameraConstants.IS_ADDITINAL_PHOTO, false);
            List<PhotoInfo> list = intent.getParcelableArrayListExtra(CameraConstants.PHOTO_LIST);
            if (list != null) {
                mListPhotos.clear();
                mListPhotos.addAll(list);
            }
        }

        initView();

        setViewMode(true, null);
    }

    private void initView() {
        //图例按钮
        btnLegend = (ImageView) findViewById(R.id.iv_picture_legend);
        btnLegend.setOnClickListener(this);
        //引导图按钮
        cbGuide = (CheckBox) findViewById(R.id.cb_guide_line);
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
        //上一张
        btnPrePhoto = (TextView) findViewById(R.id.tv_take_pre);
        btnPrePhoto.setOnClickListener(this);
        //下一张
        btnNextPhoto = (TextView) findViewById(R.id.tv_take_next);
        btnNextPhoto.setOnClickListener(this);

        //照片展示
        ivPhoto = (ImageView) findViewById(R.id.iv_photo_show);
        //引导图
        ivGuideLine = (ImageView) findViewById(R.id.iv_guide_line);
        //照片名称
        tvName = (TextView) findViewById(R.id.tv_photo_name);
        //提示信息
        tvTips = (TextView) findViewById(R.id.tv_photo_tips);
        //照片张数
        tvIndex = (TextView) findViewById(R.id.tv_photo_index);
        //提示对焦
        tvRemindFocus = (TextView) findViewById(R.id.tv_remind_focusing);

        leftLL = (LinearLayout) findViewById(R.id.ll_left);
        rightRl = (RelativeLayout) findViewById(R.id.rl_right);
        cameraLayout = (CameraLayout) findViewById(R.id.camera_layout);

        cbGuide.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                PersistUtil.saveBoolean(mContext, PersistUtil.CAMERA_GUIDE_LINE, isChecked);
                if (isChecked) {
                    ivGuideLine.setVisibility(View.VISIBLE);
                } else {
                    ivGuideLine.setVisibility(View.GONE);
                }
            }
        });

        /**提示对焦的闪烁动画*/
        Animation animRemindFocus = new AlphaAnimation(0, 1);
        animRemindFocus.setDuration(1000);
        animRemindFocus.setInterpolator(new LinearInterpolator());
        animRemindFocus.setRepeatCount(3);
        animRemindFocus.setRepeatMode(Animation.REVERSE);
        tvRemindFocus.startAnimation(animRemindFocus);
        animRemindFocus.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvRemindFocus.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

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

    /**
     * 处理相应的点击事件
     *
     * @param id
     */
    private void dealClickListner(int id) {
        if (id == btnTakePic.getId()) {//拍照
            leftLL.setVisibility(View.INVISIBLE);
            rightRl.setVisibility(View.INVISIBLE);
            setButtonClickable(false);//禁止按钮的点击事件
            CameraUtil.getInstance().doTakePic(this);
            return;
        }

        if (id == btnRetakePic.getId()) {//重拍
            checkButtonVisible(false);
            return;
        }

        if (id == btnLegend.getId()) {//图例
            showPictureLegend();
            return;
        }

        if (id == btnFlashLight.getId()) {//闪光灯
            setFlashLightMode();
            return;
        }

        if (id == btnSystemPhoto.getId()) {//系统相册
            CameraUtil.getInstance().doOpenSystemPhoto(CameraActivity.this, new PhotoAlbum.ActivityForResultCallBack() {
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

        if (id == btnPrePhoto.getId()) {//上一张
            mCurrentIndex = mCurrentIndex > 0 ? mCurrentIndex - 1 : mCurrentIndex;
            setViewMode(true, null);
            return;
        }

        if (id == btnNextPhoto.getId()) {//下一张
            if (mCurrentIndex != mListPhotos.size() - 1) {
                mCurrentIndex++;
                setViewMode(true, null);
            } else {//最后一张
                finish();
            }
            return;
        }

        if (id == btnFinish.getId()) {
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
        btnPrePhoto.setClickable(clickable);
        btnNextPhoto.setClickable(clickable);
        btnFinish.setClickable(clickable);
        btnSystemPhoto.setClickable(clickable);
        btnTakePic.setClickable(clickable);
        btnRetakePic.setClickable(clickable);
    }


    /**
     * 设置界面各种view的状态
     */
    private void setViewMode(boolean showPhoto, String path) {
        PhotoInfo photoInfo = mListPhotos.get(mCurrentIndex);
        if (photoInfo == null) return;

        String imgPath;
        if (showPhoto) {
            imgPath = StringUtil.isEmpty(path) ? photoInfo.getPath_local() : path;
            imgPath = StringUtil.isEmpty(imgPath) ? photoInfo.getPath_qiniu() : imgPath;
            Log.d(TAG, "setViewMode()" + imgPath);
            if (StringUtil.isEmpty(imgPath)) {
                showPhoto = false;
                ivPhoto.setVisibility(View.GONE);
            } else {
                ivPhoto.setVisibility(View.VISIBLE);
                ImageLoader.load(imgPath, ivPhoto);
            }
        }

        String alias = photoInfo.getAlias();
        tvName.setText(alias);
        if (!mIsAdditinalPic) {
            tvTips.setText(photoInfo.getTips());
            tvIndex.setText(MessageFormat.format("{0}/{1}", mCurrentIndex + 1, mListPhotos.size()));
        } else {
            if (StringUtil.isEmpty(alias)) {
                tvName.setText("附加照片" + (mCurrentIndex + 1));
            }
        }

        checkButtonVisible(showPhoto);

        /**图例*/
        try {
            JSONObject object = new JSONObject(LegendHelp.legendJson);
            JSONObject obj = object.getJSONObject(photoInfo.getCategory_id() + "");
            if (obj == null) {
                btnLegend.setAlpha(0.2f);
                btnLegend.setClickable(false);
            } else {
                btnLegend.setAlpha(1.0f);//要用float型
                btnLegend.setClickable(true);
            }
        } catch (JSONException e) {
            btnLegend.setAlpha(0.2f);
            btnLegend.setClickable(false);
        }

        /**引导线*/
        boolean isOpenGuide = PersistUtil.loadBoolean(mContext, PersistUtil.CAMERA_GUIDE_LINE, true);
        cbGuide.setChecked(isOpenGuide);
        ImageLoader.load(photoInfo.getGuide_image(), ivGuideLine);
        if (mIsAdditinalPic) {//附加照片没有引导线
            cbGuide.setBackgroundResource(R.drawable.icon_guideline_default);
            cbGuide.setClickable(false);
        }

        /**闪光灯*/
        CameraUtil.getInstance().setFlashMode(mContext, btnFlashLight);

        /**系统相册*/
        boolean canSystemPhoto = photoInfo.isCanSystemPhoto();
        if (canSystemPhoto) {
            btnSystemPhoto.setVisibility(View.VISIBLE);
        } else {
            btnSystemPhoto.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * 检查按钮的隐藏与显示
     *
     * @author xhe
     * @date 2017/4/10 14:45
     */
    private void checkButtonVisible(boolean showPhoto) {
        Log.d(TAG, "checkButtonVisible()  showPhoto=" + showPhoto);

        //上一张 按钮
        if (mCurrentIndex > 0) {
            btnPrePhoto.setVisibility(View.VISIBLE);
        } else {
            btnPrePhoto.setVisibility(View.INVISIBLE);
        }

        //下一张
        if (mCurrentIndex != mListPhotos.size() - 1) {
            btnNextPhoto.setVisibility(View.VISIBLE);
            btnNextPhoto.setText("下一张");
            btnFinish.setVisibility(View.VISIBLE);
        } else {//最后一张
            if (mIsAdditinalPic) {
                btnNextPhoto.setVisibility(View.GONE);
                btnFinish.setVisibility(View.VISIBLE);
            } else {
                btnNextPhoto.setText("确定");
                btnNextPhoto.setVisibility(View.VISIBLE);
                btnFinish.setVisibility(View.GONE);
            }
        }

        //按钮 依据当前照片是否展示
        if (showPhoto) {//照片展示的时候
            btnFinish.setText("确定");
            btnLegend.setVisibility(View.INVISIBLE);
            cbGuide.setVisibility(View.INVISIBLE);
            ivGuideLine.setVisibility(View.INVISIBLE);
            btnFlashLight.setVisibility(View.INVISIBLE);
            btnTakePic.setVisibility(View.INVISIBLE);
            btnRetakePic.setVisibility(View.VISIBLE);
            cameraLayout.setVisibility(View.INVISIBLE);
            ivPhoto.setVisibility(View.VISIBLE);
        } else {//照片不展示的时候
            btnFinish.setText("返回");
            btnLegend.setVisibility(View.VISIBLE);
            cbGuide.setVisibility(View.VISIBLE);
            btnFlashLight.setVisibility(View.VISIBLE);
            btnTakePic.setVisibility(View.VISIBLE);
            btnRetakePic.setVisibility(View.INVISIBLE);
            cameraLayout.setVisibility(View.VISIBLE);
            /**引导线*/
            boolean isOpenGuide = PersistUtil.loadBoolean(mContext, PersistUtil.CAMERA_GUIDE_LINE, true);
            cbGuide.setChecked(isOpenGuide);
            if (isOpenGuide) {
                ivGuideLine.setVisibility(View.VISIBLE);
            } else {
                ivGuideLine.setVisibility(View.GONE);
            }
            ivPhoto.setVisibility(View.GONE);
        }

        if (mListPhotos.get(mCurrentIndex).isCanSystemPhoto()) {
            btnSystemPhoto.setVisibility(View.VISIBLE);
        } else {
            btnSystemPhoto.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 展示图例pop
     */
    private void showPictureLegend() {
        PopupWindow popupWindow = LegendHelp.getPop(this, mListPhotos.get(mCurrentIndex).getCategory_id());
        if (popupWindow != null) {
            popupWindow.showAtLocation(findViewById(R.id.ll_camera), Gravity.CENTER, 0, 0);
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
     * 设置照片信息的路径
     *
     * @param index
     * @param path
     */
    private void setPathToList(int index, String path) {
        //删除之前的照片
        String prePath = mListPhotos.get(index).getPath_local();
        FileUtil.deleteFile(prePath);
        Log.d(TAG, index + "----存储照片路径：" + path);
        mListPhotos.get(index).setPath_local(path);
        mListPhotos.get(index).setModified(true);//照片修改过
        if (mIsAdditinalPic) {
            mListPhotos.get(index).setName("附加照片" + (index + 1));
        }
    }


    /**
     * 压缩图片
     *
     * @param inputPath
     * @param subscriber
     * @param delete
     */
    private void scalImage(int index, String inputPath, Subscriber<? super Map<Integer, String>> subscriber, boolean delete) {
        String outputPath = PhotoUtil.getPhotoSavePath(mContext);
        ImageUtil.scalImageSizeAndSave(mContext, ImageUtil.MAX_SIZE_PHOTO_DRIVER, inputPath, outputPath, delete);
        Map<Integer, String> map = new HashMap<Integer, String>();
        map.put(index, outputPath);
        subscriber.onNext(map);
        subscriber.onCompleted();
    }

    /**
     * 异步保存图片
     *
     * @param observable
     */
    private void savePhoto(Observable<Map<Integer, String>> observable) {
        observable
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        mIsSaving = true;
                        showLoading("处理中");
                    }
                })
                .subscribe(new Action1<Map<Integer, String>>() {
                    @Override
                    public void call(Map<Integer, String> map) {
                        for (Map.Entry<Integer, String> entry : map.entrySet()) {
                            int index = entry.getKey();
                            String path = entry.getValue();
                            ivPhoto.setVisibility(View.VISIBLE);
                            ImageLoader.load(path, ivPhoto);
                            setPathToList(index, path);
                            break;
                        }
                        finishSavePhoto();
                        CameraUtil.getInstance().startPreview();
                    }
                });
    }

    /**
     * 图片保存完毕之后的一些控件操作
     */
    private void finishSavePhoto() {
        //处理附加照片的下一张
        if (mIsAdditinalPic) {
            if (mListPhotos != null && mCurrentIndex == mListPhotos.size() - 1 && mListPhotos.size() > 0 && mListPhotos.size() < CameraConstants.MAX_ADDITONAL_PHOTO_COUNT) {
                long task_id = mListPhotos.get(0).getTask_id();
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.setTask_id(task_id);
                photoInfo.setCategory_id(CameraConstants.CAT_ID_ADDITINAL);
                mListPhotos.add(photoInfo);
            }
        }
        //存储操作做完了，可以进行点击操作了
        leftLL.setVisibility(View.VISIBLE);
        rightRl.setVisibility(View.VISIBLE);
        mIsSaving = false;
        checkButtonVisible(true);
        setButtonClickable(true);
        hideLoading();
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
//        ivPhoto.setVisibility(View.VISIBLE);
//        mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//        if (mBitmap != null) {
//            if (PhotoUtil.isNeedCompressToShow(mBitmap.getWidth(), mBitmap.getHeight())) {//大于手机支持显示的宽高，需要压缩显示
//                Log.e(TAG, "大于手机支持显示的宽高，需要压缩显示，但是存放的时候还是原来的bitmap" + mCurrentIndex);
//                //图片尺寸过大时，压缩显示，但是存储的时候还是存储原图
//                BitmapFactory.Options options = new BitmapFactory.Options();
//                options.inSampleSize = 2;
//                mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
//                if (mBitmap != null) {
//                    ivPhoto.setImageBitmap(mBitmap);
//                }
//            } else {
//                ivPhoto.setImageBitmap(mBitmap);
//            }
//        }
        //子线程中执行保存图片操作
        savePhoto(Observable
                .create(new Observable.OnSubscribe<Map<Integer, String>>() {
                    @Override
                    public void call(Subscriber<? super Map<Integer, String>> subscriber) {
                        int index = mCurrentIndex;
                        String path = PhotoUtil.getPhotoSavePath(mContext);
                        FileUtil.writeByteToSDFile(path, data);
                        scalImage(index, path, subscriber, true);
                    }
                }));
    }


    int index;
    String resultPath;

    /**
     * 选择相册返回
     */
    private void dealPhotoAlbumReturn(Intent data) {
        List<String> photos = PhotoAlbum.parseResult(data);
        if (photos == null || photos.size() <= 0) {
            return;
        }
        index = mCurrentIndex;
        resultPath = photos.get(0);//这个路径是照片在相册中的本身路径，需要将图片复制到自己的路径下，再做处理,因为最后上传完数据会把照片也删掉
        //展示照片
        ivPhoto.setVisibility(View.VISIBLE);
        ivGuideLine.setVisibility(View.INVISIBLE);
        ImageUtil.releaseImageViewResouce(ivPhoto);
        ImageLoader.load(resultPath, ivPhoto);

        //异步存储图片
        savePhoto(Observable.create(new Observable.OnSubscribe<Map<Integer, String>>() {
            @Override
            public void call(Subscriber<? super Map<Integer, String>> subscriber) {
                scalImage(index, resultPath, subscriber, true);
            }
        }));
    }

    @Override
    public void finish() {
        //图片保存完毕后结束，回传到上一界面
        if (mIsSaving) {
            ToastUtil.show(mContext, "正在保存图片");
            return;
        }
        Log.d(TAG, "finish()：" + mListPhotos.toString());
        hideLoading();
        Intent intent = new Intent();
        intent.putExtra(CameraConstants.PHOTO_LIST, (Serializable) mListPhotos);
        setResult(RESULT_OK, intent);
        super.finish();
    }

    @Override
    protected void onDestroy() {
        ImageUtil.releaseImageViewResouce(ivPhoto);
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.recycle();
        }
        mBitmap = null;
        super.onDestroy();
    }
}
