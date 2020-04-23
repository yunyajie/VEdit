package com.example.vedit.Utils;

import android.app.Activity;
import android.net.Uri;

import com.example.vedit.Activities.MainActivity;
import com.example.vedit.Constants.FinalConstants;
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
    //选择一个视频
    public void selectOneVid(List<Uri> mSelectedVid, Activity activity){
        if (mSelectedVid!=null) mSelectedVid.clear();
        Matisse.from(activity)
                .choose(MimeType.ofVideo())
                .showSingleMediaType(true)
                .theme(R.style.Matisse_Dracula)
                .showPreview(true)
                .imageEngine(new GlideEngine())
                .forResult(FinalConstants.REQUEST_CODE_CHOOSEONEVID);
    }
    //选择多个视频  上限为5
    public void selectMoreVid(List<Uri> mSelectedVid, Activity activity){
        if (mSelectedVid!=null) mSelectedVid.clear();
        Matisse.from(activity)
                .choose(MimeType.ofVideo())
                .maxSelectable(5)
                .theme(R.style.Matisse_Dracula)
                .showPreview(true)
                .showSingleMediaType(true)
                .imageEngine(new GlideEngine())
                .forResult(FinalConstants.REQUEST_CODE_CHOOSEMULTIVID);

    }
}
