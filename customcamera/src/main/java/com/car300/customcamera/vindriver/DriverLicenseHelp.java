package com.car300.customcamera.vindriver;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.car300.customcamera.utils.CameraUtil;
import com.car300.customcamera.utils.FileUtil;
import com.car300.customcamera.utils.ImageUtil;
import com.car300.customcamera.utils.SharedPreUtil;
import com.car300.customcamera.utils.StringUtil;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

/**
 * Created by xhe on 2017/4/17.
 * 行驶证识别
 */

public class DriverLicenseHelp {
    public static IVinCallBack vinCallBack;

    public static void indentifyVinByAliyun(final Context context, final ICallBack callBack) {
        CameraUtil.getInstance().doTakeDriverCameraActivity(context);
        vinCallBack = new IVinCallBack() {
            @Override
            public void path(final String photoPath) {
                Observable
                        .create(new Observable.OnSubscribe<DriverLicenseInfo>() {
                            @Override
                            public void call(Subscriber<? super DriverLicenseInfo> subscriber) {
                                subscriber.onNext(requestAliApi(context, photoPath));
                                subscriber.onCompleted();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(new Action0() {
                            @Override
                            public void call() {
                                callBack.start();
                            }
                        })
                        .subscribe(new Subscriber<DriverLicenseInfo>() {
                            @Override
                            public void onCompleted() {
                                //识别结束把照片删掉
                                FileUtil.deleteFile(photoPath);
                                vinCallBack = null;
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                vinCallBack = null;
                                callBack.failure("识别失败,请手动输入");
                            }

                            @Override
                            public void onNext(DriverLicenseInfo info) {
                                vinCallBack = null;
                                if (info != null && info.isSuccess() && !StringUtil.isEmpty(info.getVin()) && info.getVin().length() == 17) {
                                    callBack.success(info);
                                } else {
                                    callBack.failure("识别失败,请手动输入");
                                }
                            }
                        });
            }
        };

    }

    private static DriverLicenseInfo requestAliApi(Context context, String imgPath) {
        String host = "https://dm-53.data.aliyun.com";
        String path = "/rest/160601/ocr/ocr_vehicle.json";
        String method = "POST";
        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("Authorization", "APPCODE 42436c662e3c43b5bd90395491b16bf0");
        String ali_appcode = SharedPreUtil.getString(context, "ali_appcode");
        ali_appcode = StringUtil.isEmpty(ali_appcode) ? "42436c662e3c43b5bd90395491b16bf0" : ali_appcode;
        headers.put("Authorization", "APPCODE " + ali_appcode);
        Map<String, String> querys = new HashMap<String, String>();
        Log.d("DriverLicenseHelp", "》》开始识别 行驶证信息 appcode:" + ali_appcode);
        String base64 = null;//要求照片小于1.5M
        try {
            base64 = ImageUtil.getBase64FromFile(imgPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtil.isEmpty(base64)) {
            return null;
        }
        String bodys = "{\"inputs\":[{\"image\":{\"dataType\":50,\"dataValue\":\"" + base64 + "\"}}]}";

        try {
            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
            Log.d("DriverLicenseHelp", "》》结束识别 行驶证信息");
            if (response != null) {
                Log.d("DriverLicenseHelp", "行驶证识别response：" + response.getStatusLine().getStatusCode());
                String json = null;
                try {
                    json = EntityUtils.toString(response.getEntity());
                    if (json == null) {
                        return null;
                    }
                    Log.d("DriverLicenseHelp", "行驶证识别信息：" + json);
                    JSONObject obj = new JSONObject(json);
                    JSONObject jsonObject = obj.getJSONArray("outputs").getJSONObject(0);
                    DriverLicenseInfo info = JSON.parseObject(jsonObject.getJSONObject("outputValue").getString("dataValue"), DriverLicenseInfo.class);
                    return info;
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("DriverLicenseHelp", "》》结束识别 行驶证信息  异常");
            Log.e("DriverLicenseHelp", "行驶证识别信息：" + e.getMessage(), e);
        }
        return null;
    }

    public interface IVinCallBack {
        void path(String photoPath);
    }

    public interface ICallBack {
        void success(DriverLicenseInfo info);

        void failure(String msg);

        void start();
    }
}
