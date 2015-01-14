package com.hustascii.ydfm.fragment;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.adapter.HomeAdapter;
import com.hustascii.ydfm.beans.Item;
import com.hustascii.ydfm.util.Globles;

import java.util.ArrayList;
import java.util.HashMap;


public class HomeFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        super.onCreateView(inflater, container,savedInstanceState);
//        return super.setView(inflater, super.getData(Globles.BASE_URL+"channel/1/"));
//    }


}
