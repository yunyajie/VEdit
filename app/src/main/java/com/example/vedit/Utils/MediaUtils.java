package com.example.vedit.Utils;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

/**
 * @ProjectName: EpMediaTest
 * @Package: com.example.epmediatest.Utils
 * @ClassName: MediaUtils
 * @Description: 获取视频信息
 * @Author: yunyajie
 * @CreateDate: 2020/3/17 22:14
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/17 22:14
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */


public class MediaUtils {
    private long duration;//视频时长  毫秒
    private int height;//高度
    private int width;//宽度
    private static MediaUtils sMediaUtils;
    private MediaMetadataRetriever retriever;
    private MediaUtils(){ }
    public static MediaUtils getInstance(){
        if (sMediaUtils==null){
            sMediaUtils=new MediaUtils();
        }
        return sMediaUtils;
    }

    public void setSource(String filePath){
        retriever=new MediaMetadataRetriever();
        retriever.setDataSource(filePath,new HashMap<String, String>());
        duration=Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
        width=Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
        height=Integer.parseInt(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
    }
    /**
     * Created by Yajie on 2020/3/23 21:19
     * 获取视频某一帧  *@para  timeMs毫秒  *@para listener
     */
    public Bitmap decodeFrame(long timeMs){
        if (retriever==null){
            return null;
        }
//        传入第一个参数为微妙，毫秒*1000，
//        第二个参数
//                OPTION_CLOSEST
//        在给定的时间，检索最近一个帧,这个帧不一定是关键帧。
//        OPTION_CLOSEST_SYNC
//        在给定的时间，检索最近一个同步与数据源相关联的的帧（关键帧）。
//        OPTION_NEXT_SYNC
//        在给定时间之后检索一个同步与数据源相关联的关键帧。
//        OPTION_PREVIOUS_SYNC
//                在给定时间之前检索一个同步与数据源相关的关键帧

        Bitmap bitmap=retriever.getFrameAtTime(timeMs*1000,
                MediaMetadataRetriever.OPTION_CLOSEST);
        if (bitmap!=null){
            return bitmap;
        }
        return null;
    }


    public long getDuration() {
        return duration;
    }
    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }

}
