package com.example.vedit.Activities;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.Player.MediaManager;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.OthUtils;
import com.example.vedit.Utils.TimerUtils;
import com.example.vedit.Widgets.FrameOverlayView;

import java.lang.ref.WeakReference;

public class CropActivity extends NoTitleActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private final String TAG="CropActivity";
    private SurfaceView ip_surfaceview;
    private FrameLayout crop_frameLayout;
    private ImageView ip_play_igview;
    private TextView ip_ctime_tv;
    private TextView ip_ttime_tv;
    private SeekBar ip_seekbar;
    private FrameOverlayView crop_FOView;
    private SurfaceHolder surfaceHolder;
    private Button bt_cropInfo;
    private MediaManager mediaManager;
    private MediaPlayer mediaPlayer;
    private Uri videoPath;
    //更新UI
    private Handler myHandler;
    private TimerUtils timerUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        initViews();
    }

    private void initViews() {

        Intent intent=getIntent();
        videoPath=Uri.parse(intent.getStringExtra(FinalConstants.INTENT_SELECTONEVID_KEY));

        ip_surfaceview = (SurfaceView) findViewById(R.id.ip_surfaceview);
        crop_frameLayout = (FrameLayout) findViewById(R.id.ip_frame);
        ip_play_igview = (ImageView) findViewById(R.id.ip_play_igview);
        ip_ctime_tv = (TextView) findViewById(R.id.ip_ctime_tv);
        ip_ttime_tv = (TextView) findViewById(R.id.ip_ttime_tv);
        ip_seekbar=(SeekBar)findViewById(R.id.ip_seekbar);
        crop_FOView=(FrameOverlayView)findViewById(R.id.crop_FOView);
        bt_cropInfo=(Button)findViewById(R.id.bt_cropInfo);

        mediaManager=MediaManager.getInstance();


        //初始化surfaceholder类，SurfaceView的控制器
        surfaceHolder = ip_surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);
        bt_cropInfo.setOnClickListener(this);

        myHandler=new MyHandler(this);
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
        //视频总时间
        ip_ttime_tv.setText(OthUtils.secToTimeRetain(mediaPlayer.getDuration() / 1000));
        mediaManager.play();
        //启动定时器
        timerUtils.startTimer();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        mediaManager.destory();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) { changeVideoSize(); }

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

        crop_frameLayout.getLayoutParams().width=videoWith;
        crop_frameLayout.getLayoutParams().height=videoHeight;

        ViewGroup.LayoutParams layoutParams=crop_FOView.getLayoutParams();
        layoutParams.height=videoHeight;
        layoutParams.width=videoWith;
        crop_FOView.setLayoutParams(layoutParams);
    }
    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaManager.loop();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ip_play_igview:
                if (mediaManager.isPlaying()){
                    mediaManager.pause();
                }else {
                    mediaManager.play();
                }
                break;
            case R.id.bt_cropInfo:
                //获取裁剪信息并执行裁剪命令
                crop();
        }
    }

    //获取裁剪信息并裁减
    private void crop() {
        Rect frameRect=crop_FOView.getFrameRect();
        Log.e(TAG,"frameRect.width()="+frameRect.width()+":"+"frameRect.height()="+frameRect.height()+":"+frameRect.left+":"+frameRect.right+":"+frameRect.top+":"+frameRect.bottom);
        Rect parentRect=crop_FOView.getParentInfo();
        Log.e(TAG,"parentRect.width()="+parentRect.width()+":"+"parentRect.height()="+parentRect.height());
        //视频真实宽高
        int Vwith=mediaPlayer.getVideoWidth();
        int Vheight=mediaPlayer.getVideoHeight();
        float videoWidth=((float)frameRect.width()/parentRect.width())*Vwith;
        float videoHeight=((float)frameRect.height()/parentRect.height())*Vheight;
        float x=((float)frameRect.left/parentRect.width())*Vwith;
        float y=((float) frameRect.top/parentRect.height())*Vheight;
        Log.e(TAG,"视频尺寸裁剪-----原视频:宽="+Vwith+"-----高="+Vheight+"--------裁剪视频范围:宽="+videoWidth+"----高="+videoHeight+"---x="+x+"---y="+y);
        EpMediaUtils epMediaUtils=new EpMediaUtils(CropActivity.this);
        epMediaUtils.setInputVideo(videoPath.getPath());
        String outputPath=MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4");
        epMediaUtils.setOutputPath(outputPath);
        epMediaUtils.crop(videoWidth,videoHeight,x,y);
        MyApplication.addNewFile(outputPath);
        //记录新生成的文件
        MyApplication.addNewFile(outputPath);
        SharedPreferences sharedPreferences=getSharedPreferences("newfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("filePath",outputPath);
        editor.commit();
    }

    @Override
    protected void onDestroy() {
        mediaManager.destory();
        timerUtils.closeTimer();
        super.onDestroy();
    }

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
        private final WeakReference<CropActivity> mTarget;

        private MyHandler(CropActivity mTarget) {
            this.mTarget = new WeakReference<CropActivity>(mTarget);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            CropActivity cropActivity = mTarget.get();
            if (cropActivity != null) {
                int currentTime = cropActivity.mediaManager.getCurrentPosition();
                switch (msg.what) {
                    case FinalConstants.PROGRESS_CHANGED:
                        cropActivity.ip_seekbar.setProgress(currentTime);
                        cropActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                    case FinalConstants.SEEKBAR_CHANGED:
                        cropActivity.ip_seekbar.setProgress(currentTime);
                        Log.i(cropActivity.TAG, "currentTime" + currentTime + "====" + OthUtils.secToTimeRetain(currentTime / 1000));
                        cropActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                }
            }
        }
    }

}
