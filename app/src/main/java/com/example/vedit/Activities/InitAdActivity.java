package com.example.vedit.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.vedit.R;

import java.util.Random;

/**
 * Created by Yajie on 2020/3/19 0:01
 */
public class InitAdActivity extends BaseActivity {
    private int[]picsLayout={R.layout.layout_hello, R.layout.layout_hello2,R.layout.layout_hello3,R.layout.layout_hello4
    ,R.layout.layout_hello5};
    private int i;
    private int count=5;
    private Button mBtnSkip;
    @SuppressLint("HandlerLeak")
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what==0){
                mBtnSkip.setText("跳过("+getCount()+")");
                handler.sendEmptyMessageDelayed(0,1000);
            }
        }
    };
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Random r=new Random();
        i=r.nextInt(10);
        Log.i("test","随机数是："+i);
        if (i<5){
            //随机概率会出现广告页,小于5出现，否则不出现
            setContentView(R.layout.activity_adv);
            initView();
        }else {
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }

    }

    private void initView() {
        linearLayout=findViewById(R.id.advLine);
        View view=View.inflate(InitAdActivity.this,picsLayout[i],null);
        linearLayout.addView(view);
        mBtnSkip=view.findViewById(R.id.btn_skip);
        mBtnSkip.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InitAdActivity.this,MainActivity.class));
                handler.removeMessages(0);
                finish();
            }
        });
        handler.sendEmptyMessageDelayed(0,1000);
    }

    private int getCount() {
        count--;
        if (count==0){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
            finish();
        }
        return count;
    }

}
