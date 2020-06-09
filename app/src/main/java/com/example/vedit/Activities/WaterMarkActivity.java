package com.example.vedit.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Bean.PicWatermark;
import com.example.vedit.Bean.TextWatermark;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.Interface.ViewErrorListener;
import com.example.vedit.Player.MediaManager;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.FileSelectUtils;
import com.example.vedit.Utils.FileUtils;
import com.example.vedit.Utils.OthUtils;
import com.example.vedit.Utils.TimerUtils;
import com.example.vedit.Widgets.FrameOverlayPic;
import com.example.vedit.Widgets.FrameOverlayText;
import com.zhihu.matisse.Matisse;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpText;

public class WaterMarkActivity extends NoTitleActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private String TAG="WaterMarkActivity";
    private SurfaceView ip_surfaceview;
    private FrameLayout watermark_frameLayout;
    private ImageView ip_play_igview;
    private TextView ip_ctime_tv;
    private TextView ip_ttime_tv;
    private SeekBar ip_seekbar;
    private FrameOverlayPic pic_FOV;
    private FrameOverlayText text_FOV;
    private Button bt_watermark_pic;
    private Button bt_watermark_text;
    private Button bt_watermark_ok;
    private Button bt_watermark_clear;
    private Button bt_fontset;
    private SurfaceHolder surfaceHolder;
    private MediaManager mediaManager;
    private MediaPlayer mediaPlayer;
    private Uri videoPath;

    private Boolean watermark_pic=false;
    private Boolean watermark_text=false;

    //图片水印开始和结束时间
    private float startTime=0;
    private float endTime=0;
    //文字水印开始和结束时间
    private float startTime_text=0;
    private float endTime_text=0;

    //更新UI
    private Handler myHandler;
    private TimerUtils timerUtils;

    //视频真实宽高
    private int Vwith;
    private int Vheight;

    List<Uri> mSelectedPic;
    List<PicWatermark>picWatermarks;
    List<TextWatermark>textWatermarks;
    private String textContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watermark);
        initViews();
    }

    private void initViews() {
        Intent intent=getIntent();
        videoPath=Uri.parse(intent.getStringExtra(FinalConstants.INTENT_SELECTONEVID_KEY));

        picWatermarks=new ArrayList<PicWatermark>();
        textWatermarks=new ArrayList<TextWatermark>();

        ip_surfaceview = (SurfaceView) findViewById(R.id.ip_surfaceview);
        watermark_frameLayout = (FrameLayout) findViewById(R.id.ip_frame);
        ip_play_igview = (ImageView) findViewById(R.id.ip_play_igview);
        ip_ctime_tv = (TextView) findViewById(R.id.ip_ctime_tv);
        ip_ttime_tv = (TextView) findViewById(R.id.ip_ttime_tv);
        ip_seekbar=(SeekBar)findViewById(R.id.ip_seekbar);
        pic_FOV=(FrameOverlayPic)findViewById(R.id.pic_FOL);
        text_FOV=(FrameOverlayText)findViewById(R.id.text_FOL);
        bt_watermark_ok=(Button)findViewById(R.id.bt_watermark_ok);
        bt_watermark_pic=(Button)findViewById(R.id.bt_watermark_pic);
        bt_watermark_text=(Button)findViewById(R.id.bt_watermark_text);
        bt_watermark_clear=(Button)findViewById(R.id.bt_watermark_clear);
        bt_fontset=(Button)findViewById(R.id.bt_setfont) ;


        text_FOV.setViewErrorListener(new ViewErrorListener() {
            @Override
            public void onError() {
                Toast.makeText(WaterMarkActivity.this,"字数过多或字体过大，请重新设置",Toast.LENGTH_SHORT).show();
            }
        });

        mediaManager=MediaManager.getInstance();


        //初始化surfaceholder类，SurfaceView的控制器
        surfaceHolder = ip_surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);

        bt_watermark_ok.setOnClickListener(this);
        bt_watermark_pic.setOnClickListener(this);
        bt_watermark_text.setOnClickListener(this);
        bt_watermark_clear.setOnClickListener(this);
        bt_fontset.setOnClickListener(this);

        myHandler=new WaterMarkActivity.MyHandler(this);
        timerUtils=new TimerUtils(myHandler);
        timerUtils.setNotifyWhat(FinalConstants.PROGRESS_CHANGED);
        timerUtils.setTime(1000,1000);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaManager.init(this,videoPath);
        mediaPlayer=mediaManager.getMediaPlayer();
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setDisplay(surfaceHolder);
        ip_seekbar.setMax(mediaPlayer.getDuration());

        //视频真实宽高
        Vwith=mediaPlayer.getVideoWidth();
        Vheight=mediaPlayer.getVideoHeight();


        //视频总时间
        ip_ttime_tv.setText(OthUtils.secToTimeRetain(mediaPlayer.getDuration() / 1000));

        //设置为退出时播放的位置
        mediaManager.seekTo(position);

        mediaManager.play();
        //启动定时器
        timerUtils.startTimer();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    private int position=0;
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        position=mediaPlayer.getCurrentPosition();
        mediaManager.destory();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        changeVideoSize();
    }
    private void changeVideoSize() {
        int videoWith = mediaPlayer.getVideoWidth();
        int videoHeight = mediaPlayer.getVideoHeight();
        //surfaceView的宽高
        int surfaceWith = ip_surfaceview.getWidth();
        int surfaceHeight = ip_surfaceview.getHeight();
        //根据视频尺寸计算视频可以在surfaceView中放大的最大倍数
        float max;
        //竖屏模式下按视频宽度计算放大倍数
        max = Math.max((float) videoWith / (float) surfaceWith, (float) videoHeight / (float) surfaceHeight);
        //视频宽高分别/最大倍数 计算出放大后的视频尺寸
        videoWith = (int) Math.ceil((float) videoWith / max);
        videoHeight = (int) Math.ceil((float) videoHeight / max);
        //无法直接设置视频尺寸，将计算出的视频尺寸设置到surfaceView让视频自动填充
        ip_surfaceview.setLayoutParams(new FrameLayout.LayoutParams(videoWith, videoHeight));

        watermark_frameLayout.getLayoutParams().width=videoWith;
        watermark_frameLayout.getLayoutParams().height=videoHeight;

        ViewGroup.LayoutParams layoutParams=pic_FOV.getLayoutParams();
        layoutParams.height=videoHeight;
        layoutParams.width=videoWith;
        pic_FOV.setLayoutParams(layoutParams);

        ViewGroup.LayoutParams layoutParams1=text_FOV.getLayoutParams();
        layoutParams1.width=videoWith;
        layoutParams1.height=videoHeight;
        text_FOV.setLayoutParams(layoutParams1);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaManager.pause();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ip_play_igview:
                if (mediaManager.isPlaying()){
                    mediaManager.pause();
                }else {
                    mediaManager.play();
                }
                break;
            case R.id.bt_watermark_pic:
                //图片水印
                if (watermark_pic){
                    mediaManager.pause();
                    endTime=mediaPlayer.getCurrentPosition();
                    if (startTime==endTime){
                        Toast.makeText(WaterMarkActivity.this,"取消",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(WaterMarkActivity.this,"图片水印信息已记录",Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"图片水印:startTime="+startTime+"**endTime="+endTime);
                        Rect frameRect=pic_FOV.getFrameRect();
                        Rect parentRect=pic_FOV.getParentInfo();
                       float picWidth=((float)frameRect.width()/parentRect.width())*Vwith;
                       float picHeight=((float)frameRect.height()/parentRect.height())*Vheight;
                       int x=(int)(((float)frameRect.left/parentRect.width())*Vwith);
                       int y=(int)(((float) frameRect.top/parentRect.height())*Vheight);
                        picWatermarks.add(new PicWatermark(x,y,picWidth,picHeight,
                                new FileUtils(this).getFilePathByUri(mSelectedPic.get(0)),(int)startTime/1000,(int)endTime/1000));
                    }
                    watermark_pic=false;
                    pic_FOV.setVisibility(View.INVISIBLE);
                }else {
                    new FileSelectUtils().selectOnePic(mSelectedPic,WaterMarkActivity.this,FinalConstants.REQUESTCODE_SELECTPIC_WATERMARK);
                }
                break;
            case R.id.bt_watermark_text:
                mediaManager.pause();
                //文字水印
                if(watermark_text){
                    endTime_text=mediaPlayer.getCurrentPosition();
                    if (startTime_text==endTime_text){
                        Toast.makeText(WaterMarkActivity.this,"取消",Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(WaterMarkActivity.this,"文字水印信息已记录",Toast.LENGTH_SHORT).show();
                        Log.e(TAG,"文字水印:startTime="+startTime_text+"**endTime="+endTime_text);
                        Rect frameRect=text_FOV.getFrameRect();
                        Rect parentRect=text_FOV.getParentInfo();
                        int x=(int)(((float)frameRect.left/parentRect.width())*Vwith);
                        int y=(int)(((float) frameRect.top/parentRect.height())*Vheight);
                        float fontSize=((float)frameRect.height()/parentRect.height())*Vheight;
                        Log.e(TAG,"fontsize=="+fontSize);
                        String content=text_FOV.getTextWatermark();
                        textWatermarks.add(new TextWatermark(x,y,fontColor,fontSize,content,(int)(startTime_text/1000),(int)(endTime_text/1000)));
                    }
                    watermark_text=false;
                    text_FOV.setVisibility(View.INVISIBLE);
                }else{
                    final EditText et=new EditText(WaterMarkActivity.this);
                    new AlertDialog.Builder(WaterMarkActivity.this)
                            .setCancelable(false)
                            .setTitle("请输入添加的文字")
                            .setView(et)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String input=et.getText().toString().trim();
                                    if (input.equals("")){
                                        Toast.makeText(WaterMarkActivity.this,"输入内容不能为空",Toast.LENGTH_SHORT).show();
                                    }else {
                                        textContent=input;
                                        watermark_text=true;
                                        text_FOV.setTextWatermark(textContent);
                                        text_FOV.setVisibility(View.VISIBLE);
                                        startTime_text=mediaPlayer.getCurrentPosition();
                                        mediaManager.play();
                                    }
                                }
                            })
                            .setNegativeButton("取消",null)
                            .show();
                }
                break;
            case R.id.bt_watermark_clear:
                //清除已添加的水印信息
                if (picWatermarks!=null) picWatermarks.clear();
                if (textWatermarks!=null) textWatermarks.clear();
                Toast.makeText(WaterMarkActivity.this,"清除成功",Toast.LENGTH_SHORT).show();
                break;
            case R.id.bt_watermark_ok:
                //处理
//                int curr=mediaPlayer.getCurrentPosition();
//                EpMediaUtils epMediaUtils=new EpMediaUtils(this);
//                epMediaUtils.test(new FileUtils(this).getFilePathByUri(videoPath),MyApplication.getPicPath()+OthUtils.createFileName("PNG","png"),curr/1000);
                addWatermark();
                break;
            case R.id.bt_setfont:
                setFontDialog();
                break;

        }
    }
    private void addWatermark(){
        if (watermark_pic||watermark_text){
            Toast.makeText(WaterMarkActivity.this,"水印添加还未完成",Toast.LENGTH_SHORT).show();
            return;
        }
        boolean key=false;
        EpMediaUtils epMediaUtils=new EpMediaUtils(this);
        epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(videoPath));
        String outputPath=MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4");
        epMediaUtils.setOutputPath(outputPath);
        if (picWatermarks!=null&&picWatermarks.size()!=0){
            key=true;
            epMediaUtils.addDraw(picWatermarks);
        }
        if (textWatermarks!=null&&textWatermarks.size()!=0){
            if (!key) key=true;
            epMediaUtils.addText(textWatermarks);
        }
        if (!key){
            Toast.makeText(WaterMarkActivity.this,"没有任何水印信息",Toast.LENGTH_SHORT).show();
            return;
        }
        epMediaUtils.multiOperate();
        //记录新生成的文件
        MyApplication.addNewFile(outputPath);
        SharedPreferences sharedPreferences=getSharedPreferences("newfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("filePath",outputPath);
        editor.commit();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            switch (requestCode){
                case FinalConstants.REQUESTCODE_SELECTPIC_WATERMARK:
                    //图片水印
                    mSelectedPic= Matisse.obtainResult(data);
                    ContentResolver resolver=getContentResolver();
                    Bitmap waterpic=null;
                    try {
                        waterpic= BitmapFactory.decodeStream(resolver.openInputStream(mSelectedPic.get(0)));
                    } catch (FileNotFoundException e) {
                        Log.e(TAG,"图片不可用");
                        Toast.makeText(WaterMarkActivity.this,"图片不可用",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    watermark_pic=true;
                    pic_FOV.setWaterPic(waterpic);
                    pic_FOV.setVisibility(View.VISIBLE);
                    Log.i(TAG,"取状态********position="+position);
                    mediaManager.seekTo(position);
                    startTime=position;
                    break;
            }
        }
    }

    /** 进度条点击事件  */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int process=ip_seekbar.getProgress();
        mediaManager.seekTo(process);
        myHandler.sendEmptyMessage(FinalConstants.SEEKBAR_CHANGED);
    }

    /**
     * 静态内部类
     */
    private static class MyHandler extends Handler {
        private final WeakReference<WaterMarkActivity> mTarget;

        private MyHandler(WaterMarkActivity mTarget) {
            this.mTarget = new WeakReference<WaterMarkActivity>(mTarget);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            WaterMarkActivity waterMarkActivity = mTarget.get();
            if (waterMarkActivity != null) {
                int currentTime = waterMarkActivity.mediaManager.getCurrentPosition();
                switch (msg.what) {
                    case FinalConstants.PROGRESS_CHANGED:
                        waterMarkActivity.ip_seekbar.setProgress(currentTime);
                        waterMarkActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                    case FinalConstants.SEEKBAR_CHANGED:
                        waterMarkActivity.ip_seekbar.setProgress(currentTime);
                        Log.i(waterMarkActivity.TAG, "currentTime" + currentTime + "====" + OthUtils.secToTimeRetain(currentTime / 1000));
                        waterMarkActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(TAG,"保存状态*****position="+position);
        outState.putInt("position",position);
    }

    private int fontSize=40;
    private EpText.Color fontColor=EpText.Color.White;
    private int colorKey=8;
    private void setFontDialog() {
        mediaManager.pause();
        final Dialog dialog=new Dialog(this,R.style.Theme_AppCompat_DayNight_Dialog);
        View view=View.inflate(this,R.layout.layout_setfont,null);
        dialog.setContentView(view);
        Window window=dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        RadioGroup rg_setfontsize=dialog.findViewById(R.id.rg_setfontsize);
        rg_setfontsize.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_fontsize_40:
                        fontSize=40;
                        break;
                    case R.id.rb_fontsize_50:
                        fontSize=50;
                        break;
                    case R.id.rb_fontsize_60:
                        fontSize=60;
                        break;
                    case R.id.rb_fontsize_70:
                        fontSize=70;
                        break;
                    case R.id.rb_fontsize_80:
                        fontSize=80;
                        break;
                }
            }
        });
        RadioGroup rg_setfontcolor=(RadioGroup)dialog.findViewById(R.id.rg_setfontcolor);
        rg_setfontcolor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_fontcolor_red:
                        colorKey=1;
                        fontColor=EpText.Color.Red;
                        break;
                    case R.id.rb_fontcolor_black:
                        colorKey=2;
                        fontColor=EpText.Color.Black;
                        break;
                    case R.id.rb_fontcolor_blue:
                        colorKey=3;
                        fontColor=EpText.Color.Blue;
                        break;
                    case R.id.rb_fontcolor_cyan:
                        colorKey=4;
                        fontColor=EpText.Color.Cyan;
                        break;
                    case R.id.rb_fontcolor_darkblue:
                        colorKey=5;
                        fontColor=EpText.Color.DarkBlue;
                        break;
                    case R.id.rb_fontcolor_green:
                        colorKey=6;
                        fontColor=EpText.Color.Green;
                        break;
                    case R.id.rb_fontcolor_orange:
                        colorKey=7;
                        fontColor=EpText.Color.Orange;
                        break;
                    case R.id.rb_fontcolor_white:
                        colorKey=8;
                        fontColor=EpText.Color.White;
                        break;
                    case R.id.rb_fontcolor_yellow:
                        colorKey=9;
                        fontColor=EpText.Color.Yellow;
                        break;
                    case R.id.rb_fontcolor_skyblue:
                        colorKey=10;
                        fontColor=EpText.Color.SkyBlue;
                        break;

                }
            }
        });
        Button bt_setfont_ok=(Button)dialog.findViewById(R.id.bt_setfont_ok);
        bt_setfont_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                text_FOV.setFontSize(fontSize);
                switch (colorKey){
                    case 1:
                        text_FOV.setFontColor(Color.RED);
                        break;
                    case 2:
                        text_FOV.setFontColor(Color.BLACK);
                        break;
                    case 3:
                        text_FOV.setFontColor(Color.BLUE);
                        break;
                    case 4:
                        text_FOV.setFontColor(Color.CYAN);
                        break;
                    case 5:
                        //深蓝
                        text_FOV.setFontColor(Color.rgb(0,0,139));
                        break;
                    case 6:
                        text_FOV.setFontColor(Color.GREEN);
                        break;
                    case 7:
                        text_FOV.setFontColor(Color.rgb(255,165,0));
                        break;
                    case 8:
                        text_FOV.setFontColor(Color.WHITE);
                        break;
                    case 9:
                        text_FOV.setFontColor(Color.YELLOW);
                        break;
                    case 10:
                        //天蓝
                        text_FOV.setFontColor(Color.rgb(135,206,235));
                        break;

                }

                Log.i(TAG,"设置字体大小为="+fontSize);
                Log.i(TAG,"字体颜色为："+colorKey);
            }
        });
        dialog.show();

    }


}
