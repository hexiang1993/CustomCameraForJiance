package com.car300.customcamera.utils;

import android.content.Context;
import android.opengl.GLES10;
import android.os.Build;
import android.util.Log;

import java.io.File;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

/**
 * Created by xhe on 2017/4/11.
 */

public class PhotoUtil {
    public static File getPhotoSaveFolder(Context context){
        return StorageFolderUtil.getPhotoFolder(context);
    }

    public static String getPhotoSavePath(Context c) {
        return getPhotoSavePath(c, null);
    }

    public static String getPhotoSavePath(Context c, String fileName) {
        if (StringUtil.isEmpty(fileName)) {
            fileName = "PHOTO_" + System.currentTimeMillis() + ".jpg";
        }
        return new File(getPhotoSaveFolder(c), fileName).getAbsolutePath();
    }


    /**
     * 照片预览的时候可能由于照片过大不能显示
     * Bitmap too large to be uploaded into a view
     *
     * @param w
     * @param h
     * @return
     */
    public static boolean isNeedCompressToShow(int w, int h) {
        int maxSize = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            maxSize = getGLESTextureLimitEqualAboveLollipop();
        } else {
            maxSize = getGLESTextureLimitBelowLollipop();
        }
        Log.e("CustomCamera", "手机支持显示：" + maxSize + "，照片实际的：" + w + "," + h);
        if (maxSize < h || maxSize < w) {
            return true;
        }

        return false;
    }

    private static int getGLESTextureLimitBelowLollipop() {
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        return maxSize[0];
    }

    private static int getGLESTextureLimitEqualAboveLollipop() {
        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] vers = new int[2];
        egl.eglInitialize(dpy, vers);
        int[] configAttr = {
                EGL10.EGL_COLOR_BUFFER_TYPE, EGL10.EGL_RGB_BUFFER,
                EGL10.EGL_LEVEL, 0,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        egl.eglChooseConfig(dpy, configAttr, configs, 1, numConfig);
        if (numConfig[0] == 0) {// TROUBLE! No config found.
        }
        EGLConfig config = configs[0];
        int[] surfAttr = {
                EGL10.EGL_WIDTH, 64,
                EGL10.EGL_HEIGHT, 64,
                EGL10.EGL_NONE
        };
        EGLSurface surf = egl.eglCreatePbufferSurface(dpy, config, surfAttr);
        final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;  // missing in EGL10
        int[] ctxAttrib = {
                EGL_CONTEXT_CLIENT_VERSION, 1,
                EGL10.EGL_NONE
        };
        EGLContext ctx = egl.eglCreateContext(dpy, config, EGL10.EGL_NO_CONTEXT, ctxAttrib);
        egl.eglMakeCurrent(dpy, surf, surf, ctx);
        int[] maxSize = new int[1];
        GLES10.glGetIntegerv(GLES10.GL_MAX_TEXTURE_SIZE, maxSize, 0);
        egl.eglMakeCurrent(dpy, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE,
                EGL10.EGL_NO_CONTEXT);
        egl.eglDestroySurface(dpy, surf);
        egl.eglDestroyContext(dpy, ctx);
        egl.eglTerminate(dpy);

        return maxSize[0];
    }

}
