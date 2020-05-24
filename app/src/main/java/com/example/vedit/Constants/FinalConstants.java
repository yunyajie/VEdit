package com.example.vedit.Constants;

/**
 * 常量配置
 */
public class FinalConstants {

	/** 基数 */
	public static final int FINAL_BASE = 1000;
	/** 外存权限申请码*/
	public static final int MY_PERMISSIONS_REQUEST_CODE_EXTRENALSTORAGE = FINAL_BASE + 1;
	/**  选择一个视频请求码 */
	public static final int REQUEST_CODE_CHOOSEONEVID=FINAL_BASE+2;
    /**  进度条前进 */
	public static final int PROGRESS_CHANGED=FINAL_BASE+3;
	/** 进度条改变  */
	public static final int SEEKBAR_CHANGED =FINAL_BASE+4;
	//** 相机权限请求码*/
	public static final int MY_PERMISSIONS_REQUEST_CODE_CAMERA=FINAL_BASE+5;
	/** 拍照码  */
	public static final int REQUEST_CAPTURE_IMAGE =FINAL_BASE+6;
	/** 录像码  */
	public static final int REQUEST_CAPTURE_VIDEO=FINAL_BASE+7;
	/** 合并视频  */
	public static final int REQUESTCODE_SELECTMOREVID_MERGE=FINAL_BASE+8;
	/** 处理成功  */
	public static final int EXEC_PROCESSVIDEO_SUCCESS=FINAL_BASE+9;
	/** 处理失败  */
	public static final int EXEC_PROCESSVIDEO_FAILE=FINAL_BASE+10;
	/** 时长剪辑  */
	public static final int REQUESTCODE_SELECTVID_TRIM=FINAL_BASE+11;
	/** 尺寸裁剪  */
	public static final int REQUESTCODE_SELECTVID_CROP=FINAL_BASE+12;
	/** 获取背景音乐  */
	public static final int REQUESTCODE_SELECTVID_GETBGM=FINAL_BASE+13;
	/** 添加背景音乐  */
	public static final int REQUESTCODE_SELECTVID_ADDBGM=FINAL_BASE+14;
	/** 视频转图片  */
	public static final int REQUESTCODE_SELECTVID_V2P=FINAL_BASE+15;
	/** 选择音频  */
	public static final int REQUESTCODE_SELECTAUD_ADDBGM=FINAL_BASE+16;
	/** 倒放  */
	public static final int REQUESTCODE_SELECTVID_REVERSE=FINAL_BASE+17;
	/** 旋转和镜像  */
	public static final int REQUESTCODE_SELECTVID_ROTATION=FINAL_BASE+18;
	/** 时间水印  */
	public static final int REQUESTCODE_SELECTVID_ADDTIME=FINAL_BASE+19;
	/** 倍速  */
	public static final int REQUESTCODE_SELECTVID_CHANGEPTS=FINAL_BASE+20;
	/** 图片或文字水印  */
	public static final int REQUESTCODE_SELECTVID_WATERMARK=FINAL_BASE+21;

	/** 选择一个视频的键*/
	public static final String INTENT_SELECTONEVID_KEY="SelectedOneVid";
	/** 选择不多于5个的视频  */
	public static final String INTENT_SELECTMULTIVID_KEY="SelectedMultiVid";
	/** 包名  */
	public static final String PACKAGE_NAME="com.example.vedit";

}
