package com.example.vedit.Player;

import android.media.MediaPlayer;


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

	// --------------------
	private MediaManager() {}
    public static MediaManager getInstance(){
		if (instance==null){
			instance=new MediaManager();
		}
		return instance;
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
		}
	}

//	// =============== 封装回调事件 =================
//	/** MediaPlayer回调事件 */
//	private MediaListener mMeidaListener;
//
//	/** MediaPlayer回调接口 */
//	public interface MediaListener {
//
//		void onPrepared();
//
//		void onCompletion();
//
//		void onBufferingUpdate(int percent);
//
//		void onSeekComplete();
//
//		void onError(int what, int extra);
//
//		void onVideoSizeChanged(int width, int height);
//	}
//
//	/**
//	 * Created by Yajie on 2020/4/1 12:09
//	 * 设置MediaPlayer回调
//	 */
//	public void setmMeidaListener(MediaListener mMeidaListener){
//		this.mMeidaListener=mMeidaListener;
//	}

	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
}
