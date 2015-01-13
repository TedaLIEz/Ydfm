package com.hustascii.ydfm.fragment;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.adapter.HomeAdapter;
import com.hustascii.ydfm.beans.Item;
import com.hustascii.ydfm.util.DataSpider;

import java.util.ArrayList;


public abstract class BaseFragment extends Fragment {
    private ListView mListView;
    private HomeAdapter homeAdapter;
    private ArrayList<Item> mList;
    private String url;

    protected View setView(LayoutInflater inflater,ArrayList<Item> list){
        View view = inflater.inflate(R.layout.activity_main, null, false);
        mListView = (ListView)view.findViewById(R.id.infolist);


        mList = new ArrayList<Item>();

        for(int i=0;i<6;i++){
            Item item = new Item("林清晓","雨夜书筒","9:14","39次");
            mList.add(item);
        }

        homeAdapter = new HomeAdapter(getActivity(),mList);
        mListView.setAdapter(homeAdapter);
        return view;
    }

    protected ArrayList<Item> getData(String url){
        DataSpider spider = new DataSpider(getActivity(),url);
        return spider.getData();
    }
}
