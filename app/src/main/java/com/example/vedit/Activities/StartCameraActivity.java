package com.example.vedit.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.R;
import com.example.vedit.Utils.OthUtils;

import java.io.File;

public class StartCameraActivity extends AppCompatActivity {
    private static final String SAVE_PATH = MyApplication.getWorkPath();
    private AlertDialog mPermissionDialog;
    private String mPackName="com.example.vedit";
    private Button bt_takePhoto;
    private Button bt_takeVideo;
    private String photoName="temp.jpg";
    private Uri photoUri=null;
    private Uri videoUri=null;
    private String videoName="temp.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startcamera);

        initCameraPermission();
    }


    private void initCameraPermission() {
        /**
         * Created by Yajie on 2020/3/19 22:22
         * 申请读取外存权限
         */
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)!=
                PackageManager.PERMISSION_GRANTED){//没有权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA}, FinalConstants.MY_PERMISSIONS_REQUEST_CODE_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case FinalConstants.MY_PERMISSIONS_REQUEST_CODE_CAMERA:
                //如果被拒绝结果数组为空
                if (grantResults.length>0&&
                        grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.i("StartCameraActivity","相机权限授予");
                }else {
                    //未授予
                    Log.i("StartCameraActivity","相机权限未被授予");
                    showPermissionDialog();
                }
                break;
        }

    }

    private void showPermissionDialog() {
        if (mPermissionDialog==null){
            mPermissionDialog=new AlertDialog.Builder(this)
                    .setMessage("禁用相机权限该功能无法使用，请授予")
                    .setCancelable(false)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionDialog.cancel();
                            //cancelPermissionDialog();
                            Uri packageURL=Uri.parse("package:"+mPackName);
                            Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    packageURL);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            mPermissionDialog.cancel();
                            StartCameraActivity.this.finish();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    public void takeVideo(View view) {
        /**
         * @method  takeVideo
         * @description 调用系统相机拍摄视频
         * @date: 2020/3/9 15:37
         * @author: yajie
         * @param
         * @return void
         */
        //创建保存视频的文件夹
//        File videoPath=new File(VIDEO_PATH);
//        if (!videoPath.exists()){
//            Log.i("创建文件夹",VIDEO_PATH.toString());
//            videoPath.mkdirs();
//        }
        videoName= OthUtils.createFileName("VIDEO","mp4");
        Log.i("视频文件名",videoName);
        Intent intent=new Intent();
        intent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        //设置视频录制质量
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY,1);
        //设置视频最大允许录制时长(10秒)
       // intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,10*1000);
//        //设置视频最大允许尺寸
//        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,1000);
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            videoUri = FileProvider.getUriForFile(this,
                    "com.example.vedit.fileprovider",new File(SAVE_PATH,videoName));
        }else {
            videoUri=Uri.fromFile(new File(SAVE_PATH,videoName));
        }

        if (intent.resolveActivity(getPackageManager())!=null){
            intent.putExtra(MediaStore.EXTRA_OUTPUT,videoUri);
            startActivityForResult(intent,FinalConstants.REQUEST_CAPTURE_VIDEO);
        }
    }

    public void takePhoto(View view) {
        /**
         * @method  takepicture
         * @description 调用系统相机拍照
         * @date: 2020/3/8 18:22
         * @author: yajie
         * @param
         * @return void
         */
        //创建保存图片的文件夹
//        File photoPath=new File(PHOTO_PATH);
//        if (!photoPath.exists()){
//            Log.i("创建文件夹",photoPath.toString());
//            photoPath.mkdirs();
//        }
        //生成唯一文件名
        photoName =OthUtils.createFileName("IMG","jpg");
        Log.i("文件名", photoName);
        //获取图片uri
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            photoUri= FileProvider.getUriForFile(this,
                    "com.example.vedit.fileprovider",new File(SAVE_PATH, photoName));
        }else {
            photoUri=Uri.fromFile(new File(SAVE_PATH, photoName));
        }
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager())!=null){

            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);

            startActivityForResult(intent,FinalConstants.REQUEST_CAPTURE_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FinalConstants.REQUEST_CAPTURE_IMAGE:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "照片已保存到本地", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "取消拍照", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            case FinalConstants.REQUEST_CAPTURE_VIDEO:
                if (resultCode == RESULT_OK) {
                    Toast.makeText(this, "拍摄完成", Toast.LENGTH_SHORT).show();
                } else {
                    File tempVideo = new File(SAVE_PATH, videoName);
                    if (tempVideo.exists()) {
                        Log.i("提示", "视频文件" + videoName + "存在");
                        tempVideo.delete();
                        if (!tempVideo.exists()) Log.i("提示", "视频文件" + videoName + "已删除");
                    }
                    Toast.makeText(this, "取消拍摄", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
        }
    }
}
