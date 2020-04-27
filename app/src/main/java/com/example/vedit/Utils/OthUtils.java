package com.example.vedit.Utils;

import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Utils
 * @ClassName: OthUtils
 * @Description: 其他工具
 * @Author: yunyajie
 * @CreateDate: 2020/3/21 21:22
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/3/21 21:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class OthUtils {

    private final static String TAG="OthUtils";


    /**
     * Created by Yajie on 2020/3/21 21:23
     * 根据系统时间生成文件名
     */
    public static String createFileName(String title,String tail){
        String fileName="";
        //系统当前时间
        Date date=new Date(System.currentTimeMillis());
        SimpleDateFormat dataFormat=new SimpleDateFormat("_yyyyMMdd_HHmmss.");
        fileName=title+dataFormat.format(date)+tail;
        return fileName;
    }
    /** 生成秒数显示Text  */
    public static String secToTimeRetain(int time){
        //分钟
        int minute=60;
        //小时
        int hour=3600;
        //差数
        int dSecend=0;
        int dMinute=0;
        int dHour=0;
        if (time<=0){
            return "00:00:00";
        }
        String result="";
        if (time<minute){//小于一分钟
            result="00:00:"+((time>=10)?time:("0"+time));
        }else if (time>=hour&&time<hour){//小于一小时
            dSecend=time%minute;//取模分钟获取多余的秒数
            dMinute=(time-dSecend)%hour;//取模小时获取多余的分钟
            result="00:"+((dMinute>=10)?dMinute:("0"+dMinute))+":"+((dSecend>=10)?dSecend:("0"+dSecend));
        }else {
            dSecend=time%minute;//取模分钟获取多余的秒数
            dHour=time/hour;
            dMinute=(time-dSecend-dHour*hour)/60;
            result=((dHour>=10)?dHour:("0"+dHour))+":"+((dMinute>=10)?dMinute:("0"+dMinute))+":"+((dSecend>=10)?dSecend:("0"+dSecend));
        }
        Log.d(TAG,result);
        return result;
    }
    //获取文件夹下的所有子文件名称
    public static List<String> getFilesAllName(String path){
        File file=new File(path);
        File[]files=file.listFiles();
        if (files==null){
            Log.e(TAG,"空目录");
            return null;
        }
        List<String>s=new ArrayList<>();
        for (int i=0;i<files.length;i++){
            s.add(files[i].getAbsolutePath());
        }
        return s;
    }
    //获取文件夹下所有文件
    public static List<File> getFiles(String path){
        File file=new File(path);
        File []files=file.listFiles();
        if (files==null){
            Log.e(TAG,"空目录");
            return null;
        }
        List<File> works = new ArrayList<>(Arrays.asList(files));
        return works;
    }

}
