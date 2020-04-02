package com.example.vedit.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.vedit.Fragments.Fragment1;
import com.example.vedit.Fragments.Fragment2;
import com.example.vedit.Fragments.Fragment3;
import com.example.vedit.R;
import com.example.vedit.Utils.Item;
import com.example.vedit.Utils.MyAdapter;

import java.util.ArrayList;

public class EditActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private DrawerLayout drawerLayout;
    private ListView list_left_drawer;
    private ArrayList<Item>menuLists;
    private MyAdapter<Item>myAdapter=null;
    Fragment1 fragment1;
    Fragment2 fragment2;
    Fragment3 fragment3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        list_left_drawer=(ListView)findViewById(R.id.list_left_drawer);
        menuLists=new ArrayList<Item>();
        menuLists.add(new Item(R.mipmap.ic_launcher,"Fragment1"));
        menuLists.add(new Item(R.mipmap.ic_launcher,"碎片2"));
        menuLists.add(new Item(R.mipmap.ic_launcher,"Fragment3"));
        myAdapter=new MyAdapter<Item>(menuLists,R.layout.item_list) {
            @Override
            public void bindView(ViewHolder holder, Item obj) {
                holder.setImageResource(R.id.img_icon,obj.getIconId());
                holder.setText(R.id.txt_content,obj.getIconName());
            }
        };
        list_left_drawer.setAdapter(myAdapter);
        list_left_drawer.setOnItemClickListener(this);
        fragment1=new Fragment1();
        //默认显示第一个
        FragmentManager fm=getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_content,fragment1).commit();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentManager fragmentManager=getSupportFragmentManager();
        switch (position){
            case 0:
                if(fragment1==null){
                    fragment1=new Fragment1();
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_content,fragment1).commit();
                break;
            case 1:
                if (fragment2==null){
                    fragment2=new Fragment2();
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_content,fragment2).commit();
                break;
            case 2:
                if (fragment3==null){
                    fragment3=new Fragment3();
                }
                fragmentManager.beginTransaction().replace(R.id.fragment_content,fragment3).commit();
                break;
        }

        Log.i("EditActivity","点击了第"+position+"的碎片");
        drawerLayout.closeDrawer(list_left_drawer);

    }
}
