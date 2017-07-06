package com.car300.customcamera.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.HONEYCOMB;


/**
 * Created by 耿 on 2016/7/13.
 */
public class ImageLoader {
    public final static String DEFAULT_DISK_CACHE_DIR = "image_manager_disk_cache";


    public static void init(Context context) {
        context = context.getApplicationContext();
        int memoryCacheSize = calculateMemoryCacheSize(context);
        if (memoryCacheSize > 1024 * 1024 * 20) {
            memoryCacheSize = 1024 * 1024 * 20;
        }

        GlideBuilder builder = new GlideBuilder(context);
        builder.setMemoryCache(new LruResourceCache(memoryCacheSize));
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, 100 * 1024 * 1024));
        //  builder.setDiskCache(new ExternalCacheDiskCacheFactory(context, DEFAULT_DISK_CACHE_DIR, 100 * 1024 * 1024));
        builder.setBitmapPool(new LruBitmapPool(20 * 1024 * 1024));
        builder.setDecodeFormat(DecodeFormat.PREFER_RGB_565);
        Glide.setup(builder);

    }

    public static void load(String url, ImageView imageView) {
        if (url==null||url.trim().length()==0) {
            return;
        }
        if (!url.startsWith("http") && !url.startsWith("file")) {
            url = FileUtil.LOCAL_FILE_URI_PREFIX + url;
        }
        Glide.with(imageView.getContext()).load(url).skipMemoryCache(false).dontAnimate().into(imageView);
    }

    public static void load(String url, int default_img_id,ImageView imageView) {
        if (url==null||url.trim().length()==0) {
            loadDrawable(default_img_id,imageView);
            return;
        }
        if (!url.startsWith("http") && !url.startsWith("file")) {
            url = FileUtil.LOCAL_FILE_URI_PREFIX + url;
        }
        Glide.with(imageView.getContext()).load(url).skipMemoryCache(false).dontAnimate().into(imageView);
    }

    public static void loadDrawable(int img_id,ImageView imageView){
        Glide.with(imageView.getContext()).load(img_id).dontAnimate().into(imageView);
    }

    public static void loadGif(int img_id,ImageView imageView){
        Glide.with(imageView.getContext()).load(img_id).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }


    public static int calculateMemoryCacheSize(Context context) {
        ActivityManager am = getService(context, ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags & FLAG_LARGE_HEAP) != 0;
        int memoryClass = am.getMemoryClass();
        if (largeHeap && SDK_INT >= HONEYCOMB) {
            memoryClass = ActivityManagerHoneycomb.getLargeMemoryClass(am);
        }
        // Target ~15% of the available heap.
        return 1024 * 1024 * memoryClass / 7;
    }

    @TargetApi(HONEYCOMB)
    public static class ActivityManagerHoneycomb {
        static int getLargeMemoryClass(ActivityManager activityManager) {
            return activityManager.getLargeMemoryClass();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T getService(Context context, String service) {
        return (T) context.getSystemService(service);
    }
}
