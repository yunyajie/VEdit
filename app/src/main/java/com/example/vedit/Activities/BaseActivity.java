package com.example.vedit.Activities;


import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;


/**
 * Created by Yajie on 2020/3/18 23:55
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucent(this);
    }

    public static void setTranslucent(Activity activity) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
            //设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }
}
