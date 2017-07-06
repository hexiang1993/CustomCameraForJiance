package com.car300.customcamera.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.car300.customcamera.R;
import com.car300.customcamera.utils.ImageLoader;
import com.car300.customcamera.utils.StringUtil;


/**
 * Created by xhe on 2017/4/12.
 */

public class LoadingDialog {
    Activity mActivity;
    private View mView;
    private ImageView mIvLoading;
    private Dialog mDialog;
    private TextView mTvLoading;

    public LoadingDialog(Activity activity) {
        this.mActivity = activity;
        initView();
    }

    private void initView() {
        mView = LayoutInflater.from(mActivity).inflate(R.layout.load_dialog_content, null);
        mIvLoading = (ImageView) mView.findViewById(R.id.iv_loading);
        mTvLoading = (TextView) mView.findViewById(R.id.tv_loading);
        ImageLoader.loadGif(R.drawable.anim_loading2, mIvLoading);
        mDialog = new Dialog(mActivity, R.style.DialogStyle);
        mDialog.setContentView(mView);
        Window dialogWindow = mDialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        dialogWindow.setBackgroundDrawableResource(android.R.color.transparent);//不出现黑棱角
    }

    public void show() {
        if (mActivity==null||mActivity.isFinishing()||mDialog == null || mDialog.isShowing()) {
            return;
        }

        mDialog.show();
    }

    public void setLoadingText(String msg) {
        if (mTvLoading != null && !StringUtil.isEmpty(msg)) {
            mTvLoading.setText(msg);
        }
    }

    public void setCancelable(boolean cancelable) {
        if (mDialog != null) {
            mDialog.setCancelable(cancelable);
        }
    }

    public void dismiss() {
        if (mActivity==null||mActivity.isFinishing()||mDialog == null || !mDialog.isShowing()) {
            return;
        }
        mDialog.dismiss();
    }

    public boolean isShowing() {
        if (mDialog == null) return false;
        return mDialog.isShowing();
    }
}
