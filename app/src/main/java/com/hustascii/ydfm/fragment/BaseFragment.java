package com.hustascii.ydfm.fragment;


import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.activity.PlayActivity;
import com.hustascii.ydfm.adapter.HomeAdapter;
import com.hustascii.ydfm.beans.Item;
import com.hustascii.ydfm.view.RefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class BaseFragment extends Fragment{

    private RefreshLayout swipeLayout;
    private ListView mListView;
    private HomeAdapter homeAdapter;
    private ArrayList<Item> mList;
    private String url;
    private ProgressDialog pd;
    private ImageLoader mImageLoader;
    private int page;
    private Toolbar toolbar;
    private ActionBar bar;
    public BaseFragment() {

    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        url = this.getTag();

        page = 1;

        pd = new ProgressDialog(this.getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("加载中...");
        pd.show();
        getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        bar=getActivity().getActionBar();
        View view = inflater.inflate(R.layout.activity_main, null, false);
        toolbar=(Toolbar)view.findViewById(R.id.toolbar);


        mListView = (ListView) view.findViewById(R.id.infolist);
        swipeLayout = (RefreshLayout) view.findViewById(R.id.swipe_refresh);
        mImageLoader = ImageLoader.getInstance();
        mListView.setOnScrollListener(new PauseOnScrollListener(mImageLoader,false, false));

        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);


        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                getData();
                refresh();
            }
        });
        swipeLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {

                Toast.makeText(getActivity(), "load", Toast.LENGTH_SHORT).show();
                LoadMore();

            }
        });
        setView();
        return view;
    }



    public String getUrl() {
        return url;
    }

    public String getPageUrl(){
        return this.url+String.valueOf(page)+"/";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private void refresh(){
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getPageUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                try {
                    if (statusCode == 200) {
                        Toast.makeText(getActivity(), "数据下载成功!", Toast.LENGTH_SHORT)
                                .show();
                        Boolean is_change = true;
                        Document document = Jsoup.parse(new String(responseBody));
                        Elements authors = document.select("div.channel-meta").select("span:has(i.fa-pencil)");
                        Elements speakers = document.select("div.channel-meta").select("span:has(i.fa-microphone)");
                        Elements times = document.select("div.channel-meta").select("span:has(i.fa-clock-o)");
                        Elements clicks = document.select("div.channel-meta").select("span:has(.fa-headphones)");
                        Elements titles = document.select("div.channel-title").select("a");
                        Elements urls = document.select("div.channel-title").select("a");
                        Elements imgs = document.select("div.channel-pic").select("img");
                        for (int i = 0; i < authors.size(); i++) {
                            Item item = new Item(titles.get(i).text(), authors.get(i).text(), speakers.get(i).text(), times.get(i).text(), clicks.get(i).text(), imgs.get(i).attr("src"), urls.get(i).attr("href").toString());
                            if((i==0)&&(item == mList.get(i))) {
                                is_change = false;
                                break;
                            }
                            else {
                                mList.add(item);
                            }
                        }
                        Log.v("body", new String(responseBody));
//                        pd.dismiss();
                        swipeLayout.setRefreshing(false);
                        if(!is_change)
                            homeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(),
                                "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    private void setView(){
        mList = new ArrayList<Item>();
        homeAdapter = new HomeAdapter(getActivity(),mList);
        mListView.setAdapter(homeAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Item item = mList.get(i);
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                intent.putExtra("map",item);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
    }




    public void getData() {
        AsyncHttpClient client = new AsyncHttpClient();
        page = 1;
        client.get(getPageUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                try {

                    if (statusCode == 200) {
                        Toast.makeText(getActivity(), "数据下载成功!", Toast.LENGTH_SHORT)
                                .show();
                        Document document = Jsoup.parse(new String(responseBody));
                        Elements authors = document.select("div.channel-meta").select("span:has(i.fa-pencil)");
                        Elements speakers = document.select("div.channel-meta").select("span:has(i.fa-microphone)");
                        Elements times = document.select("div.channel-meta").select("span:has(i.fa-clock-o)");
                        Elements clicks = document.select("div.channel-meta").select("span:has(.fa-headphones)");
                        Elements titles = document.select("div.channel-title").select("a");
                        Elements urls = document.select("div.channel-title").select("a");
                        Elements imgs = document.select("div.channel-pic").select("img");
                        mList.clear();
                        for (int i = 0; i < authors.size(); i++) {
                            Item item = new Item(titles.get(i).text(), authors.get(i).text(), speakers.get(i).text(), times.get(i).text(), clicks.get(i).text(), imgs.get(i).attr("src"), urls.get(i).attr("href").toString());
                            mList.add(item);
                        }
                        Log.v("body", new String(responseBody));
                        pd.dismiss();
                        swipeLayout.setRefreshing(false);
                        homeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(),
                                "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });


    }

    private void LoadMore(){
        AsyncHttpClient client = new AsyncHttpClient();
        page+=1;
        client.get(this.url+String.valueOf(page)+"/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                try {
                    if (statusCode == 200) {
                        Toast.makeText(getActivity(), "数据下载成功!", Toast.LENGTH_SHORT)
                                .show();
                        Document document = Jsoup.parse(new String(responseBody));
                        Elements authors = document.select("div.channel-meta").select("span:has(i.fa-pencil)");
                        Elements speakers = document.select("div.channel-meta").select("span:has(i.fa-microphone)");
                        Elements times = document.select("div.channel-meta").select("span:has(i.fa-clock-o)");
                        Elements clicks = document.select("div.channel-meta").select("span:has(.fa-headphones)");
                        Elements titles = document.select("div.channel-title").select("a");
                        Elements urls = document.select("div.channel-title").select("a");
                        Elements imgs = document.select("div.channel-pic").select("img");
                        for (int i = 0; i < authors.size(); i++) {
                            Item item = new Item(titles.get(i).text(), authors.get(i).text(), speakers.get(i).text(), times.get(i).text(), clicks.get(i).text(), imgs.get(i).attr("src"), urls.get(i).attr("href").toString());
                            mList.add(item);
                        }
                        pd.dismiss();
                        swipeLayout.setLoading(false);
                        homeAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(getActivity(),
                                "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

    }


}
