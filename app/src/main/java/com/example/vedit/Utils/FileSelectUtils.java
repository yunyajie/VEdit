package com.example.vedit.Utils;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;

import com.example.vedit.R;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.List;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Utils
 * @ClassName: FileSelectUtils
 * @Description: 文件选择工具
 * @Author: yunyajie
 * @CreateDate: 2020/4/23 22:22
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/23 22:22
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class FileSelectUtils {
    //选择多个视频  上限为5
    public void selectMoreVid(List<Uri> mSelectedVid, Activity activity,int REQUESTCODE_SELECTMOREVID){
        if (mSelectedVid!=null) mSelectedVid.clear();
        Matisse.from(activity)
                .choose(MimeType.ofVideo())
                .maxSelectable(5)
                .theme(R.style.Matisse_Dracula)
                .showPreview(true)
                .showSingleMediaType(true)
                .imageEngine(new GlideEngine())
                .forResult(REQUESTCODE_SELECTMOREVID);

    }
    //选择一个视频
    public  void selectOneVid(List<Uri> mSelectedVid, Activity activity,int REQUESTCODE_SELECTONEVID){
        if (mSelectedVid!=null) mSelectedVid.clear();
        Matisse.from(activity)
                .choose(MimeType.ofVideo())
                .showSingleMediaType(true)
                .theme(R.style.Matisse_Dracula)
                .showPreview(true)
                .imageEngine(new GlideEngine())
                .forResult(REQUESTCODE_SELECTONEVID);
    }
    //选择一张图片
    public void selectOnePic(List<Uri> mSelectedPic,Activity activity,int REQUESTCODE_SELECTONEPIC){
        if (mSelectedPic!=null) mSelectedPic.clear();
        Matisse.from(activity)
                .choose(MimeType.ofImage())
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.5f)
                .imageEngine(new GlideEngine())
                .showSingleMediaType(true)
                .theme(R.style.Matisse_Dracula)
                .forResult(REQUESTCODE_SELECTONEPIC);
    }
}
