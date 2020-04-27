package com.example.vedit.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.FileSelectUtils;
import com.example.vedit.Utils.FileUtils;
import com.example.vedit.Utils.OthUtils;
import com.zhihu.matisse.Matisse;

import java.util.List;

public class ActionsActivity extends NoTitleActivity implements View.OnClickListener {

    private Button bt_actions1;
    private Button bt_actions2;
    private Button bt_actions3;
    private Button bt_actions4;
    List<Uri> mSelectedVid;
    private static String TAG="ActionsActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        initViews();
    }
    //初始化控件
    private void initViews() {
        bt_actions1=(Button)findViewById(R.id.bt_actions1);
        bt_actions2=(Button)findViewById(R.id.bt_actions2);
        bt_actions3=(Button)findViewById(R.id.bt_actions3);
        bt_actions4=(Button)findViewById(R.id.bt_actions4);

        bt_actions1.setOnClickListener(this);
        bt_actions2.setOnClickListener(this);
        bt_actions3.setOnClickListener(this);
        bt_actions4.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_actions1:
                //时长剪辑
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this, FinalConstants.REQUESTCODE_SELECTVID_TRIM);
                break;
            case R.id.bt_actions2:
                //尺寸裁剪
                new FileSelectUtils().selectOneVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTVID_CROP);
                break;
            case R.id.bt_actions3:
                break;
            case R.id.bt_actions4:
                //合并视频
                new FileSelectUtils().selectMoreVid(mSelectedVid,ActionsActivity.this,FinalConstants.REQUESTCODE_SELECTMOREVID_MERGE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_TRIM&&resultCode==RESULT_OK){
            //时长剪辑
            mSelectedVid= Matisse.obtainResult(data);
            Log.d(TAG,"Matisse-mSelectedOneVid="+mSelectedVid);
            //创建意图对象
            Intent intent=new Intent(this,TrimActivity.class);
            //传递键值对
            intent.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            startActivity(intent);
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTVID_CROP&&resultCode==RESULT_OK){
            //时长裁剪
            mSelectedVid= Matisse.obtainResult(data);
            Log.d(TAG,"Matisse-mSelectedOneVid="+mSelectedVid);
            //创建意图对象
            Intent intent=new Intent(this,CropActivity.class);
            //传递键值对
            intent.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            startActivity(intent);
        }
        if (requestCode==FinalConstants.REQUESTCODE_SELECTMOREVID_MERGE&&resultCode==RESULT_OK){
            //视频合并
            mSelectedVid=Matisse.obtainResult(data);
            EpMediaUtils epMediaUtils =new EpMediaUtils(this);
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+ OthUtils.createFileName("VIDEO","mp4"));
            epMediaUtils.mergeVideos(mSelectedVid);
        }
    }
}
