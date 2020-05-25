package com.example.vedit.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.vedit.Widgets.FrameOverlayPic;
import com.zhihu.matisse.Matisse;

import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;
import java.util.List;

public class WaterMarkActivity extends NoTitleActivity implements SurfaceHolder.Callback, MediaPlayer.OnVideoSizeChangedListener, MediaPlayer.OnCompletionListener, View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private String TAG="WaterMarkActivity";
    private SurfaceView ip_surfaceview;
    private FrameLayout watermark_frameLayout;
    private ImageView ip_play_igview;
    private TextView ip_ctime_tv;
    private TextView ip_ttime_tv;
    private SeekBar ip_seekbar;
    private FrameOverlayPic pic_FOV;
    private Button bt_watermark_pic;
    private Button bt_watermark_text;
    private Button bt_watermark_ok;
    private SurfaceHolder surfaceHolder;
    private MediaManager mediaManager;
    private MediaPlayer mediaPlayer;
    private Uri videoPath;

    private Boolean watermark_pic=false;
    private Boolean watermark_text=false;


    //更新UI
    private Handler myHandler;
    private TimerUtils timerUtils;

    List<Uri> mSelectedPic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watermark);
        initViews();
    }

    private void initViews() {
        Intent intent=getIntent();
        videoPath=Uri.parse(intent.getStringExtra(FinalConstants.INTENT_SELECTONEVID_KEY));

        ip_surfaceview = (SurfaceView) findViewById(R.id.ip_surfaceview);
        watermark_frameLayout = (FrameLayout) findViewById(R.id.ip_frame);
        ip_play_igview = (ImageView) findViewById(R.id.ip_play_igview);
        ip_ctime_tv = (TextView) findViewById(R.id.ip_ctime_tv);
        ip_ttime_tv = (TextView) findViewById(R.id.ip_ttime_tv);
        ip_seekbar=(SeekBar)findViewById(R.id.ip_seekbar);
        pic_FOV=(FrameOverlayPic)findViewById(R.id.pic_FOL);
        bt_watermark_ok=(Button)findViewById(R.id.bt_watermark_ok);
        bt_watermark_pic=(Button)findViewById(R.id.bt_watermark_pic);
        bt_watermark_text=(Button)findViewById(R.id.bt_watermark_text);

        mediaManager=MediaManager.getInstance();


        //初始化surfaceholder类，SurfaceView的控制器
        surfaceHolder = ip_surfaceview.getHolder();
        surfaceHolder.addCallback(this);
        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);

        bt_watermark_ok.setOnClickListener(this);
        bt_watermark_pic.setOnClickListener(this);
        bt_watermark_text.setOnClickListener(this);

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

        watermark_frameLayout.getLayoutParams().width=videoWith;
        watermark_frameLayout.getLayoutParams().height=videoHeight;

        ViewGroup.LayoutParams layoutParams=pic_FOV.getLayoutParams();
        layoutParams.height=videoHeight;
        layoutParams.width=videoWith;
        pic_FOV.setLayoutParams(layoutParams);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mediaManager.loop();
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
                    watermark_pic=false;
                    pic_FOV.setVisibility(View.INVISIBLE);
                }else {
                    new FileSelectUtils().selectOnePic(mSelectedPic,WaterMarkActivity.this,FinalConstants.REQUESTCODE_SELECTPIC_WATERMARK);
                }
                break;
            case R.id.bt_watermark_ok:
                //处理
                Rect frameRect=pic_FOV.getFrameRect();
                Rect parentRect=pic_FOV.getParentInfo();
                //视频真实宽高
                int Vwith=mediaPlayer.getVideoWidth();
                int Vheight=mediaPlayer.getVideoHeight();
                float picWidth=((float)frameRect.width()/parentRect.width())*Vwith;
                float picHeight=((float)frameRect.height()/parentRect.height())*Vheight;
                int x=(int)(((float)frameRect.left/parentRect.width())*Vwith);
                int y=(int)(((float) frameRect.top/parentRect.height())*Vheight);
                Log.e(TAG,"视频尺寸裁剪-----原视频:宽="+Vwith+"-----高="+Vheight+"--------图片水印范围:宽="+picWidth+"----高="+picHeight+"---x="+x+"---y="+y);
                EpMediaUtils epMediaUtils=new EpMediaUtils(this);
                epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(videoPath));
                epMediaUtils.setInputPhoto(new FileUtils(this).getFilePathByUri(mSelectedPic.get(0)));
                epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
                epMediaUtils.addDraw(x,y,picWidth,picHeight,false);
                break;
        }
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
}
