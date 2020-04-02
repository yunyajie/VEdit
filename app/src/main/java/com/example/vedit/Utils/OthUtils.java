package com.example.vedit.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

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

}
