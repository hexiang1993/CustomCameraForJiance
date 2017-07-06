package com.car300.customcamera;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.car300.customcamera.view.LoadingDialog;


/**
 * Created by xhe on 2017/4/12.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private LoadingDialog mLoading;
    public Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutID());
        mContext = this;
        mLoading = new LoadingDialog(this);
        init(savedInstanceState);
    }

    public abstract int getLayoutID();

    public abstract void init(Bundle savedInstanceState);

    public void showLoading() {
        if (mLoading != null) {
            mLoading.show();
        }
    }

    public void showLoading(String msg) {
        if (mLoading != null) {
            mLoading.setLoadingText(msg);
            mLoading.show();
        }
    }

    public void hideLoading() {
        if (mLoading != null) {
            mLoading.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        hideLoading();
        mLoading = null;
        super.onDestroy();
    }
}
