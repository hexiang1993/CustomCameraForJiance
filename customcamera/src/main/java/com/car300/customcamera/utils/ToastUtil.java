package com.car300.customcamera.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

//Toast统一管理类
public class ToastUtil {

    public static void show(Context context, @NonNull String message) {
//        Toasty.normal(context, message, Toast.LENGTH_SHORT).show();//需要引入 compile 'com.github.GrenderG:Toasty:1.1.4'
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    public static void showLong(Context context, @NonNull String message) {
//        Toasty.normal(context, message, Toast.LENGTH_LONG).show();
        Toast.makeText(context,message,Toast.LENGTH_LONG).show();
    }

}