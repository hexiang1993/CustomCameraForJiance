package com.car300.customcamera.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PersistUtil {
    public static final String CAMERA_FACING_BACK = "camera.facing.back";
    public static final String CAMERA_GUIDE_LINE = "camera.guide.line2";
    public static final String CAMERA_FLASH_LIGHT = "camera.flash.light";

    public static void saveBoolean(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean loadBoolean(Context context, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static void save(Context context, String key, Object value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value == null)
            editor.remove(key);
        else
            editor.putString(key, value.toString());
        editor.apply();
    }

    public static String load(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("share", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }
}
