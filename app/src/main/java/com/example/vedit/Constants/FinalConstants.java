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
	/** 选择不多于5个视频  */
	public static final int REQUEST_CODE_CHOOSEMULTIVID=FINAL_BASE+8;

	/** 处理成功  */
	public static final int EXEC_PROCESSVIDEO_SUCCESS=FINAL_BASE+9;
	/** 处理失败  */
	public static final int EXEC_PROCESSVIDEO_FAILE=FINAL_BASE+10;


	/** 选择一个视频的键*/
	public static final String INTENT_SELECTONEVID_KEY="SelectedOneVid";
	/** 选择不多于5个的视频  */
	public static final String INTENT_SELECTMULTIVID_KEY="SelectedMultiVid";
	/** 包名  */
	public static final String PACKAGE_NAME="com.example.vedit";

}
