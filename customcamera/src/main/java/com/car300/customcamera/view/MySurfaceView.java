package com.car300.customcamera.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.car300.customcamera.utils.CameraUtil;

/**
 * Created by xhe on 2017/4/7.
 */

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;

    public MySurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceHolder = getHolder();
        surfaceHolder.setFormat(PixelFormat.TRANSPARENT);//translucent半透明 transparent透明
        surfaceHolder.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        CameraUtil.getInstance().doOpenCamera(getContext());
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        CameraUtil.getInstance().doStartPreview(surfaceHolder,getContext());
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        CameraUtil.getInstance().doStopCamera();
    }
}

