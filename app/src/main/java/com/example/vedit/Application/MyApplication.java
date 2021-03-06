package com.example.vedit.Application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yajie on 2020/2/10.
 */

public class MyApplication extends Application {
	//全局上下文
	protected Context mContext;

	private static String savePath;
	private static String workPath;
	private static String picPath;
	//闪屏显示判断，第一次启动显示
	private static int isRunning;

	private static List<String>newFiles;


	@Override
	public void onCreate() {
		super.onCreate();
		//APP启动
		isRunning=0;
		newFiles=new ArrayList<String>();
		//初始化全局上下文
		mContext=getApplicationContext();
		choseSavePath();
		copyFilesFassets(getApplicationContext(), "Ress", savePath);
	}

	private void choseSavePath() {
		//savePath = Environment.getExternalStorageDirectory().getPath() + "/EpMedia/";
		savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VEdit/";
		workPath=savePath+"myworks/";
		picPath=savePath+"pic/";
		//Log.i("SD卡是否被挂载",String.valueOf( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)));
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		file=new File(workPath);
		if (!file.exists()){
			file.mkdirs();
		}
		file=new File(picPath);
		if (!file.exists()){
			file.mkdirs();
		}
	}

	/**
	 * 获取缓存路径
	 *
	 * @return
	 */
	public static String getSavePath() {
		return savePath;
	}
	public static String getWorkPath(){return workPath;}
	public static String getPicPath(){return picPath;}
	public static List<String> getNewFiles(){return newFiles;}
	public static void  addNewFile(String newfile){newFiles.add(newfile);}
	public static void clearNewFiles(){newFiles.clear();}
	/** 获取字体文件路径  */
	public static String getTTFPath(){return savePath+"/msyh.ttf";}

	/**
	 * 从assets目录中复制文件到本地
	 *
	 * @param context Context
	 * @param oldPath String  原文件路径
	 * @param newPath String  复制后路径
	 */
	public static void copyFilesFassets(Context context, String oldPath, String newPath) {
		try {
			String fileNames[] = context.getAssets().list(oldPath);
			if (fileNames.length > 0) {
				File file = new File(newPath);
				file.mkdirs();
				for (String fileName : fileNames) {
					copyFilesFassets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
				}
			} else {
				InputStream is = context.getAssets().open(oldPath);
				File ff = new File(newPath);
				if (!ff.exists()) {
					FileOutputStream fos = new FileOutputStream(ff);
					byte[] buffer = new byte[1024];
					int byteCount = 0;
					while ((byteCount = is.read(buffer)) != -1) {
						fos.write(buffer, 0, byteCount);
					}
					fos.flush();
					is.close();
					fos.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static int getIsRunning() {
		return isRunning;
	}

	public static void setIsRunning(int isRunning) {
		MyApplication.isRunning = isRunning;
	}
}
