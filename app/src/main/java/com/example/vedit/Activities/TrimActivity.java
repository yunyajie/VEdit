package com.example.vedit.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.OthUtils;
import com.example.vedit.Widgets.ExecProgressDialog;
import com.example.vedit.Widgets.VideoSeekBar;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;


public class TrimActivity extends Activity implements SurfaceHolder.Callback, View.OnClickListener, SeekBar.OnSeekBarChangeListener, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener {
    //上下文
    private Context mContext;
    //日志TAG
    private final String TAG = "TrimActivity";
    //视频剪辑View
    public static VideoSeekBar trim_video_seekbar;
    private SurfaceView ip_surfaceview;
    private FrameLayout ip_frame;

    private ImageView ip_play_igview;
    private TextView ip_ctime_tv;
    private TextView ip_ttime_tv;
    private SeekBar ip_seekbar;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer = null;

    private TextView trim_start;
    private TextView trim_end;

    //视频Uri
    private String videopath;
    //更新UI
    private Handler myHandler;


    private Timer timer;
    private TimerTask timerTask;

    private LinearLayout trim_linearLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        mContext = TrimActivity.this;
        initViews();
        //更新UI
        myHandler = new MyHandler(this);
    }

    /**
     * 静态内部类
     */
    private static class MyHandler extends Handler {
        private final WeakReference<TrimActivity> mTarget;

        private MyHandler(TrimActivity mTarget) {
            this.mTarget = new WeakReference<TrimActivity>(mTarget);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            TrimActivity trimActivity = mTarget.get();
            if (trimActivity != null && trimActivity.mediaPlayer != null) {
                int currentTime = trimActivity.mediaPlayer.getCurrentPosition();
                switch (msg.what) {
                    case FinalConstants.PROGRESS_CHANGED:
                        trimActivity.ip_seekbar.setProgress(currentTime);
                        trimActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        trim_video_seekbar.setProgressDraw(true);
                        trim_video_seekbar.setProgress(currentTime);
                        Log.i(trimActivity.TAG, "startTime==" + OthUtils.secToTimeRetain((int) trim_video_seekbar.getStartTime() / 1000) + "-------------endTime==" + OthUtils.secToTimeRetain((int) trim_video_seekbar.getEndTime() / 1000));
                        trimActivity.trim_start.setText(OthUtils.secToTimeRetain((int) trim_video_seekbar.getStartTime() / 1000));
                        trimActivity.trim_end.setText(OthUtils.secToTimeRetain((int) trim_video_seekbar.getEndTime() / 1000));
                        break;
                    case FinalConstants.SEEKBAR_CHANGED:
                        trimActivity.ip_seekbar.setProgress(currentTime);
                        Log.i("进度条改变", "currentTime" + currentTime + "====" + OthUtils.secToTimeRetain(currentTime / 1000));
                        trimActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));

                        Log.i(trimActivity.TAG, "startTime==" + OthUtils.secToTimeRetain((int) trim_video_seekbar.getStartTime() / 1000) + "-------------endTime==" + OthUtils.secToTimeRetain((int) trim_video_seekbar.getEndTime() / 1000));

                        break;
                }
            }
        }
    }


    private void initViews() {
        //初始化数据
        Intent intent = getIntent();
        //获取选择的视频
        videopath = intent.getStringExtra(FinalConstants.INTENT_SELECTONEVID_KEY);
        Log.e(TAG, videopath);
        trim_video_seekbar = (VideoSeekBar) findViewById(R.id.trim_video_seekbar);
        ip_surfaceview = (SurfaceView) findViewById(R.id.ip_surfaceview);
        ip_frame = (FrameLayout) findViewById(R.id.ip_frame);

        ip_play_igview = (ImageView) findViewById(R.id.ip_play_igview);
        ip_ctime_tv = (TextView) findViewById(R.id.ip_ctime_tv);
        ip_ttime_tv = (TextView) findViewById(R.id.ip_ttime_tv);
        ip_seekbar = (SeekBar) findViewById(R.id.ip_seekbar);
        trim_start = (TextView) findViewById(R.id.trim_start);
        trim_end = (TextView) findViewById(R.id.trim_end);
        trim_linearLayout=(LinearLayout)findViewById(R.id.trim_linearLayout);

        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);

        //初始化surfaceholder类，SurfaceView的控制器
        surfaceHolder = ip_surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        //视频预览
        trim_video_seekbar.reset();
        trim_video_seekbar.setVideoUri(true, videopath);

        //初始化对话框
        execProgressDialog = new ExecProgressDialog(this);
        execProgressDialog.DialogInit();


        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = FinalConstants.PROGRESS_CHANGED;
                //更新进度条
                myHandler.sendMessage(message);
            }
        };


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setDisplay(surfaceHolder);//设置显示视频显示在SurfaceView上
        playVideo(Uri.parse(videopath));
    }

    private void playVideo(Uri uri) {
        try {
            mediaPlayer.setDataSource(this, uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
            ip_seekbar.setMax(mediaPlayer.getDuration());
            //视频总时间
            ip_ttime_tv.setText(OthUtils.secToTimeRetain(mediaPlayer.getDuration() / 1000));

            //启动定时器
            timer.schedule(timerTask, 1000, 1000);

//            VideoThread videoThread = new VideoThread();
//            videoThread.start();

            changePlayerView(true);
        } catch (IOException e) {
            Log.i(TAG, "播放视频失败");
            Toast.makeText(this,"视频文件不存在",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.onDestroy();
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;

    }

    private void changePlayerView(boolean isPlaying) {
        ip_play_igview.setImageResource(isPlaying ? R.mipmap.ic_media_stop : R.mipmap.ic_media_play);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ip_play_igview:
                Log.i(TAG, "点击了播放或暂停");
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                    changePlayerView(false);
                } else {
                    mediaPlayer.start();
                    changePlayerView(true);
                }
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i(TAG, "SeekBar改变。。。");

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        Log.i(TAG, "SeekBar拖动。。。");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG, "SeekBar停止拖动。。。");
        int process = ip_seekbar.getProgress();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(process);
            Message message = new Message();
            message.what = FinalConstants.SEEKBAR_CHANGED;
            myHandler.sendMessage(message);
        }

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

        ViewGroup.LayoutParams lp=trim_linearLayout.getLayoutParams();
        lp.height=videoHeight;
        lp.width=videoWith;
        trim_linearLayout.setLayoutParams(lp);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.i(TAG, "视频播放完成");
        mediaPlayer.seekTo(0);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        Message message = new Message();
        message.what = FinalConstants.SEEKBAR_CHANGED;
        myHandler.sendMessage(message);

    }

    //视频进度条更新
    class VideoThread extends Thread {
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                Message message = new Message();
                message.what = FinalConstants.PROGRESS_CHANGED;
                //更新进度条
                myHandler.sendMessage(message);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            Log.i(TAG, "进程运行结束");

        }
    }

    @Override
    protected void onDestroy() {
        myHandler.removeMessages(FinalConstants.PROGRESS_CHANGED);
        timer.cancel();
        timerTask.cancel();
        timerTask = null;
        timer = null;
        if (mediaPlayer != null) {
            Log.i(TAG, "mediaPlayer--in--onDestroy--释放中");
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }

    private ExecProgressDialog execProgressDialog;

    //剪辑时长
    public void trimDuration(View view) {
        trim_video_seekbar.setCutMode(false);
        //获取开始时间和结束时间--单位毫秒
        float startTime = trim_video_seekbar.getStartTime() / 1000;
        float endTime = trim_video_seekbar.getEndTime() / 1000;
        Log.i(TAG, "视频时长剪辑开始：startTime=" + startTime + "----------endTime=" + endTime + "---------duration=" + (endTime - startTime));
        EpMediaUtils epMediaUtils=new EpMediaUtils(this);
        epMediaUtils.setInputVideo(videopath);
        epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
        epMediaUtils.clip(startTime,endTime-startTime);

    }


}
