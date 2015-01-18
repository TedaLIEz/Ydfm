package com.hustascii.ydfm.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.beans.Item;
import com.hustascii.ydfm.util.AnimateFirstDisplayListener;
import com.hustascii.ydfm.util.Crawls;
import com.hustascii.ydfm.util.Globles;
import com.hustascii.ydfm.util.MusicPlayer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.apache.http.Header;

import at.markushi.ui.CircleButton;
import de.hdodenhof.circleimageview.CircleImageView;
import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.imid.swipebacklayout.lib.app.SwipeBackActivity;


public class PlayActivity extends SwipeBackActivity {

    private static final int VIBRATE_DURATION = 20;

    private SwipeBackLayout mSwipeBackLayout;
    private int primaryColor;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private TextView statusBar;
    private Toolbar toolbar;
    private ActionBar actionBar;
    private Item item;
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

        intent = getIntent();
        item = (Item) intent.getSerializableExtra("map");
        statusBar = (TextView) findViewById(R.id.statusBar);
        Log.v("height", String.valueOf(Globles.getStatusBarHeight(this)));
        statusBar.setHeight(Globles.getStatusBarHeight(this));
        statusBar.setBackgroundColor(Color.parseColor("#F36B63"));
        toolbar = (Toolbar) findViewById(R.id.toolbar);

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

        contentUrl = item.getContentUrl();
        if (musicUrl == null || musicUrl.equals("")) {
            AsyncHttpClient client = new AsyncHttpClient();
            client.get(Globles.BASE_URL + contentUrl.substring(1), new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    super.onSuccess(statusCode, headers, responseBody);
                    musicUrl = Globles.BASE_URL + Crawls.getMusicUrl(new String(responseBody)).substring(1);
                    Log.v("musicUrl", musicUrl);

                    if (musicUrl == null || musicUrl.equals("")) {
                        Toast.makeText(getApplicationContext(), "网页解析错误", Toast.LENGTH_SHORT).show();
                    } else {

                        if (musicUrl.equals(player.getUrl())) {
                            Log.v("status", "continue");
                            if(player.isplay())
                                myBtn.setImageResource(R.drawable.ic_stop_fm);

                        } else {
                            player.setUrl(musicUrl);
                            player.prepare();

                        }
                    }
                }
            });
        }


        myBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (status == 0) {
                    status = 1;
                    myBtn.setImageResource(R.drawable.ic_stop_fm);
                    player.play();
                } else {
                    status = 0;
                    myBtn.setImageResource(R.drawable.ic_play_fm);
                    player.pause();

                }
            }
        });


        myBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                int v = value * player.mediaPlayer.getDuration() / seekBar.getMax();
                player.mediaPlayer.seekTo(v);
            }

        });


        mTitle.setText(item.getTitle());
        mAuthor.setText(item.getAuthor());
        mSpeaker.setText(item.getSpeaker());
        mTimer.setText(item.getTime());
        mListen.setText(item.getListen());
        contentUrl = item.getContentUrl();
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
        mImageLoader.displayImage(Globles.BASE_URL + item.getImgUrl().substring(1), mImg, options, new AnimateFirstDisplayListener());

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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void vibrate(long duration) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {
                0, duration
        };
        vibrator.vibrate(pattern, -1);
    }

    public void takeArticle(View ivew) {
        if (contentUrl == null && contentUrl.isEmpty()) {
            Toast.makeText(this, "url为空", Toast.LENGTH_SHORT).show();
        } else {
            Intent i = new Intent(PlayActivity.this, ArticleActivity.class);
            i.putExtra("url", contentUrl);
            startActivity(i);
        }

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
