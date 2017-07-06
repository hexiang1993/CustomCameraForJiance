package com.xhe.customcamera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.car300.customcamera.CustomCamera;
import com.car300.customcamera.data.PhotoInfo;
import com.car300.customcamera.utils.CameraUtil;
import com.car300.customcamera.vindriver.DriverLicenseHelp;
import com.car300.customcamera.vindriver.DriverLicenseInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<PhotoInfo> list = new ArrayList<>();
                        PhotoInfo photoInfo = new PhotoInfo();
                        photoInfo.setTask_id(22222222);
                        list.add(photoInfo);
                        CustomCamera.doTakeNormalCamera(MainActivity.this, list, 0, false, new CustomCamera.ActivityForResultCallback() {
                            @Override
                            public void result(Intent data) {
                                if (data == null) {
                                    return;
                                }
                                List<PhotoInfo> list = CustomCamera.getNormalResult(data);
                                Log.d("MainActivityCamera", list != null ? list.toString() : "list=null");
                            }
                        });
                    }
                });

        findViewById(R.id.tv_vin_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomCamera.doTakeVinCamera(MainActivity.this, new DriverLicenseHelp.ICallBack() {
                            @Override
                            public void success(DriverLicenseInfo info) {
                                Log.d("MainActivityCamera", "成功：" + (info != null ? info.toString() : "info=null"));
                            }

                            @Override
                            public void failure(String msg) {
                                Log.d("MainActivityCamera", "失败" + msg);
                            }

                            @Override
                            public void start() {
                                Log.d("MainActivityCamera", "开始");
                            }
                        });
                    }
                });

        findViewById(R.id.tv_accident_camera)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomCamera.doTakeAccidentCamera(MainActivity.this, "", new CustomCamera.ActivityForResultCallback() {
                            @Override
                            public void result(Intent data) {
                                String resultPath = CustomCamera.getResultPath(data);
                                Log.d("MainActivityCamera", "照片路径：" + resultPath);
                            }
                        });
                    }
                });
    }
}
