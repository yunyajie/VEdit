package com.example.vedit.Activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.R;
import com.example.vedit.Widgets.VideoSeekBar;
import com.nostra13.universalimageloader.utils.L;

import java.io.File;

public class TrimActivity extends AppCompatActivity implements SurfaceHolder.Callback, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    //上下文
    private Context mContext;
    //日志TAG
    private final String TAG="TrimActivity";
    //视频剪辑View
    public static VideoSeekBar trim_video_seekbar;
    private SurfaceView ip_surfaceview;
    private ImageView ip_play_igview;
    private TextView ip_ctime_tv;
    private TextView ip_ttime_tv;
    private SeekBar ip_seekbar;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer=null;
    private Uri videopath=Uri.fromFile(new File(MyApplication.getWorkPath(),"L世欢-36.mp4"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trim);
        mContext=TrimActivity.this;
        initViews();
    }

    private void initViews() {
        trim_video_seekbar=(VideoSeekBar)findViewById(R.id.trim_video_seekbar);
        ip_surfaceview=(SurfaceView)findViewById(R.id.ip_surfaceview);
        ip_play_igview=(ImageView)findViewById(R.id.ip_play_igview);
        ip_ctime_tv=(TextView)findViewById(R.id.ip_ctime_tv);
        ip_ttime_tv=(TextView)findViewById(R.id.ip_ttime_tv);
        ip_seekbar=(SeekBar)findViewById(R.id.ip_seekbar);

        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);

        //初始化surfaceholder类，SurfaceView的控制器
        surfaceHolder=ip_surfaceview.getHolder();
        surfaceHolder.addCallback(this);

        //视频剪辑View
        trim_video_seekbar.reset();
        trim_video_seekbar.setProgressDraw(true);
        trim_video_seekbar.setProgressLine(true);
        trim_video_seekbar.setProgressBG(true);
        trim_video_seekbar.setCutMode(true,true);
        float videoFrame=60*1000f;
//        try {
            trim_video_seekbar.setVideoUri(true,videopath.getPath());
//        }catch (Exception e){
//            Log.i(TAG,"无法绘制预览图");
//            Log.i(TAG,e.toString());
//        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer=MediaPlayer.create(this,videopath);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDisplay(surfaceHolder);//设置显示视频显示在SurfaceView上
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.onDestroy();
        if (mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
        mediaPlayer.release();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ip_play_igview:
                Log.i(TAG,"点击了播放或暂停");
                if (mediaPlayer.isPlaying()){
                    mediaPlayer.pause();
                }else {
                    mediaPlayer.start();
                }
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.i(TAG,"SeekBar改变。。。");

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

        Log.i(TAG,"SeekBar拖动。。。");
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.i(TAG,"SeekBar停止拖动。。。");

    }
}
