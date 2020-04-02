package com.example.vedit.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vedit.R;
import com.libq.videocutpreview.VideoCutView;

public class Fragment1 extends Fragment {
    private TextView tv_fg1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_fragment1, container, false);
        tv_fg1=(TextView)view.findViewById(R.id.tv_fg1);

        tv_fg1.setText("视频裁剪预览");
        return view;
    }
}
