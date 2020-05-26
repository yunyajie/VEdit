package com.example.vedit.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.Interface.ExecInterface;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.FileSelectUtils;
import com.example.vedit.Utils.FileUtils;
import com.example.vedit.Utils.MediaUtils;
import com.example.vedit.Utils.OthUtils;
import com.example.vedit.Widgets.SampleExecDialog;
import com.zhihu.matisse.Matisse;

import java.io.File;
import java.util.List;

import VideoHandle.EpEditor;

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
                Log.i(TAG,"旋转和镜像");
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_ROTATION);
                break;
            case R.id.bt_actions8:
                //倍速
                Log.i(TAG,"倍速");
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_CHANGEPTS);
                break;
            case R.id.bt_actions9:
                //图片水印
            case R.id.bt_actions10:
                //文字水印
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_WATERMARK);
                break;
            case R.id.bt_actions11:
                //时间水印
                Log.i(TAG,"时间水印");
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_ADDTIME);
                break;
            case R.id.bt_actions12:
                //倒放
                Log.i(TAG,"倒放");
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_REVERSE);
                break;
                default:
                    break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            mSelectedVid=Matisse.obtainResult(data);
            EpMediaUtils epMediaUtils =new EpMediaUtils(this);
            File file=new File(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            if (!file.exists()){
                Log.e(TAG,"选择到失效文件");
                Toast.makeText(ActionsActivity.this,"该视频文件不存在",Toast.LENGTH_SHORT).show();
                return;
            }
            switch (requestCode){
                case FinalConstants.REQUESTCODE_SELECTVID_TRIM:
                    //时长剪辑
                    Log.d(TAG,"Matisse-mSelectedOneVid="+mSelectedVid);
                    //创建意图对象
                    Intent intent=new Intent(this,TrimActivity.class);
                    //传递键值对
                    intent.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                    startActivity(intent);
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_CROP:
                    //时长裁剪
                    Log.d(TAG,"Matisse-mSelectedOneVid="+mSelectedVid);
                    //创建意图对象
                    Intent intent1=new Intent(this,CropActivity.class);
                    //传递键值对
                    intent1.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                    startActivity(intent1);
                    break;
                case FinalConstants.REQUESTCODE_SELECTMOREVID_MERGE:
                    //视频合并
                    epMediaUtils.setOutputPath(MyApplication.getWorkPath()+ OthUtils.createFileName("VIDEO","mp4"));
                    epMediaUtils.mergeVideos(mSelectedVid);
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_GETBGM:
                    //获取BGM
                    epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                    epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("AUDIO","mp3"));
                    epMediaUtils.demuxerBGM();
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_ADDBGM:
                    //添加BGM
                    Intent intent2=new Intent(this,AddBGMActivity.class);
                    intent2.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                    startActivity(intent2);
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_ROTATION:
                    //旋转和镜像
                    Intent intent3=new Intent(this,ReverAndMirrActivity.class);
                    intent3.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                    startActivity(intent3);
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_REVERSE:
                    //倒放
                    epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                    epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
                    epMediaUtils.reverse(true,false);
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_ADDTIME:
                    //时间水印
                    showAddTimeDialog();
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_CHANGEPTS:
                    //倍速
                    showChangePTSDialog();
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_V2P:
                    //视频转图片
                    showV2PDialog();
                    break;
                case FinalConstants.REQUESTCODE_SELECTVID_WATERMARK:
                    //图片或文字水印
                    Intent intent4=new Intent(this,WaterMarkActivity.class);
                    intent4.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                    startActivity(intent4);
                    break;
            }
        }
      }
      //视频转图片
    final String[]rate=new String[]{"1","2","3","4","5","6"};
    private int selectRate=1;
    private void showV2PDialog() {
        SampleExecDialog sampleExecDialog=new SampleExecDialog();
        sampleExecDialog.diaogInit(ActionsActivity.this, "请选择每秒生成的图片数", rate, new ExecInterface() {
            @Override
            public void exec() {
                video2photo();
            }

            @Override
            public void setSelectEle(int which) {
                selectRate=which+1;
                Log.i(TAG,"每秒生成图片"+selectRate+"张");
            }
        });
        sampleExecDialog.dialogShow();
    }

    //视频生成图片
    private void video2photo() {
        String inputVideo=new FileUtils(this).getFilePathByUri(mSelectedVid.get(0));
        EpMediaUtils epMediaUtils=new EpMediaUtils(ActionsActivity.this);
        epMediaUtils.setInputVideo(inputVideo);
        epMediaUtils.setOutputPath(MyApplication.getPicPath()+"img_"+OthUtils.createFileName("","")+"_%03d.jpg");
        MediaUtils mediaUtils=MediaUtils.getInstance();
        mediaUtils.setSource(inputVideo);
        epMediaUtils.video2pic(Integer.parseInt(mediaUtils.getWidth()),Integer.parseInt(mediaUtils.getHeight()),selectRate);
    }

    //变速类型
     final String[]pts=new String[]{"0.25","0.5","1.5","2","3","4"};
    private int selectPTS=0;

    public void showChangePTSDialog(){
        final SampleExecDialog sampleExecDialog=new SampleExecDialog();
        sampleExecDialog.diaogInit(ActionsActivity.this,"选择倍率", pts, new ExecInterface() {
            @Override
            public void exec() {
                changePTSExec();
            }

            @Override
            public void setSelectEle(int which) {
                selectPTS=which;
                Log.i(TAG,"selectPTS=="+selectPTS);
            }
        });
        sampleExecDialog.dialogShow();
    }

    //时间水印类型
    final String[]type=new String[]{"hh:mm:ss","yyyy-MM-dd hh:mm:ss","yyyy年MM月dd日 hh时mm分ss秒"};
    private int selectType=1;
    private void showAddTimeDialog() {
        SampleExecDialog sampleExecDialog=new SampleExecDialog();
        sampleExecDialog.diaogInit(ActionsActivity.this, "请选择类型", type, new ExecInterface() {
            @Override
            public void exec() {
                addTimeExec();
            }

            @Override
            public void setSelectEle(int which) {
                selectType=which+1;
            }
        });
        sampleExecDialog.dialogShow();
    }

    //变速
    private void changePTSExec() {
        EpMediaUtils epMediaUtils=new EpMediaUtils(this);
        epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
        epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
        switch (selectPTS){
            case 0:
                Log.i(TAG,"变速0.25");
                epMediaUtils.changePTS((float)0.25, EpEditor.PTS.ALL);
                break;
            case 1:
                Log.i(TAG,"变速0.5");
                epMediaUtils.changePTS((float) 0.5, EpEditor.PTS.ALL);
                break;
            case 2:
                Log.i(TAG,"变速1.5");
                epMediaUtils.changePTS((float)1.5, EpEditor.PTS.ALL);
                break;
            case 3:
                Log.i(TAG,"变速2");
                epMediaUtils.changePTS(2, EpEditor.PTS.ALL);
                break;
            case 4:
                Log.i(TAG,"变速3");
                epMediaUtils.changePTS(3, EpEditor.PTS.ALL);
                break;
            case 5:
                Log.i(TAG,"变速4");
                epMediaUtils.changePTS(4, EpEditor.PTS.ALL);
                break;
        }
    }


    //添加时间水印
    private void addTimeExec() {
          EpMediaUtils epMediaUtils=new EpMediaUtils(this);
          epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
          epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
          int vHeight,vWidth;
        MediaUtils mediaUtils=MediaUtils.getInstance();
        try {
            mediaUtils.setSource(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
        }catch (Exception e){
            Toast.makeText(ActionsActivity.this,"该视频文件失效或不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        vHeight=Integer.parseInt(mediaUtils.getHeight());
        vWidth=Integer.parseInt(mediaUtils.getWidth());
        float size=vWidth/30;
        switch (selectType){
            case 1:
                //8
                Log.i(TAG,"类型1");
                epMediaUtils.addTime(0,0,size,"white",1);
                break;
            case 2:
                //19
                Log.i(TAG,"类型2");
                epMediaUtils.addTime(0,0,size,"white",2);
                break;
            case 3:
                //11
                Log.i(TAG,"类型3");
                epMediaUtils.addTime(0,0,size,"white",3);
                break;
        }

    }
}
