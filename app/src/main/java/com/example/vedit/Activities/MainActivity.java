package com.example.vedit.Activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.example.vedit.Application.MyApplication;
import com.example.vedit.Constants.FinalConstants;
import com.example.vedit.R;
import com.example.vedit.Utils.EpMediaUtils;
import com.example.vedit.Utils.FileUtils;
import com.example.vedit.Utils.Item;
import com.example.vedit.Utils.MyAdapter;
import com.example.vedit.Utils.OthUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends NoTitleActivity {
    private AlertDialog mPermissionDialog;
    private ImageButton ib_camera;
    private ImageButton ib_edit;
    private SwipeMenuListView myWorks_lv;
    private MyAdapter<Item> myAdapter;
    private ArrayList<Item> myWorks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ib_camera=(ImageButton)findViewById(R.id.ib_camera);
        ib_edit=(ImageButton)findViewById(R.id.ib_edit);
        initWriteExternalPermission();
        myWorks_lv =(SwipeMenuListView)findViewById(R.id.myworks_lv);
        myWorks=new ArrayList<Item>();
        for (int i=0;i<20;i++){
            myWorks.add(new Item(R.mipmap.ic_launcher,"测试一下"+i));
        }

        myAdapter=new MyAdapter<Item>(myWorks,R.layout.item_mywork) {
            @Override
            public void bindView(ViewHolder holder, Item obj) {
                holder.setImageResource(R.id.mywork_iv,obj.getIconId());
                holder.setText(R.id.mywork_tv,obj.getIconName());
            }
        };

        myWorks_lv.setAdapter(myAdapter);
        //step1.creat a MenuCreator
        SwipeMenuCreator creator=new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                //create "rename" item
                SwipeMenuItem renameItem=new SwipeMenuItem(
                        getApplicationContext());
                renameItem.setBackground(new ColorDrawable(Color.rgb(0xc9,
                        0xc9,0xce)));

                renameItem.setWidth(dp2px(90));
                renameItem.setTitle("Rename");
                renameItem.setTitleColor(Color.WHITE);
                renameItem.setTitleSize(18);
                menu.addMenuItem(renameItem);
                //create "delete" item
                SwipeMenuItem deleteItem=new SwipeMenuItem(
                        getApplicationContext());
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xf9,
                        0x3f,0x25)));
                deleteItem.setWidth(dp2px(90));
                deleteItem.setIcon(R.drawable.ic_delete);
                menu.addMenuItem(deleteItem);
            }
        };
        //set creator
        myWorks_lv.setMenuCreator(creator);
        //step2.listener item click event
        myWorks_lv.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                Item item=myWorks.get(position);
                Log.i("MainActivity","点击了第"+position+"Item");
                switch (index){
                    case 0:
                        //rename
                        Log.i("MainAcivity","重命名第"+position+"个Item");
                        break;
                    case 1:
                        //delete
                        Log.i("MainAcivity","删除第"+position+"个Item");
                        myWorks.remove(position);
                        myAdapter.notifyDataSetChanged();
                        break;
                }
                return false;
            }
        });
        //set SwipeListener
        myWorks_lv.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {
            @Override
            public void onSwipeStart(int position) {
                //swipe start
                Log.i("MainActivity","滑动开始");
            }

            @Override
            public void onSwipeEnd(int position) {
               //swipe end
                Log.i("MainActivity","滑动结束");
            }
        });
        //set MenuStateChangeListener
        myWorks_lv.setOnMenuStateChangeListener(new SwipeMenuListView.OnMenuStateChangeListener() {
            @Override
            public void onMenuOpen(int position) {
                Log.i("MainActivity","菜单展开");
            }

            @Override
            public void onMenuClose(int position) {
                Log.i("MainActivity","菜单关闭");
            }
        });
        //other setting
//		listView.setCloseInterpolator(new BounceInterpolator());
        //test item long click
        myWorks_lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),position+"long click",
                        Toast.LENGTH_LONG).show();
                return false;
            }
        });
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    private void initWriteExternalPermission() {
        /**
         * Created by Yajie on 2020/3/19 22:22
         * 申请读取外存权限
         */
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                PackageManager.PERMISSION_GRANTED){//没有权限
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, FinalConstants.MY_PERMISSIONS_REQUEST_CODE_EXTRENALSTORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case FinalConstants.MY_PERMISSIONS_REQUEST_CODE_EXTRENALSTORAGE:
                //如果被拒绝结果数组为空
                if (grantResults.length>0&&
                grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Log.i("MainActivity","外存读取权限授予");
                }else {
                    //未授予
                    Log.i("MainActivity","外存读取权限未被授予");
                    showPermissionDialog();
                }
                break;
        }

    }

    private void showPermissionDialog() {
        if (mPermissionDialog==null){
            mPermissionDialog=new AlertDialog.Builder(this)
                    .setMessage("禁用存储权限应用无法使用，请授予")
                    .setCancelable(false)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mPermissionDialog.cancel();
                            //cancelPermissionDialog();
                            Uri packageURL=Uri.parse("package:"+FinalConstants.PACKAGE_NAME);
                            Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    packageURL);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //关闭页面或者做其他操作
                            mPermissionDialog.cancel();
                           MainActivity.this.finish();
                        }
                    })
                    .create();
        }
        mPermissionDialog.show();
    }

    public void startCamera(View view) {
        Intent intent=new Intent(this,StartCameraActivity.class);
        startActivity(intent);
    }

    public void startEdit(View view) {
          selectOneVid();
//        new FileSelectUtils().selectMoreVid(mSelectedVid,MainActivity.this);
    }

    @Override
    public void finish() {
        moveTaskToBack(true);
    }

    List<Uri>mSelectedPic;
    List<Uri>mSelectedVid;
    private static String TAG="MainActivity";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==FinalConstants.REQUEST_CODE_CHOOSEONEVID&&resultCode==RESULT_OK){//选择一个视频
            mSelectedVid=Matisse.obtainResult(data);
            Log.d(TAG,"Matisse-mSelectedOneVid="+mSelectedVid);
            EpMediaUtils epMediaUtils=new EpMediaUtils(this);
            epMediaUtils.setInputVideo(new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
            epMediaUtils.addFilter();
//            //创建意图对象
//            Intent intent=new Intent(this,TrimActivity.class);
//            //传递键值对
//            intent.putExtra(FinalConstants.INTENT_SELECTONEVID_KEY,new FileUtils(this).getFilePathByUri(mSelectedVid.get(0)));
//            startActivity(intent);
        }
        if (requestCode==FinalConstants.REQUEST_CODE_CHOOSEMULTIVID&&resultCode==RESULT_OK){
            mSelectedVid=Matisse.obtainResult(data);
            EpMediaUtils epMediaUtils =new EpMediaUtils(this);
            epMediaUtils.setOutputPath(MyApplication.getWorkPath()+OthUtils.createFileName("VIDEO","mp4"));
            epMediaUtils.mergeVideos(mSelectedVid);
        }
    }

    //选择一个视频
    public void selectOneVid(){
        if (mSelectedVid!=null) mSelectedVid.clear();
        Matisse.from(MainActivity.this)
                .choose(MimeType.ofVideo())
                .showSingleMediaType(true)
                .theme(R.style.Matisse_Dracula)
                .showPreview(true)
                .imageEngine(new GlideEngine())
                .forResult(FinalConstants.REQUEST_CODE_CHOOSEONEVID);
    }

}
