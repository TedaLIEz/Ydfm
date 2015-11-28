package com.hustascii.ydfm.fragment;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.hustascii.ydfm.R;
import com.hustascii.ydfm.activity.PlayActivity;
import com.hustascii.ydfm.adapter.HomeAdapter;
import com.hustascii.ydfm.beans.MusicContent;
import com.hustascii.ydfm.beans.MusicContentLite;
import com.hustascii.ydfm.view.RefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class BaseFragment extends Fragment{

    private RefreshLayout swipeLayout;
    private ListView mListView;
    private HomeAdapter homeAdapter;
    private ArrayList<MusicContentLite> mList;
    private String url;
    private int channel;
    private ProgressDialog pd;
    private ImageLoader mImageLoader;
    private int page;

    private Toolbar toolbar;
    private ActionBar bar;
//    private int currentNum = 0;
    private int num_page = 20;
//    private Snackbar snackbar;
    public BaseFragment() {

    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        url = this.getTag();

//        snackbar = Snackbar.with( getActivity() );
//        snackbar.position(Snackbar.SnackbarPosition.TOP );
//        snackbar.text("My text here");
        page = 1;

        pd = new ProgressDialog(this.getActivity());
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("加载中...");
        pd.show();
        getData();
//        loadMoreFromLean();
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
                getData();
//                getDataFromLean();
            }
        });
        swipeLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                loadMore();
//                Toast.makeText(getActivity(), "load", Toast.LENGTH_SHORT).show();
//                loadMoreFromLean();

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

    public int getChannel(){
        return channel;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    private void refresh() {
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(getPageUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                try {
                    if (statusCode == 200) {
//                        Toast.makeText(getActivity(), "数据下载成功!", Toast.LENGTH_SHORT)
//                                .show();
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
                            MusicContentLite musicContent = new MusicContentLite(titles.get(i).text(), authors.get(i).text(), speakers.get(i).text(), times.get(i).text(), clicks.get(i).text(), imgs.get(i).attr("src"), urls.get(i).attr("href").toString());
                            if ((i == 0) && (musicContent == mList.get(i))) {
                                is_change = false;
                                break;
                            } else {
                                mList.add(musicContent);
                            }
                        }
                        Log.v("body", new String(responseBody));
//                        pd.dismiss();
                        swipeLayout.setRefreshing(false);
                        if (!is_change)
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
        mList = new ArrayList<MusicContentLite>();
        homeAdapter = new HomeAdapter(getActivity(),mList);
        mListView.setAdapter(homeAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MusicContentLite musicContent = mList.get(i);
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                intent.putExtra("map", musicContent);
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
//                        Toast.makeText(getActivity(), "数据下载成功!", Toast.LENGTH_SHORT)
//                                .show();
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
                            MusicContentLite musicContent = new MusicContentLite(titles.get(i).text(), authors.get(i).text(), speakers.get(i).text(), times.get(i).text(), clicks.get(i).text(), imgs.get(i).attr("src"), urls.get(i).attr("href").toString());
                            mList.add(musicContent);
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

    private void loadMore() {
        AsyncHttpClient client = new AsyncHttpClient();
        page += 1;
        client.get(this.url + String.valueOf(page) + "/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                try {
                    if (statusCode == 200) {
//                        Toast.makeText(getActivity(), "数据下载成功!", Toast.LENGTH_SHORT)
//                                .show();
                        Document document = Jsoup.parse(new String(responseBody));
                        Elements authors = document.select("div.channel-meta").select("span:has(i.fa-pencil)");
                        Elements speakers = document.select("div.channel-meta").select("span:has(i.fa-microphone)");
                        Elements times = document.select("div.channel-meta").select("span:has(i.fa-clock-o)");
                        Elements clicks = document.select("div.channel-meta").select("span:has(.fa-headphones)");
                        Elements titles = document.select("div.channel-title").select("a");
                        Elements urls = document.select("div.channel-title").select("a");
                        Elements imgs = document.select("div.channel-pic").select("img");
                        for (int i = 0; i < authors.size(); i++) {
                            MusicContentLite musicContent = new MusicContentLite(titles.get(i).text(), authors.get(i).text(), speakers.get(i).text(), times.get(i).text(), clicks.get(i).text(), imgs.get(i).attr("src"), urls.get(i).attr("href").toString());
                            mList.add(musicContent);
                        }
                        pd.dismiss();
//                        SnackbarManager.show(snackbar);
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
//
//
//    private void getDataFromLean(){
//        AVQuery<MusicContent> query = new AVQuery<MusicContent>("MusicContent");
//        query.whereEqualTo("channel", getChannel());
//        query.limit(num_page);
//        query.findInBackground(new FindCallback<MusicContent>() {
//            public void done(List<MusicContent> contents, AVException e) {
//                if (e == null) {
//                    Logger.d("查询成功，收到第" + getChannel() + "频道的" + contents.size() + " 条符合条件的数据");
//                } else {
//                    Logger.d("查询错误: " + e.getMessage());
//                }
//
//                mList.clear();
//                mList.addAll(contents);
//                pd.dismiss();
//                swipeLayout.setRefreshing(false);
//                homeAdapter.notifyDataSetChanged();
//            }
//        });
//    }
//
//    private void loadMoreFromLean(){
//        AVQuery<MusicContent> query = new AVQuery<MusicContent>("MusicContent");
//        query.whereEqualTo("channel", getChannel());
//        query.skip(mList.size());
//        query.limit(num_page);
//        query.findInBackground(new FindCallback<MusicContent>() {
//            public void done(List<MusicContent> contents, AVException e) {
//                if (e == null) {
//                    Logger.d("查询成功，收到第"+getChannel()+"频道的" + contents.size() + " 条符合条件的数据");
//                } else {
//                    Logger.d("查询错误: " + e.getMessage());
//                }
//                mList.addAll(contents);
//                pd.dismiss();
//                swipeLayout.setRefreshing(false);
//                homeAdapter.notifyDataSetChanged();
//            }
//        });
//    }






}
