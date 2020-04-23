package com.example.vedit.Utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Widgets.ExecProgressDialog;

import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Utils
 * @ClassName: EpMediaUtils
 * @Description: 视频编辑工具整合
 * @Author: yunyajie
 * @CreateDate: 2020/4/23 23:03
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/23 23:03
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class EpMediaUtils {
    //输出文件名
    private String outputPath;
    //上下文
    private Context context;
    //源文件
    private String inputPath;
    //进度对话框
    private ExecProgressDialog execProgressDialog;


    private final String TAG="EpMediaUtils";
    public EpMediaUtils(Context context){
        this.context=context;
        execProgressDialog=new ExecProgressDialog(context);
    }
    /**
     * Created by Yajie on 2020/4/23 23:13
     * 剪辑
     * 一个参数为剪辑的起始时间，第二个参数为持续时间 单位秒
     */
    public void clip(final float start, final float duration){
        EpVideo epVideo=new EpVideo(inputPath);
        epVideo.clip(start,duration);
        EpEditor.OutputOption outputOption=new EpEditor.OutputOption(outputPath);
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG,inputPath+" do clip("+start+","+duration+") successfully.");
                execProgressDialog.ExecEnd();
            }

            @Override
            public void onFailure() {
                Log.e(TAG,inputPath+" fail to clip("+start+","+duration+").----outPath="+outputPath);
                execProgressDialog.ExecEnd();
            }

            @Override
            public void onProgress(float progress) {
               execProgressDialog.setProgress(progress);
            }
        });
    }
    /**
     * Created by Yajie on 2020/4/23 23:27
     * 合并视频
     * 输入参数为视频列表
     */
    public void mergeVideos(List<Uri> mSelectedVid) {
        ArrayList<EpVideo> epVideos=new ArrayList<>();
        for (Uri temp:mSelectedVid) epVideos.add(new EpVideo(new FileUtils(context).getFilePathByUri(temp)));
        //输出选项，参数为输出文件路径
        EpEditor.OutputOption outputOption=new EpEditor.OutputOption(MyApplication.getWorkPath()+ OthUtils.createFileName("VIDEO","mp4"));
        //设置视频宽高为第一个视频的宽高
        MediaUtils mediaUtils=MediaUtils.getInstance();
        mediaUtils.setSource(new FileUtils(context).getFilePathByUri(mSelectedVid.get(0)));
        int width=Integer.parseInt(mediaUtils.getWidth());
        int height=Integer.parseInt(mediaUtils.getHeight());
        mediaUtils.RetrieverRelease();
        Log.i(TAG,"width="+width+"-----height="+height);
        outputOption.setWidth(width);
        outputOption.setHeight(height);
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.merge(epVideos, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG,"合并成功");
                execProgressDialog.ExecEnd();
            }

            @Override
            public void onFailure() {
                Log.e(TAG,"合并失败");
                execProgressDialog.ExecEnd();
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }

   /** setters  */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }
}
