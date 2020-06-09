package com.example.vedit.Activities;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.Player.MediaManager;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.FileSelectUtils;
import com.example.vedit.Utils.FileUtils;
import com.example.vedit.Utils.OthUtils;
import com.example.vedit.Utils.TimerUtils;
import com.zhihu.matisse.Matisse;

import java.lang.ref.WeakReference;
import java.util.List;

public class AddBGMActivity extends NoTitleActivity implements SurfaceHolder.Callback, MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private String TAG="AddBGMActivity";
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
    private Uri audioPath;

    private Button bt_adjust;
    private Button bt_addbgm;
    private Button bt_selectmp3;
    private Button bt_selectmp4;

    private int old_volume=0;
    private int new_volume=100;

    private boolean fromVideo=false;
    private boolean isReady=false;
    //更新UI
    private Handler myHandler;
    private TimerUtils timerUtils;


    List<Uri> mSelectedVid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbgm);
        initViews();
    }

    private void initViews() {
        Intent intent=getIntent();
        videoPath=Uri.parse(intent.getStringExtra(FinalConstants.INTENT_SELECTONEVID_KEY));

        ip_surfaceview = (SurfaceView) findViewById(R.id.ip_surfaceview);
        addbgm_frameLayout = (FrameLayout) findViewById(R.id.ip_frame);
        ip_play_igview = (ImageView) findViewById(R.id.ip_play_igview);
        ip_ctime_tv = (TextView) findViewById(R.id.ip_ctime_tv);
        ip_ttime_tv = (TextView) findViewById(R.id.ip_ttime_tv);
        ip_seekbar=(SeekBar)findViewById(R.id.ip_seekbar);

        bt_addbgm=(Button)findViewById(R.id.bt_addbgm);
        bt_adjust=(Button)findViewById(R.id.bt_adjust);
        bt_selectmp3=(Button)findViewById(R.id.bt_selectmp3);
        bt_selectmp4=(Button)findViewById(R.id.bt_selectmp4);

        mediaManager=MediaManager.getInstance();

        bt_adjust.setOnClickListener(this);
        bt_addbgm.setOnClickListener(this);
        bt_selectmp4.setOnClickListener(this);
        bt_selectmp3.setOnClickListener(this);


        //初始化surfaceholder类，SurfaceView的控制器
        surfaceHolder = ip_surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);

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
    public void onCompletion(MediaPlayer mp) {
        mediaManager.loop();
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ip_play_igview:
                if (mediaManager.isPlaying()){
                    mediaManager.pause();
                }else {
                    mediaManager.play();
                }
                break;
            case R.id.bt_adjust:
                showBottomDialog();
                break;
            case R.id.bt_selectmp3:
                //选择MP3
                Intent intent=new Intent();
                intent.setType("audio/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,FinalConstants.REQUESTCODE_SELECTAUD_ADDBGM);
                break;
            case R.id.bt_selectmp4:
                //选择MP4
                //获取背景音乐
                new FileSelectUtils().selectOneVid(mSelectedVid,AddBGMActivity.this,FinalConstants.REQUESTCODE_SELECTVID_GETBGM);
                break;
            case R.id.bt_addbgm:
                //完成
                if (isReady){
                    addBGM();
                }else {
                    Toast.makeText(AddBGMActivity.this,"请选择音频来源",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    //添加背景音乐
    private void addBGM() {
        EpMediaUtils epMediaUtils1=new EpMediaUtils(AddBGMActivity.this);
        epMediaUtils1.setInputVideo(new FileUtils(AddBGMActivity.this).getFilePathByUri(videoPath));
        if (fromVideo){
            epMediaUtils1.setInputAudio(MyApplication.getSavePath()+"temp.mp3");

        }else {
            epMediaUtils1.setInputAudio(new FileUtils(AddBGMActivity.this).getFilePathByUri(audioPath));
        }
        String outputPath=MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4");
        epMediaUtils1.setOutputPath(outputPath);
        epMediaUtils1.music((float)old_volume/100,(float)new_volume/100);
        //记录新生成的文件
        MyApplication.addNewFile(outputPath);
        SharedPreferences sharedPreferences=getSharedPreferences("newfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("filePath",outputPath);
        editor.commit();

    }

    private void showBottomDialog() {
        final Dialog dialog=new Dialog(this,R.style.Theme_AppCompat_DayNight_Dialog);
        View view=View.inflate(this,R.layout.layout_adjustvolume,null);
        dialog.setContentView(view);
        Window window=dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        final SeekBar sb_old=dialog.findViewById(R.id.sb_old);
        final SeekBar sb_new=dialog.findViewById(R.id.sb_new);
        sb_old.setMax(100);
        sb_new.setMax(100);
        sb_old.setProgress(old_volume);
        sb_new.setProgress(new_volume);
        sb_new.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                new_volume=sb_new.getProgress();
            }
        });
        sb_old.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                old_volume=sb_old.getProgress();
            }
        });
        Button bt_adjust_volume_ok=dialog.findViewById(R.id.bt_adjust_volume_ok);
        bt_adjust_volume_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    /**
     * Created by Yajie on 2020/5/23 20:03
     * 进度条
     */
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
            if(requestCode==FinalConstants.REQUESTCODE_SELECTVID_GETBGM){
                fromVideo=true;
                isReady=true;
                //获取BGM
                mSelectedVid= Matisse.obtainResult(data);
                final EpMediaUtils epMediaUtils=new EpMediaUtils(this);
                epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
                epMediaUtils.setOutputPath(MyApplication.getSavePath()+"temp.mp3");
                epMediaUtils.demuxerBGM();
            }
            if (requestCode==FinalConstants.REQUESTCODE_SELECTAUD_ADDBGM){
                fromVideo=false;
                isReady=true;
                audioPath=data.getData();
                Log.i(TAG,audioPath.toString());
            }
        }
    }

    /**
     * 静态内部类
     */
    private static class MyHandler extends Handler {
        private final WeakReference<AddBGMActivity> mTarget;

        private MyHandler(AddBGMActivity mTarget) {
            this.mTarget = new WeakReference<AddBGMActivity>(mTarget);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            AddBGMActivity addBGMActivity = mTarget.get();
            if (addBGMActivity != null) {
                int currentTime = addBGMActivity.mediaManager.getCurrentPosition();
                switch (msg.what) {
                    case FinalConstants.PROGRESS_CHANGED:
                        addBGMActivity.ip_seekbar.setProgress(currentTime);
                        addBGMActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                    case FinalConstants.SEEKBAR_CHANGED:
                        addBGMActivity.ip_seekbar.setProgress(currentTime);
                        Log.i(addBGMActivity.TAG, "currentTime" + currentTime + "====" + OthUtils.secToTimeRetain(currentTime / 1000));
                        addBGMActivity.ip_ctime_tv.setText(OthUtils.secToTimeRetain(currentTime / 1000));
                        break;
                }
            }
        }
    }

}
