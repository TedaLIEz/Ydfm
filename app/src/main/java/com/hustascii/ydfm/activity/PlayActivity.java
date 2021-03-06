package com.hustascii.ydfm.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.beans.MusicContent;
import com.hustascii.ydfm.beans.MusicContentLite;
import com.hustascii.ydfm.util.AnimateFirstDisplayListener;
import com.hustascii.ydfm.util.Crawls;
import com.hustascii.ydfm.util.Globles;
import com.hustascii.ydfm.util.MusicPlayer;
import com.hustascii.ydfm.view.MySwipeBackActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.orhanobut.logger.Logger;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.apache.http.Header;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;


public class PlayActivity extends MySwipeBackActivity {

    private static final int VIBRATE_DURATION = 20;
    private boolean isLike;
    private SwipeBackLayout mSwipeBackLayout;
    private int primaryColor;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private TextView statusBar;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private MusicContentLite musicContent;
    private Intent intent;
    private DiscreteSeekBar myBar;
    private CircleButton myBtn;
    private int status;//播放状态
    private MusicPlayer player;
    private String contentUrl;
    private String musicUrl;
    public TextView mTitle;
    public CircleImageView mImg;
    public TextView mAuthor;
    public TextView mSpeaker;
    public TextView mTimer;
    public TextView mListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        isLike=true;
        Log.i("build", "" + Build.VERSION.SDK_INT);
        statusBar = (TextView) findViewById(R.id.statusBar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Play");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_36dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            statusBar = (TextView) findViewById(R.id.statusBar);
            Log.v("height", String.valueOf(Globles.getStatusBarHeight(this)));
            statusBar.setHeight(Globles.getStatusBarHeight(this));
            statusBar.setBackgroundColor(Color.parseColor("#F36B63"));

        }else{
            statusBar.setHeight(0);

        }
        setSupportActionBar(toolbar);
        mTitle = (TextView) findViewById(R.id.title);
        mAuthor = (TextView) findViewById(R.id.author);
        mSpeaker = (TextView) findViewById(R.id.speaker);
        mTimer = (TextView) findViewById(R.id.time);
        mListen = (TextView) findViewById(R.id.listen);
        mImg = (CircleImageView) findViewById(R.id.myimg);
        myBar = (DiscreteSeekBar) findViewById(R.id.myseekbar);
        myBtn = (CircleButton) findViewById(R.id.mybtn);
        status = 0;
        player = MusicPlayer.getInstance(myBar);

        handleIntent();


        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!player.isplay()) {
                    myBtn.setImageResource(R.drawable.ic_stop_fm);
                    if (!musicUrl.equals(player.getUrl())) {
                        player.setUrl(musicUrl);
                        player.prepare();
                    } else {
                        //player.setUrl(musicUrl);
                        player.play();
                    }
                } else {
                    if (musicUrl.equals(player.getUrl())) {
                        myBtn.setImageResource(R.drawable.ic_play_fm);
                        player.pause();
                    } else {
                        myBtn.setImageResource(R.drawable.ic_stop_fm);
                        player.setUrl(musicUrl);
                        player.prepare();
//                        player.play();
                    }

                }
            }
        });


        myBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                if (fromUser) {
                    int v = value * player.mediaPlayer.getDuration() / seekBar.getMax();
                    player.mediaPlayer.seekTo(v);
                }

            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }

        });


        mTitle.setText(musicContent.getTitle());
        mAuthor.setText(musicContent.getAuthor());
        mSpeaker.setText(musicContent.getSpeaker());
        mTimer.setText(musicContent.getTime());
        mListen.setText(musicContent.getListen());
        contentUrl = musicContent.getContentUrl();
        this.mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.example_1) //设置图片在下载期间显示的图片
                .showImageForEmptyUri(R.drawable.example_1)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.drawable.example_1)  //设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisc(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)  //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型//
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成
        mImageLoader.displayImage(Globles.BASE_URL + musicContent.getImgUrl().substring(1), mImg, options, new AnimateFirstDisplayListener());

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.addSwipeListener(new SwipeBackLayout.SwipeListener() {
            @Override
            public void onScrollStateChange(int state, float scrollPercent) {

            }

            @Override
            public void onEdgeTouch(int edgeFlag) {
                vibrate(VIBRATE_DURATION);
            }

            @Override
            public void onScrollOverThreshold() {
                vibrate(VIBRATE_DURATION);
            }
        });
        findViewById(R.id.article).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contentUrl == null && contentUrl.isEmpty()) {
                    Toast.makeText(PlayActivity.this, "url为空", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(PlayActivity.this, ArticleActivity.class);
                    i.putExtra("url", contentUrl);
                    startActivity(i);
                }
            }
        });
    }

    private void handleIntent() {
        intent = getIntent();
        musicContent = (MusicContentLite) intent.getSerializableExtra("map");
        if (Globles.DEBUG) {
            assert musicContent != null;
            Logger.d(musicContent.toString());
        }
        contentUrl = musicContent.getContentUrl();
        if (musicUrl == null || musicUrl.equals("")) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(Globles.BASE_URL + contentUrl.substring(1), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    super.onSuccess(statusCode, headers, responseBody);
                    Log.v("result", new String(responseBody));
                    musicUrl = Globles.BASE_URL + Crawls.getMusicUrl(new String(responseBody)).substring(1);
                    Log.v("musicUrl", musicUrl);

                    if (musicUrl == null || musicUrl.equals("")) {
                        Toast.makeText(getApplicationContext(), "网页解析错误", Toast.LENGTH_SHORT).show();
                    } else {


                        if(player.isplay()){
                            if(musicUrl.equals(player.getUrl())) {
                                myBtn.setImageResource(R.drawable.ic_stop_fm);
                                myBar.setProgress(player.getpos());
                            }
                        }else{
                            if(musicUrl.equals(player.getUrl())){
                                myBar.setProgress(player.getpos());
                            }else {

                                //player.prepare();
                            }
                        }

                    }
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
        if(isLike){
            Drawable drawable=getResources().getDrawable(R.drawable.ic_heart_press_fm);
            drawable.setAlpha(100);
            menu.findItem(R.id.action_love).setIcon(drawable);

        }else{
            menu.findItem(R.id.action_love).setIcon(R.drawable.ic_heart_normal_fm);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar musicContent clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();
        switch (id){
            case R.id.action_love:
                if(isLike){
                    isLike=false;
                    item.setIcon(R.drawable.ic_heart_normal_fm);
                }else{
                    isLike=true;
                    Drawable drawable=getResources().getDrawable(R.drawable.ic_heart_press_fm);
                    drawable.setAlpha(100);
                    item.setIcon(drawable);
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }


    private void vibrate(long duration) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {
                0, duration
        };
        vibrator.vibrate(pattern, -1);
    }


    private final class UIHandler extends Handler {
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case PROCESSING: // 更新进度
//                    progressBar.setProgress(msg.getData().getInt("size"));
//                    float num = (float) progressBar.getProgress()
//                            / (float) progressBar.getMax();
//                    int result = (int) (num * 100); // 计算进度
//                    resultView.setText(result + "%");
//                    if (progressBar.getProgress() == progressBar.getMax()) { // 下载完成
//                        Toast.makeText(getApplicationContext(), R.string.success,
//                                Toast.LENGTH_LONG).show();
//                    }
//                    break;
//                case FAILURE: // 下载失败
//                    Toast.makeText(getApplicationContext(), R.string.error,
//                            Toast.LENGTH_LONG).show();
//                    break;
            }
        }
    }

}
