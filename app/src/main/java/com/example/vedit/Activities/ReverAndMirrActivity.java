package com.example.vedit.Activities;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.Player.MediaManager;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.FileUtils;
import com.example.vedit.Utils.OthUtils;
import com.example.vedit.Utils.TimerUtils;

import java.lang.ref.WeakReference;

public class ReverAndMirrActivity extends NoTitleActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private String TAG="ReverAndMirrActivity";
    private SurfaceView ip_surfaceview;
    private FrameLayout addbgm_frameLayout;
    private ImageView ip_play_igview;
    private TextView ip_ctime_tv;
    private TextView ip_ttime_tv;
    private SeekBar ip_seekbar;
    private SurfaceHolder surfaceHolder;
    private MediaManager mediaManager;
    private MediaPlayer mediaPlayer;
    private Uri videoPath;


    //更新UI
    private Handler myHandler;
    private TimerUtils timerUtils;


    private Button bt_re_mi_ok;
    private RadioGroup rg_reverse;
    private RadioGroup rg_mirror;

    private int reverse_angle=0;
    private boolean isMirror=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rever_and_mirr);
        initViews();
    }

    private void initViews() {
        Intent intent=getIntent();
        videoPath= Uri.parse(intent.getStringExtra(FinalConstants.INTENT_SELECTONEVID_KEY));

        ip_surfaceview = (SurfaceView) findViewById(R.id.ip_surfaceview);
        addbgm_frameLayout = (FrameLayout) findViewById(R.id.ip_frame);
        ip_play_igview = (ImageView) findViewById(R.id.ip_play_igview);
        ip_ctime_tv = (TextView) findViewById(R.id.ip_ctime_tv);
        ip_ttime_tv = (TextView) findViewById(R.id.ip_ttime_tv);
        ip_seekbar=(SeekBar)findViewById(R.id.ip_seekbar);

        rg_reverse=(RadioGroup)findViewById(R.id.rg_reverse);
        rg_mirror=(RadioGroup)findViewById(R.id.rg_mirror);

        bt_re_mi_ok=(Button)findViewById(R.id.bt_re_mi_ok);

        mediaManager=MediaManager.getInstance();

        bt_re_mi_ok.setOnClickListener(this);

        rg_mirror.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_mirror_no:
                        isMirror=false;
                        break;
                    case R.id.rb_mirror_yes:
                        isMirror=true;
                        break;
                }
                Toast.makeText(ReverAndMirrActivity.this,isMirror?"选择镜像":"取消镜像",Toast.LENGTH_SHORT).show();
            }
        });

        rg_reverse.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_reverse_0:
                        reverse_angle=0;
                        break;
                    case R.id.rb_reverse_90:
                        reverse_angle=90;
                        break;
                    case R.id.rb_reverse_180:
                        reverse_angle=180;
                        break;
                    case R.id.rb_reverse_270:
                        reverse_angle=270;
                        break;
                }
                Toast.makeText(ReverAndMirrActivity.this,"视频旋转"+reverse_angle+"度",Toast.LENGTH_SHORT).show();
            }
        });


        //初始化surfaceholder类，SurfaceView的控制器
        surfaceHolder = ip_surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);

        myHandler=new ReverAndMirrActivity.MyHandler(this);
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

        addbgm_frameLayout.getLayoutParams().width=videoWith;
        addbgm_frameLayout.getLayoutParams().height=videoHeight;
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
            case R.id.bt_re_mi_ok:
                if (!isMirror&&reverse_angle==0){
                    Toast.makeText(ReverAndMirrActivity.this,"请选择旋转或镜像操作",Toast.LENGTH_SHORT).show();
                }else {
                    exec();
                }
                break;
        }
    }

    private void exec() {
        //处理
        EpMediaUtils epMediaUtils=new EpMediaUtils(this);
        epMediaUtils.setInputVideo(new FileUtils(ReverAndMirrActivity.this).getFilePathByUri(videoPath));
        epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
        epMediaUtils.rotation(reverse_angle,isMirror);
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
        private final WeakReference<ReverAndMirrActivity> mTarget;

        private MyHandler(ReverAndMirrActivity mTarget) {
            this.mTarget = new WeakReference<ReverAndMirrActivity>(mTarget);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            ReverAndMirrActivity reverAndMirrActivity = mTarget.get();
            if (reverAndMirrActivity != null) {
                int currentTime = reverAndMirrActivity.mediaManager.getCurrentPosition();
                switch (msg.what) {
                    case FinalConstants.PROGRESS_CHANGED:
                        reverAndMirrActivity.ip_seekbar.setProgress(currentTime);
                        reverAndMirrActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                    case FinalConstants.SEEKBAR_CHANGED:
                        reverAndMirrActivity.ip_seekbar.setProgress(currentTime);
                        Log.i(reverAndMirrActivity.TAG, "currentTime" + currentTime + "====" + OthUtils.secToTimeRetain(currentTime / 1000));
                        reverAndMirrActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                }
            }
        }
    }
}
