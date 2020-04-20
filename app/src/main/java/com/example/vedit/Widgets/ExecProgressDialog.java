package com.example.vedit.Widgets;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Widgets
 * @ClassName: ExecProgressDialog
 * @Description: 视频处理对话框
 * @Author: yunyajie
 * @CreateDate: 2020/4/20 21:13
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/4/20 21:13
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class ExecProgressDialog {
    private ProgressDialog progressDialog;
    public  ExecProgressDialog(Context context){
        progressDialog=new ProgressDialog(context);
    }
    public void DialogInit(){
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setTitle("正在处理...");
    }
    public void ExecStart(){
        progressDialog.setProgress(0);
        progressDialog.show();
    }
    public void ExecEnd(){
        progressDialog.dismiss();
    }
    public void setProgress(float progress){
        progressDialog.setProgress((int)(progress*100));
    }

}
