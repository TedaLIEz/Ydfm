package com.hustascii.ydfm.fragment;


import android.app.ActionBar;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.hustascii.ydfm.R;
import com.hustascii.ydfm.activity.PlayActivity;
import com.hustascii.ydfm.adapter.MusicContentLiteAdapter;
import com.hustascii.ydfm.beans.MusicContentLite;
import com.hustascii.ydfm.service.DataThread;
import com.hustascii.ydfm.util.FileUtils;
import com.hustascii.ydfm.util.NetWorkUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;



public class BaseFragment extends Fragment {
    private List<MusicContentLite> mList;
    private UltimateRecyclerView ultimateRecyclerView;
    private String url;
    private int channel;
    private MusicContentLiteAdapter musicContentLiteAdapter;
    private int page;
    protected String mTimeStamp;
    private Toolbar toolbar;
    private ActionBar bar;
    protected LinearLayout mLoadFailedTipContainer;
    protected TextView mTv;
    protected ImageView mLoadingFlgImg;
    protected Button mTryAgainBtn;
    protected Resources mRes;
    private State mCurrentState = State.STATE_INIT;

    private DataThread<List<MusicContentLite>> mDataThread;
    private int num_page = 20;





    enum State {
        STATE_INIT, STATE_OK, STATE_REFRESH_FAILED
    }

    public BaseFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 1;
        mList = new ArrayList<>();
        musicContentLiteAdapter = new MusicContentLiteAdapter(mList);

        mDataThread = new DataThread<List<MusicContentLite>>(new Handler()) {

            @Override
            public void onReadSuccess(List<MusicContentLite> datas) {
                Logger.d("success read cache\n" + datas.toString());
                loadView(datas);
            }

            @Override
            public void onCacheSuccess() {
//                mPrefs.edit().putBoolean("cached", true).commit();
            }

            @Override
            public void cacheData(List<MusicContentLite> data) {
                String str = JSON.toJSONString(data);
                FileUtils.writeStringToFileCache(getActivity(), FileUtils.replaceSlash(getPageUrl()), str);
            }

            @Override
            public List<MusicContentLite> readData(String tag) {
                String str = FileUtils.readStringFromFileCache(getActivity(), tag);
                List<MusicContentLite> datas = JSONArray.parseArray(str, MusicContentLite.class);
                return datas;
            }


        };
        mDataThread.start();
        mDataThread.prepareHandler();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        bar = getActivity().getActionBar();
        View view = inflater.inflate(R.layout.activity_main, container, false);
        mRes = getActivity().getResources();
        mLoadFailedTipContainer = (LinearLayout) view.findViewById(R.id.load_failed_container);

        mTv = (TextView) view.findViewById(R.id.tv_tip);
        mLoadingFlgImg = (ImageView) view.findViewById(R.id.img_load_flg);
        mTryAgainBtn = (Button) view.findViewById(R.id.btn_retry);
        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        ultimateRecyclerView = (UltimateRecyclerView) view.findViewById(R.id.urv_infolist);
        initRecyclerView();
        mTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecyclerView();
                getData();
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        mPrefs.edit().putBoolean("cached", false).apply();
        initData();
    }

    private void initRecyclerView() {
        ultimateRecyclerView.enableLoadmore();
        ultimateRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        musicContentLiteAdapter.setCustomLoadMoreView(LayoutInflater.from(getActivity()).inflate(R.layout.custom_bottom_progressbar, null));
        ultimateRecyclerView.setAdapter(musicContentLiteAdapter);
        ultimateRecyclerView.setItemAnimator(null);
        musicContentLiteAdapter.setContentClickListener(new MusicContentLiteAdapter.ContentClickListener() {

            @Override
            public void onClick(MusicContentLite musicContentLite) {
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                intent.putExtra("map", musicContentLite);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

            }
        });
        ultimateRecyclerView.setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                refresh();
            }
        });
        ultimateRecyclerView.setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, int maxLastVisiblePosition) {
                //TODO:Append to cache when loadMore
                loadMoreData();
            }

        });


    }


    private void showByState(State state) {
        mCurrentState = state;
        if (mCurrentState == State.STATE_REFRESH_FAILED) {
            showError();
        } else if (mCurrentState == State.STATE_OK) {
            showRecyclerView();
        }
    }


    // call when pull on refresh
    private void refresh() {
        AsyncHttpClient client = new AsyncHttpClient();
        page = 1;
        client.get(getPageUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                if (statusCode == 200) {
                    onDataLoadSuccess(new String(responseBody));
                } else {
                    Toast.makeText(getActivity(),
                            "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.d(getPageUrl());
                Toast.makeText(getActivity(),
                        "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();

            }
        });
        ultimateRecyclerView.setRefreshing(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDataThread.quit();
    }

    public String getUrl() {
        return url;
    }

    public String getPageUrl() {
        return this.url + String.valueOf(page) + "/";
    }

    public int getChannel() {
        return channel;
    }

    public void setUrl(String url) {
        this.url = url;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mCurrentState == State.STATE_REFRESH_FAILED && NetWorkUtils.isNetworkConnected(getActivity())) {
            initData();
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.i("lifecycle", "onStop");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("lifecycle", "onPause");
        if (mCurrentState == State.STATE_REFRESH_FAILED && NetWorkUtils.isNetworkConnected(getActivity())) {
            initData();
        }
    }


    private void initData() {
        String filePath = FileUtils.replaceSlash(getPageUrl());
        boolean exist = FileUtils.isCacheExists(getActivity(), filePath);
        if (!exist) {
            if (NetWorkUtils.checkNetworkStatus(getActivity()) >= 0) {
                getData();
            } else {
                Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();
                showByState(State.STATE_REFRESH_FAILED);
            }
        } else {
            mDataThread.readCache(filePath);
            if (NetWorkUtils.checkNetworkStatus(getActivity()) >= 0) {
                getData();
            } else {
                Toast.makeText(getActivity(), "网络连接错误", Toast.LENGTH_SHORT).show();

            }
        }

    }
    /**
     * Get data in page 1
     *
     **/
    private void getData() {

        AsyncHttpClient client = new AsyncHttpClient();
        page = 1;
        client.get(getPageUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showAnim();
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                if (statusCode == 200) {
                    onDataLoadSuccess(new String(responseBody));
                } else {
                    Toast.makeText(getActivity(),
                            "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                    showByState(State.STATE_REFRESH_FAILED);

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.d(getPageUrl());
                Toast.makeText(getActivity(),
                        "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                showByState(State.STATE_REFRESH_FAILED);


            }
        });
    }

    private void onDataLoadSuccess(String responseBody) {
        showByState(State.STATE_OK);
        mList.clear();
        mList.addAll(parseDoc(responseBody));
        musicContentLiteAdapter.notifyItemChanged(0);
        mDataThread.queueCache(FileUtils.replaceSlash(getPageUrl()), parseDoc(responseBody));
    }


    private void showRecyclerView() {
        if (ultimateRecyclerView == null) {
            return;
        }
        if (ultimateRecyclerView.getVisibility() != View.VISIBLE) {
            ultimateRecyclerView.setVisibility(View.VISIBLE);
        }
        if (mLoadFailedTipContainer.getVisibility() != View.GONE) {
            mLoadFailedTipContainer.setVisibility(View.GONE);
        }
    }

    private void showAnim() {
        ultimateRecyclerView.setRefreshing(true);
        ultimateRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                ultimateRecyclerView.setRefreshing(false);
            }
        }, 2400);
    }

    private void loadMoreData() {
        AsyncHttpClient client = new AsyncHttpClient();
        page += 1;
        client.get(this.url + String.valueOf(page) + "/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {

                if (statusCode == 200) {
                    mList.addAll(parseDoc(new String(responseBody)));
                    musicContentLiteAdapter.notifyDataSetChanged();
                    showByState(State.STATE_OK);
                } else {
                    Toast.makeText(getActivity(),
                            "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),
                        "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showError() {
        if (ultimateRecyclerView == null)
            return;
        ultimateRecyclerView.setVisibility(View.GONE);
        mLoadFailedTipContainer.setVisibility(View.VISIBLE);
        mTv.setText(mRes.getString(R.string.load_failed));
        mLoadingFlgImg.setImageResource(R.drawable.online_load_failed);
        mTryAgainBtn.setVisibility(View.VISIBLE);
    }

    public ArrayList<MusicContentLite> parseDoc(@NonNull String doc) {
        ArrayList<MusicContentLite> list = new ArrayList<>();
        Document document = Jsoup.parse(doc);
        Elements authors = document.select("div.channel-meta").select("span:has(i.fa-pencil)");
        Elements speakers = document.select("div.channel-meta").select("span:has(i.fa-microphone)");
        Elements times = document.select("div.channel-meta").select("span:has(i.fa-clock-o)");
        Elements clicks = document.select("div.channel-meta").select("span:has(.fa-headphones)");
        Elements titles = document.select("div.channel-title").select("a");
        Elements urls = document.select("div.channel-title").select("a");
        Elements imgs = document.select("div.channel-pic").select("img");
        for (int i = 0; i < authors.size(); i++) {
            MusicContentLite musicContent = new MusicContentLite(titles.get(i).text(), authors.get(i).text(), speakers.get(i).text(), times.get(i).text(), clicks.get(i).text(), imgs.get(i).attr("src"), urls.get(i).attr("href").toString());
            list.add(musicContent);
        }

        return list;
    }



    private void loadView(List<MusicContentLite> datas) {
        if (datas == null) return;

        mLoadFailedTipContainer.setVisibility(View.GONE);
        ultimateRecyclerView.setVisibility(View.VISIBLE);
        //构建数据，刷新listview
        try {
            mList.clear();
            mList.addAll(datas);
            musicContentLiteAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }





//    private String getTimeStampFromJson(String json) {
//        if (!TextUtils.isEmpty(json)) {
//            try {
//                JSONObject jsonObject = new JSONObject(json);
//                String timeStamp = jsonObject.optString("timeStamp");
//                //new interface add timeStamp in data body while odd interface
//                //put it outside
//                if (TextUtils.isEmpty(timeStamp)) {
//                    JSONObject jsonObject1 = jsonObject.optJSONObject("data");
//                    if (jsonObject1 != null) {
//
//                        timeStamp = jsonObject1.optString("timeStamp");
//                    }
//                }
//                return timeStamp;
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }




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
