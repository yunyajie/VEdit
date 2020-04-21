package com.example.vedit.Player;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.Window;
import android.widget.ImageView;

import com.example.vedit.R;

import java.io.IOException;


/**
 * MediaPlayer 统一管理类
 */
public class MediaManager {

	/** 日志TAG */
	private String TAG = "MediaManager";
	/** MediaPlayer对象 */
	private MediaPlayer mediaPlayer;
	/** 单例实例 */
	private static MediaManager instance;
	/** 上下文  */
	private  Context context;
	/** 视频路径*/
	private Uri videoPath;
	/** 视频时长  */
	private int duration;
	private ImageView ip_play_igview;
	//当前页面
	private Window window;

	// --------------------
	private MediaManager() {}
    public static MediaManager getInstance(){
		if (instance==null){
			instance=new MediaManager();
		}
		return instance;
	}

    /** 初始化 */
    public void init(Activity activity, Uri videoPath){
    	if (mediaPlayer==null){
    		mediaPlayer=new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		}
    	this.context=activity;
    	this.videoPath=videoPath;
    	this.window=activity.getWindow();
    	ip_play_igview=(ImageView)window.findViewById(R.id.ip_play_igview);
		try {
			mediaPlayer.setDataSource(context,videoPath);
			mediaPlayer.prepare();
			this.duration=mediaPlayer.getDuration();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/** 是否播放中  */
	public boolean isPlaying(){
		if (mediaPlayer !=null){
			return mediaPlayer.isPlaying();
		}
		return false;
	}
	/**
	 * 暂停操作(判断null)
	 */
	public void pause(){
		if(mediaPlayer != null){
			mediaPlayer.pause();
			changePlayerView(false);
		}
	}
	/**
	 * 播放操作
	 */
	public void play(){
		if (mediaPlayer!=null){
			if (!isPlaying()){
				mediaPlayer.start();
				changePlayerView(true);
			}
		}
	}
	/** SeekTo  */
	public void seekTo(int process){
		if (mediaPlayer!=null){
			mediaPlayer.seekTo(process);
		}
	}
	/** 销毁*/
	public void destory(){
		if (mediaPlayer!=null){
			if (isPlaying()){
				mediaPlayer.stop();
				changePlayerView(false);
			}
			mediaPlayer.reset();
			mediaPlayer.release();
			mediaPlayer=null;
		}
	}
	/** 获取当前位置  */
	public int getCurrentPosition(){
		if (mediaPlayer!=null){
			return mediaPlayer.getCurrentPosition();
		}
		return 0;
	}
	/** 循环  */
	public void loop(){
		seekTo(0);
		play();
	}
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	/** 改变播放View  */
	private void changePlayerView(boolean isPlaying) {
		ip_play_igview.setImageResource(isPlaying ? R.mipmap.ic_media_stop : R.mipmap.ic_media_play);
	}
}
