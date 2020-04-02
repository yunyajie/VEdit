package com.example.vedit.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.example.vedit.Application.MyApplication;


public class SplashActivity extends BaseActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread myThread=new Thread(){
            @Override
            public void run() {
                try{
                    sleep(1000);//使程序休眠一秒
                    Intent intent=new Intent(getApplicationContext(),InitAdActivity.class);
                    startActivity(intent);
                    finish();//关闭当前活动
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        /**
         * Created by Yajie on 2020/3/19 20:59
         * 如果不是第一次进入直接到主页面，否则进入引导界面
         */
        if (MyApplication.getIsRunning()==0){
            Log.i("SplashActivity","第一次启动，进入引导页");
            MyApplication.setIsRunning(1);
            myThread.start();

        }else {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            Log.i("SplashActivity","不是第一次启动，直接进入主页面");
            finish();
        }
    }


}
