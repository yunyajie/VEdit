package com.example.vedit.Application;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by Yajie on 2020/2/10.
 */

public class MyApplication extends Application {
	//全局上下文
	protected Context mContext;

	private static String savePath;
	private static String workPath;
	//闪屏显示判断，第一次启动显示
	private static int isRunning;

	public static int getIsRunning() {
		return isRunning;
	}

	public static void setIsRunning(int isRunning) {
		MyApplication.isRunning = isRunning;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//APP启动
		isRunning=0;
		//初始化全局上下文
		mContext=getApplicationContext();
		choseSavePath();
		copyFilesFassets(getApplicationContext(), "Ress", savePath);
	}

	private void choseSavePath() {
		//savePath = Environment.getExternalStorageDirectory().getPath() + "/EpMedia/";
		savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/VEdit/";
		workPath=savePath+"myworks/";
		//Log.i("SD卡是否被挂载",String.valueOf( Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)));
		File file = new File(savePath);
		if (!file.exists()) {
			file.mkdirs();
		}
		file=new File(workPath);
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
}
