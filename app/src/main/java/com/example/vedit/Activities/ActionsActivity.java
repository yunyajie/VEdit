package com.example.vedit.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.FileSelectUtils;
import com.example.vedit.Utils.FileUtils;
import com.example.vedit.Utils.OthUtils;
import com.zhihu.matisse.Matisse;

import java.util.List;

public class ActionsActivity extends NoTitleActivity implements View.OnClickListener {

    private Button bt_actions1;
    private Button bt_actions2;
    private Button bt_actions3;
    private Button bt_actions4;
    private Button bt_actions5;
    private Button bt_actions6;
    private Button bt_actions7;
    private Button bt_actions8;
    private Button bt_actions9;
    private Button bt_actions10;
    private Button bt_actions11;
    private Button bt_actions12;
    List<Uri> mSelectedVid;
    private static String TAG="ActionsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        initViews();
    }
    //初始化控件
    private void initViews() {
        bt_actions1=(Button)findViewById(R.id.bt_actions1);
        bt_actions2=(Button)findViewById(R.id.bt_actions2);
        bt_actions3=(Button)findViewById(R.id.bt_actions3);
        bt_actions4=(Button)findViewById(R.id.bt_actions4);
        bt_actions5=(Button)findViewById(R.id.bt_actions5);
        bt_actions6=(Button)findViewById(R.id.bt_actions6);
        bt_actions7=(Button)findViewById(R.id.bt_actions7);
        bt_actions8=(Button)findViewById(R.id.bt_actions8);
        bt_actions9=(Button)findViewById(R.id.bt_actions9);
        bt_actions10=(Button)findViewById(R.id.bt_actions10);
        bt_actions11=(Button)findViewById(R.id.bt_actions11);
        bt_actions12=(Button)findViewById(R.id.bt_actions12);


        bt_actions1.setOnClickListener(this);
        bt_actions2.setOnClickListener(this);
        bt_actions3.setOnClickListener(this);
        bt_actions4.setOnClickListener(this);
        bt_actions5.setOnClickListener(this);
        bt_actions6.setOnClickListener(this);
        bt_actions7.setOnClickListener(this);
        bt_actions8.setOnClickListener(this);
        bt_actions9.setOnClickListener(this);
        bt_actions10.setOnClickListener(this);
        bt_actions11.setOnClickListener(this);
        bt_actions12.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_actions1:
                //时长剪辑
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this, FinalConstants.REQUESTCODE_SELECTVID_TRIM);
                break;
            case R.id.bt_actions2:
                //尺寸裁剪
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_CROP);
                break;
            case R.id.bt_actions3:
                //获取背景音乐
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_GETBGM);
                break;
            case R.id.bt_actions4:
                //合并视频
                new FileSelectUtils().selectMoreVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTMOREVID_MERGE);
                break;
            case R.id.bt_actions5:
                //添加BGM
                Log.i(TAG,"添加BGM");
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_ADDBGM);
                break;
            case R.id.bt_actions6:
                //视频转图片
                Log.i(TAG,"视频转图片");
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_V2P);
                break;
            case R.id.bt_actions7:
                //旋转和镜像
                break;
            case R.id.bt_actions8:
                //倍速
                break;
            case R.id.bt_actions9:
                //图片水印
                break;
            case R.id.bt_actions10:
                //文字水印
                break;
            case R.id.bt_actions11:
                //时间水印
                break;
            case R.id.bt_actions12:
                //倒放
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_REVERSE);
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_TRIM&&resultCode==RESULT_OK){
            //时长剪辑
            mSelectedVid= Matisse.obtainResult(data);
            Log.d(TAG,"Matisse-mSelectedOneVid="+mSelectedVid);
            //创建意图对象
            Intent intent=new Intent(this,TrimActivity.class);
            //传递键值对
            intent.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            startActivity(intent);
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_CROP&&resultCode==RESULT_OK){
            //时长裁剪
            mSelectedVid= Matisse.obtainResult(data);
            Log.d(TAG,"Matisse-mSelectedOneVid="+mSelectedVid);
            //创建意图对象
            Intent intent=new Intent(this,CropActivity.class);
            //传递键值对
            intent.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            startActivity(intent);
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTMOREVID_MERGE&&resultCode==RESULT_OK){
            //视频合并
            mSelectedVid=Matisse.obtainResult(data);
            EpMediaUtils epMediaUtils =new EpMediaUtils(this);
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+ OthUtils.createFileName("VIDEO","mp4"));
            epMediaUtils.mergeVideos(mSelectedVid);
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_GETBGM&&resultCode==RESULT_OK){
            //获取BGM
            mSelectedVid=Matisse.obtainResult(data);
            EpMediaUtils epMediaUtils=new EpMediaUtils(this);
            epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("AUDIO","mp3"));
            epMediaUtils.demuxerBGM();
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_ADDBGM&&resultCode==RESULT_OK){
            //添加BGM
            mSelectedVid=Matisse.obtainResult(data);
            Intent intent=new Intent();
            intent.setType("audio/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,FinalConstants.REQUESTCODE_SELECTAUD_ADDBGM);



            EpMediaUtils epMediaUtils=new EpMediaUtils(this);
            epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            //epMediaUtils.setInputAudio();
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
            epMediaUtils.music(0,1);
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_V2P&&resultCode==RESULT_OK){
            //视频转图片
            mSelectedVid=Matisse.obtainResult(data);
            EpMediaUtils epMediaUtils=new EpMediaUtils(this);
            epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("PIC","jpg"));
            epMediaUtils.video2pic(1048,720,30);
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_REVERSE&&resultCode==RESULT_OK){
            //倒放
            mSelectedVid=Matisse.obtainResult(data);
            EpMediaUtils epMediaUtils=new EpMediaUtils(this);
            epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
            epMediaUtils.reverse(true,false);
        }
    }
}
