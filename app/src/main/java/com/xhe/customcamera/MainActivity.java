package com.xhe.customcamera;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.car300.customcamera.data.PhotoInfo;
import com.car300.customcamera.utils.CameraUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<PhotoInfo> list = new ArrayList<>();
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.setTask_id(22222222);
                list.add(photoInfo);
                /*CameraUtil.getInstance().doOpenNormalCamera(MainActivity.this,list , 0, false, new CameraUtil.ActivityForResultCallback() {
                    @Override
                    public void result(Intent data) {

                    }
                });*/
            }
        });
    }
}
