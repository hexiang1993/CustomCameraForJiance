package com.car300.customcamera.view;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.car300.customcamera.R;
import com.car300.customcamera.utils.CameraUtil;

/**
 * 对焦和相机界面
 */
public class CameraLayout extends RelativeLayout {

    private FocusImageView mFocusImageView;
    private Context mContext;


    public CameraLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        setOnTouchListener(new TouchListener());
    }

    private void initView(Context context) {
        inflate(context, R.layout.layout_camera_container, this);
        mContext = context;
        MySurfaceView mSurfaceView = (MySurfaceView) findViewById(R.id.cameraView);
        mFocusImageView = (FocusImageView) findViewById(R.id.focusImageView);
    }


    private final AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            if (success) {
                mFocusImageView.onFocusSuccess();
            } else {
                mFocusImageView.onFocusFailed();
            }
        }
    };

    private final class TouchListener implements OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_UP:
                    //对焦
                    Point point = new Point((int) event.getX(), (int) event.getY());
                    //xhe 优化相机对焦
                    CameraUtil.getInstance().autoFocus(autoFocusCallback);
                    CameraUtil.getInstance().setFocusArea(mContext,point);
                    mFocusImageView.startFocus(point);
                    break;
            }
            return true;
        }
    }

}