package com.hustascii.ydfm.fragment;


import android.app.ActionBar;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.hustascii.ydfm.R;
import com.hustascii.ydfm.activity.PlayActivity;
import com.hustascii.ydfm.adapter.HomeAdapter;
import com.hustascii.ydfm.beans.MusicContent;
import com.hustascii.ydfm.beans.MusicContentLite;
import com.hustascii.ydfm.util.FileUtils;
import com.hustascii.ydfm.util.Globles;
import com.hustascii.ydfm.util.NetWorkUtils;
import com.hustascii.ydfm.view.RefreshLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.orhanobut.logger.Logger;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class BaseFragment extends Fragment {
    //TODO: Add Init View when first load data
    protected final static String CACHE_FILE_NAME = "content_json";
    private RefreshLayout swipeLayout;
    private ListView mListView;
    private HomeAdapter homeAdapter;
    private List<MusicContentLite> mList;
    private String url;
    private int channel;
    private ImageLoader mImageLoader;
    private int page;
    protected String mTimeStamp;
    protected String mCacheFileName;//jsons字串缓存文件名称
    private Toolbar toolbar;
    private ActionBar bar;
    protected LinearLayout mLoadFailedTipContainer;
    protected TextView mTv;
    protected ImageView mLoadingFlgImg;
    protected Button mTryAgainBtn;
    protected SharedPreferences mPrefs;
    protected Resources mRes;
    protected boolean mIsFirstLoaded = true;    //true 则执行第一次加载时的操作，false则执行刷新时的操作
    protected String mCacheJsonString;
    private State mCurrentState = State.STATE_INIT;

    /**
     * *
     * 读取文件后台任务
     */
    private ReadCacheTask mReadCacheTask;
    private WriteCacheTask mWriteCacheTask;
    protected boolean mHasLoadedUI = false;//不管以何种方式得到数据，成功的加载UI标记，避免重复开启读缓存和网络请求的任务

    private int num_page = 20;

    //    private Snackbar snackbar;

    enum State {
        STATE_INIT, STATE_OK, STATE_REFRESH_FAILED, STATE_UNKNOWN
    }

    public BaseFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = 1;
        mCacheFileName = CACHE_FILE_NAME;
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
        mListView = (ListView) view.findViewById(R.id.infolist);
        swipeLayout = (RefreshLayout) view.findViewById(R.id.swipe_refresh);


        mImageLoader = ImageLoader.getInstance();
        mListView.setOnScrollListener(new PauseOnScrollListener(mImageLoader, false, false));
        swipeLayout.setColorScheme(android.R.color.holo_red_light, android.R.color.holo_green_light,
                android.R.color.holo_blue_bright, android.R.color.holo_orange_light);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();

            }
        });

        swipeLayout.setOnLoadListener(new RefreshLayout.OnLoadListener() {

            @Override
            public void onLoad() {
                loadMore();
            }
        });

        mTryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                showByState(State.STATE_INIT);
            }
        });
        setListView();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getData();

    }

    private void showByState(State state) {
        mCurrentState = state;
        if (mCurrentState == State.STATE_REFRESH_FAILED) {
            onRefreshLoadFailed();
        } else if (mCurrentState == State.STATE_OK) {
            onLoadSuccess();
        } else if (mCurrentState == State.STATE_INIT) {
            showRefreshAnim();
        }
    }

    //call when data load.
    private void showRefreshAnim() {
        Logger.d("anim called");
        if (swipeLayout.getVisibility() != View.VISIBLE) {
            swipeLayout.setVisibility(View.VISIBLE);
        }
        if (mLoadFailedTipContainer.getVisibility() != View.GONE) {
            mLoadFailedTipContainer.setVisibility(View.GONE);
        }
        swipeLayout.setRefreshing(true);
        swipeLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 2500);
    }

    // call when data load success
    private void onLoadSuccess() {
        if (swipeLayout == null) {
            return;
        }
        mTryAgainBtn.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
        swipeLayout.setRefreshing(false);
        mLoadingFlgImg.setVisibility(View.GONE);
        mTv.setVisibility(View.GONE);
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
        if (mListView.getAdapter() == null || mListView.getAdapter().isEmpty())
            mListView.setAdapter(homeAdapter);
        mCurrentState = State.STATE_INIT;
        super.onResume();
    }

    // init listView with adapter
    private void setListView() {
        mList = new LinkedList<>();
        homeAdapter = new HomeAdapter(getActivity(), mList);
        mListView.setAdapter(homeAdapter);
        //TODO: musicContent is null
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MusicContentLite musicContent = mList.get(i);
                Logger.d("music content" + musicContent);
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                intent.putExtra("map", musicContent);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });

        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

    }

    /**
     * Get data in page 1
     *
     **/
    public void getData() {
//        showRefreshAnim();

        AsyncHttpClient client = new AsyncHttpClient();
        page = 1;
        client.get(getPageUrl(), new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showRefreshAnim();
            }

            @Override
            public void onProgress(int bytesWritten, int totalSize) {
                super.onProgress(bytesWritten, totalSize);
                showRefreshAnim();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {
                if (statusCode == 200) {
                    mList.clear();
                    mList.addAll(parseDoc(new String(responseBody)));
                    homeAdapter.notifyDataSetChanged();
//                    showRefreshAnim();
                } else {
                    Toast.makeText(getActivity(),
                            "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                    if (!mIsFirstLoaded) {
                        showByState(State.STATE_REFRESH_FAILED);
                    } else {
                        swipeLayout.setRefreshing(false);
                    }

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Logger.d(getPageUrl());
                Toast.makeText(getActivity(),
                        "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                if (!mIsFirstLoaded) {
                    showByState(State.STATE_REFRESH_FAILED);
                } else {
                    swipeLayout.setRefreshing(false);
                }

            }
        });
        mIsFirstLoaded = false;

    }

    private void loadMore() {
        AsyncHttpClient client = new AsyncHttpClient();
        page += 1;
        client.get(this.url + String.valueOf(page) + "/", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers,
                                  byte[] responseBody) {

                if (statusCode == 200) {
                    mList.addAll(parseDoc(new String(responseBody)));
                    swipeLayout.setLoading(false);
                    homeAdapter.notifyDataSetChanged();
                    showByState(State.STATE_OK);

                } else {
                    Toast.makeText(getActivity(),
                            "网络访问异常，错误码：" + statusCode, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (mIsFirstLoaded) {
                    mHasLoadedUI = false;
                }
            }
        });
    }

    //show reload UI when refresh failed.
    private void onRefreshLoadFailed() {
        if (swipeLayout == null)
            return;
        //TODO: update failed UI
        swipeLayout.setVisibility(View.GONE);
        mLoadFailedTipContainer.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), mRes.getString(R.string.refresh_failed), Toast.LENGTH_SHORT).show();
    }

    //TODO: What the f**k r this?

    private void onLoadFailed() {
        if (swipeLayout == null)
            return;
        swipeLayout.setVisibility(View.GONE);
        mLoadFailedTipContainer.setVisibility(View.VISIBLE);
        mTv.setText(mRes.getString(R.string.load_failed));
        mLoadingFlgImg.setImageResource(R.drawable.online_load_failed);
        mTryAgainBtn.setVisibility(View.VISIBLE);
    }

    public ArrayList<MusicContentLite> parseDoc(String doc) {
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


    private class ReadCacheTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            //读取缓存文件，显示正在加载...
            onStartLoading();
        }

        @Override
        protected Void doInBackground(Void... params) {
            mCacheJsonString = onLoadResponseStringFromFileCache();//先从文件缓存中拿字串
            mIsFirstLoaded = TextUtils.isEmpty(mCacheJsonString);
            //构建时间戳
            mTimeStamp = getTimeStampFromJson(mCacheJsonString);//取本地时间戳
            if (TextUtils.isEmpty(mTimeStamp)) {
                mTimeStamp = "0";
            }
//            mApiParams = mApiParams.with("timeStamp", mTimeStamp);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (swipeLayout == null)
                return;
            //拿到Json，开始走UI层操作
            if (!NetWorkUtils.isNetworkConnected(getActivity())) {//未联网时先读取文件缓存,如果缓存中没有数据，则提示联网
                Toast.makeText(getActivity(), mRes.getString(R.string.no_network_connected_toast), Toast.LENGTH_SHORT).show();
                if (!mIsFirstLoaded) {
                    //文件缓存中有数据，则直接从缓存中取数据
                    onLoadViewFromCache(mCacheJsonString);//断网情况下，从本地缓存中只加载一次View
                    mHasLoadedUI = true;
                } else {
                    onNetWorkUnConnectedWithNoFileCache();//文件缓存中没有数据，则提示网络出错
                }
            } else {
                if (!mIsFirstLoaded) {//有缓存，直接从缓存中加载View
                    onLoadViewFromCache(mCacheJsonString);
                    mHasLoadedUI = true;
                }
                getData();//不管有没有缓存，均开启网络请求
            }
        }
    }

    private void loadView(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }

        mLoadFailedTipContainer.setVisibility(View.GONE);
        swipeLayout.setVisibility(View.VISIBLE);
        //构建数据，刷新listview
        try {
            mList = generateContentData(s);
            updateRecommendedThemeUi(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    void updateRecommendedThemeUi(int command) {
        /**
         * 参数command
         * 0：初次加载
         * 1：刷新
         * 2：加载更多
         */
        if (mList == null || mList.size() == 0) {
            return;
        }
        if (homeAdapter != null) {
//            pd.dismiss();

            switch (command) {
                case 0:
                case 1:
                    mList.clear();
                    int length = mList.size();
                    for (int i = 0; i < length; i++) {
                        mList.add(mList.get(i));
                    }
                    break;
                case 2:
                    break;
                default:
            }
            homeAdapter.notifyDataSetChanged();
        }
    }


    protected List<MusicContentLite> generateContentData(String s) throws JSONException {
//        LinkedList<MusicContentLite> data = new LinkedList<MusicContentLite>();

//        JSONObject obj = new JSONObject(s);
//        JSONArray array = obj.getJSONArray("data");
        List<MusicContentLite> list = JSON.parseArray(s, MusicContentLite.class);
        return list;
    }


    private void onNetWorkUnConnectedWithNoFileCache() {
        if (swipeLayout == null)
            return;
        swipeLayout.setVisibility(View.GONE);
        mLoadFailedTipContainer.setVisibility(View.VISIBLE);
        mTv.setText(mRes.getString(R.string.load_failed));
        mLoadingFlgImg.setImageResource(R.drawable.online_load_failed);
        mTryAgainBtn.setVisibility(View.VISIBLE);
    }


    protected void onLoadViewFromCache(String s) {
        loadView(s);
    }


    private String getTimeStampFromJson(String json) {
        if (!TextUtils.isEmpty(json)) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                String timeStamp = jsonObject.optString("timeStamp");
                //new interface add timeStamp in data body while odd interface
                //put it outside
                if (TextUtils.isEmpty(timeStamp)) {
                    JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                    if (jsonObject1 != null) {

                        timeStamp = jsonObject1.optString("timeStamp");
                    }
                }
                return timeStamp;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private class WriteCacheTask extends AsyncTask<Void, Void, Void> {
        private String mJson;

        public WriteCacheTask(String s) {
            mJson = s;
        }

        @Override
        protected Void doInBackground(Void... params) {
            onRestoreFileCache(mJson);//网络请求成功，则将json数据更新到硬盘
            return null;
        }
    }


    protected void onRestoreFileCache(String s) {
        FileUtils.writeStringToFileCache(getActivity(), mCacheFileName, s);
    }


    protected String onLoadResponseStringFromFileCache() {
        String s = FileUtils.readStringFromFileCache(getActivity(), mCacheFileName);
        return s;
    }

    protected void onStartLoading() {//第一次加载时的回调
        if (swipeLayout == null)
            return;
        swipeLayout.setVisibility(View.GONE);
        mLoadFailedTipContainer.setVisibility(View.VISIBLE);
        mTv.setText(mRes.getString(R.string.network_loading));//正在加载的页面显示
        mLoadingFlgImg.setImageResource(R.drawable.ic_logo);
        mTryAgainBtn.setVisibility(View.GONE);
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
