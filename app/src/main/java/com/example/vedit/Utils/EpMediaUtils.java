package com.example.vedit.Utils;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.Widgets.ExecProgressDialog;

import java.util.ArrayList;
import java.util.List;

import VideoHandle.EpDraw;
import VideoHandle.EpEditor;
import VideoHandle.EpText;
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
public  class EpMediaUtils {
    //输出文件名
    private String outputPath;
    //上下文
    private Context context;
    //源文件*****处理一个视频时
    private String inputVideo;
    //图片文件路径
    private String inputPhoto;
    //输入音频路径
    private String inputAudio;
    //进度对话框
    private ExecProgressDialog execProgressDialog;
    //处理结果
    private EpMediaUtilsHandler epMediaUtilsHandler;

    
    //组合操作使用的EpVideo
    private EpVideo epVideo;


    private final String TAG="EpMediaUtils";
    public EpMediaUtils(Context context){
        this.context=context;
        execProgressDialog=new ExecProgressDialog(context);
        epMediaUtilsHandler=new EpMediaUtilsHandler();
    }
    /**
     * Created by Yajie on 2020/4/23 23:13
     * 剪辑
     * 一个参数为剪辑的起始时间，第二个参数为持续时间 单位秒
     */
    public void clip(float start,float duration){
        EpVideo epVideo=new EpVideo(inputVideo);
        epVideo.clip(start,duration);
        Log.i(TAG,"时长剪辑");
        exec(epVideo);
    }
    /** 视频尺寸裁剪  参数分别为裁剪的宽、高、起始位置x，y*/
    public void crop(float width,float height,float x,float y){
        EpVideo epVideo=new EpVideo(inputVideo);
        epVideo.crop(width,height,x,y);
        Log.i(TAG,"视频尺寸裁剪操作");
        exec(epVideo);
    }
    /** 旋转和镜像 第一个参数为旋转的角度，第二个参数为是否镜像，仅支持90,180,270度旋转*/
    public void rotation(int angle,boolean isMirror){
        EpVideo epVideo=new EpVideo(inputVideo);
        epVideo.rotation(angle,isMirror);
        Log.i(TAG,"旋转和镜像（angle="+angle+"----isMirror="+isMirror);
        exec(epVideo);
    }
    /** 添加文字 添加位置坐标x，y  文字字号，字体文件路径，内容,Time类为显示起始时间和持续时间 */
    public void addText(int x, int y, float size, EpText.Color color, String text, int start, int end){
        EpVideo epVideo=new EpVideo(inputVideo);
        epVideo.addText(new EpText(x,y,size,color,
                MyApplication.getSavePath()+"/msyh.ttf",text,new EpText.Time(start,end)));
        exec(epVideo);
    }
    /** 添加logo 参数为图片路径、X、Y、图片的宽高，是否是动图（仅支持png,jpg,gif图片） */
    public void addDraw(int picX,int picY,float picWidth, float picHeight, boolean isAnimation){
        EpDraw epDraw=new EpDraw(inputPhoto,picX,picY,picWidth,picHeight,isAnimation);
        EpVideo epVideo=new EpVideo(inputVideo);
        epVideo.addDraw(epDraw);
        exec(epVideo);
    }
    /** 添加logo 参数为图片路径、X、Y、图片的宽高，是否是动图（仅支持png,jpg,gif图片） */
    public void addDraw(int picX,int picY,float picWidth, float picHeight, boolean isAnimation,int start,int end){
        EpDraw epDraw=new EpDraw(inputPhoto,picX,picY,picWidth,picHeight,isAnimation,start,end);
        EpVideo epVideo=new EpVideo(inputVideo);
        epVideo.addDraw(epDraw);
        exec(epVideo);
    }
    /** 添加自定义滤镜    举例 String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";
     * 底片效果*/
    public void addFilter(){
        EpVideo epVideo=new EpVideo(inputVideo);

        //可用
        //照片底色效果
        String filter = "lutyuv=y=maxval+minval-val:u=maxval+minval-val:v=maxval+minval-val";
        //色度移除,得到黑白图
        String filter2="lutyuv=u=128:v=128";
        //加噪音
        String filter5="noise=alls=100:allf=t+u";
        //模糊 值可以为2、4、8  越大越模糊
        String filter6="boxblur=8";
        //提升亮度 曝光
        String filter3="lutyuv=y=2*val";
        //画面变红
        String filter4="lutyuv=u=1.2*val:v=1.1*val";



        //把亮度(y)反转
        String filter1="lutyuv=y=negval";



        epVideo.addFilter(filter);
        exec(epVideo);
    }
    /** 添加背景音乐 原始视频音量（1为100%，0.7为70%，以此类推），添加音频音量 */
    public void music(float videoVolume, float audioVolume){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.music(inputVideo, inputAudio, outputPath, videoVolume, audioVolume, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
               execProgressDialog.setProgress(progress);
            }
        });
    }
    /** 分离背景音乐  */
    public void demuxerBGM(){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.demuxer(inputVideo, outputPath, EpEditor.Format.MP3, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }
    /** 分离视频图像  */
    public void demuxerVID(){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.demuxer(inputVideo, outputPath, EpEditor.Format.MP4, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }
    /** 视频变速 变速倍率（仅支持0.25-4倍），变速类型（选择VIDEO-视频（屏蔽音频），AUDIO-音频，ALL音视频同步变速）  */
    public void changePTS(float times, EpEditor.PTS pts){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.changePTS(inputVideo, outputPath, times, pts, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }
    /** 视频倒放
     * @param vr 是否视频倒放
     * @param ar 是否音频倒放
     * */
    public void reverse(boolean vr, boolean ar){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.reverse(inputVideo, outputPath, vr, ar, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }
    /** 为视频添加时间
     * @param size  文字大小
     * @param color 文字颜色(white,black,blue,red...)
     * @param x     文字的x坐标
     * @param y     文字的y坐标
     * @param type  时间类型(1==>hh:mm:ss,2==>yyyy-MM-dd hh:mm:ss,3==>yyyy年MM月dd日 hh时mm分ss秒) */
    public void addTime(int x, int y, float size, String color, int type){
        EpVideo epVideo=new EpVideo(inputVideo);
        epVideo.addTime(x,y,size,color,MyApplication.getSavePath()+"/msyh.ttf",type);
        exec(epVideo);
    }
    /** 视频转图片
     * @param w					输出图片宽度
     * @param h					输出图片高度
     * @param rate				每秒视频生成图片数*/
    public void video2pic(int w, int h, float rate){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.video2pic(inputVideo, outputPath, w, h, rate, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                 execProgressDialog.setProgress(progress);
            }
        });
    }
    /** 图片转视频
     * @param w				480	输出视频宽度
     * @param h				320 输出视频高度
     * @param rate			30	输出视频帧率 */
    public void pic2vid(int w, int h, float rate){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        ///////有点问题
        EpEditor.pic2video(inputPhoto, outputPath, w, h, rate, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }
    /** 自定义命令
     * @param cmd              命令
     * @param duration         视频时长（单位微秒）可以填0 */
    public void execCmd(String cmd,long duration){
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.execCmd(cmd, duration, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
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
        if (mSelectedVid.size()<=1){
            //合并视频时进选择了一个视频
            epMediaUtilsHandler.sendEmptyMessage(FinalConstants.REQUEST_CODE_CHOOSEONEVID);
            return;
        }
        ArrayList<EpVideo> epVideos=new ArrayList<>();
        for (Uri temp:mSelectedVid) {
            epVideos.add(new EpVideo(new FileUtils(context).getFilePathByUri(temp)));
        }
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
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                Log.e(TAG,"合并失败");
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }
    //处理结果
    private class EpMediaUtilsHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case FinalConstants.EXEC_PROCESSVIDEO_SUCCESS://处理成功
                    Toast.makeText(context,"处理成功",Toast.LENGTH_SHORT).show();
                    break;
                case FinalConstants.EXEC_PROCESSVIDEO_FAILE://处理失败
                    Toast.makeText(context,"处理失败",Toast.LENGTH_SHORT).show();
                    break;
                case FinalConstants.REQUEST_CODE_CHOOSEONEVID:
                    Toast.makeText(context,"请选择一个以上的视频进行合并",Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        break;
            }
        }
    }

    /** 视频处理  */
    private void exec(EpVideo epVideo){
        //输出文件
        EpEditor.OutputOption outputOption=new EpEditor.OutputOption(outputPath);
        execProgressDialog.DialogInit();
        execProgressDialog.ExecStart();
        EpEditor.exec(epVideo, outputOption, new OnEditorListener() {
            @Override
            public void onSuccess() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_SUCCESS);
            }

            @Override
            public void onFailure() {
                execProgressDialog.ExecEnd();
                epMediaUtilsHandler.sendEmptyMessage(FinalConstants.EXEC_PROCESSVIDEO_FAILE);
            }

            @Override
            public void onProgress(float progress) {
                execProgressDialog.setProgress(progress);
            }
        });
    }
    public void multiOperate(){
        exec(epVideo);
    }
    public void clipInit(float start,float duration){
        if (epVideo==null){
            epVideo=new EpVideo(inputVideo);
        }
        epVideo.clip(start,duration);
    }
    public void cropInit(float width,float height,float x,float y){
        if (epVideo==null){
            epVideo=new EpVideo(inputVideo);
        }
        epVideo.crop(width,height,x,y);
    }
   /** setters  */
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setInputVideo(String inputVideo) {
        this.inputVideo = inputVideo;
    }

    public void setInputPhoto(String inputPhoto) { this.inputPhoto = inputPhoto; }

    public void setInputAudio(String inputAudio) { this.inputAudio = inputAudio; }
}
