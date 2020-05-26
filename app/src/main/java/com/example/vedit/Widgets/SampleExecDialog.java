package com.example.vedit.Widgets;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import com.example.vedit.Interface.ExecInterface;

/**
 * @ProjectName: VEdit
 * @Package: com.example.vedit.Widgets
 * @ClassName: SampleExecDialog
 * @Description: 简单的单选对话框
 * @Author: yunyajie
 * @CreateDate: 2020/5/26 12:55
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/5/26 12:55
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class SampleExecDialog {
    private Dialog dialog;
    public void diaogInit(final Context context,String title, String []selectElementds,final ExecInterface execInterface){
             dialog=new AlertDialog.Builder(context)
                .setTitle(title)
                .setCancelable(false)
                .setSingleChoiceItems(selectElementds, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        execInterface.setSelectEle(which);
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Toast.makeText(context,"取消",Toast.LENGTH_SHORT).show();
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        execInterface.exec();
                    }
                })
                .create();
    }
    public void dialogShow(){
        dialog.show();
    }
}
