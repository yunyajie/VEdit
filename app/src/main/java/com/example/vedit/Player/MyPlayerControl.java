package com.example.vedit.Player;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.vedit.R;

import java.util.logging.Handler;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Player
 * @ClassName: MyPlayerControl
 * @Description: 播放控制器
 * @Author: yunyajie
 * @CreateDate: 2020/3/31 18:34
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/31 18:34
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class MyPlayerControl implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    //日志
    private final String TAG="MyPlayerControl";
    //上下文
    private Context mContext;
    //外部Handler vHandler改变View
    //是否暂停
    private boolean isPause=false;
    //是否销毁
    private boolean isDestroy=false;
    //当前页面
    private Window window;
    //外部Handler，改变View
    private Handler vHandler;
    private SurfaceView ip_surfaceview;
    private SurfaceHolder surfaceHolder;
    //底部Linear
    private LinearLayout ip_function_linear;
    //播放、暂停IgView
    private ImageView ip_play_igview;
    //当前播放时间
    private TextView ip_ctime_tv;
    //进度滑动SeekBar
    private SeekBar ip_seekbar;
    //视频总时间
    private TextView ip_ttime_tv;
    /**     
     * Created by Yajie on 2020/3/31 21:22
     * 初始化构造函数
     */
    public MyPlayerControl(Activity activity,Handler vHandler){
        this.mContext=activity;
        this.window=activity.getWindow();
        this.vHandler=vHandler;
        //初始化View、Values、Listeners
        initViews();
        initValues();
        initListeners();
        
    }

    /** 初始化事件  */
    private void initListeners() {
        ip_play_igview.setOnClickListener(this);
        ip_seekbar.setOnSeekBarChangeListener(this);


    }

    /** 初始化操作  */
    private void initValues() {
        
    }

    /** 初始化View  */
    private void initViews() {
        if (window!=null){
            ip_surfaceview=(SurfaceView)window.findViewById(R.id.ip_surfaceview);
            ip_function_linear=(LinearLayout)window.findViewById(R.id.ip_function_linear);
            ip_play_igview=(ImageView)window.findViewById(R.id.ip_play_igview);
            ip_ctime_tv=(TextView)window.findViewById(R.id.ip_ctime_tv);
            ip_ttime_tv=(TextView)window.findViewById(R.id.ip_ttime_tv);
            ip_seekbar=(SeekBar)window.findViewById(R.id.ip_seekbar);
        }
    }

    /**
     * Created by Yajie on 2020/3/31 21:11
     * 设置播放View显示的图标
     */
    private void changePlayerView(boolean isPlaying){
        ip_play_igview.setImageResource(isPlaying? R.mipmap.ic_media_stop:R.mipmap.ic_media_play);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            //播放点击操作
            case R.id.ip_play_igview:
                //如果播放中，则显示暂停
                boolean isPlaying=MediaManager.getInstance().isPlaying();
                //进行取反设置
                changePlayerView(!isPlaying);
                //播放中则进行暂停
                if (isPlaying){
                    //暂停定时器

                    //暂停视频
                    MediaManager.getInstance().pause();
                    if (vHandler!=null){


                    }
                }
        }

    }


    /** -------------SeekBar滑动回调------------  */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //当滑动条发生变化时调用该方法

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //开始滑动触发
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //滑动结束
        //获取滑动值
        int touchTime=seekBar.getProgress();
        MediaPlayer mediaPlayer=MediaManager.getInstance().getMediaPlayer();
        if (mediaPlayer!=null){
            //设置当前进度
            ip_seekbar.setProgress(touchTime);
            //刷新播放时间
            refPlayerTime();
            //设置滑动进度
            mediaPlayer.seekTo(touchTime);
            //播放
            if (!mediaPlayer.isPlaying()){
                mediaPlayer.start();
            }

            //切换播放图标
            changePlayerView(true);
        }
    }

    //刷新播放事件
    private void refPlayerTime() {

    }


}
