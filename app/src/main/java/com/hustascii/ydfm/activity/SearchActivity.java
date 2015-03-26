package com.hustascii.ydfm.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Vibrator;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.hustascii.ydfm.MainActivity;
import com.hustascii.ydfm.R;
import com.hustascii.ydfm.util.Globles;
import com.hustascii.ydfm.view.MySwipeBackActivity;

import java.util.zip.Inflater;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class SearchActivity extends MySwipeBackActivity {
    private static final int VIBRATE_DURATION = 20;
    private TextView statusBar;
    private ActionBar actionBar;
    private Toolbar toolbar;
    private ListView his_lv;
    private SwipeBackLayout mSwipeBackLayout;
    private android.support.v7.widget.SearchView searchView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        statusBar = (TextView) findViewById(R.id.statusBar);
        toolbar=(Toolbar)findViewById(R.id.search_toolbar);
        his_lv=(ListView)findViewById(R.id.his_list);
        toolbar.setNavigationIcon(R.drawable.ic_back_fm);
        toolbar.setTitle("搜索");
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
        getMenuInflater().inflate(R.menu.menu_search, menu);
        searchView = (android.support.v7.widget.SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setIconified(false);
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint("查找");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(SearchActivity.this, MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("key",s);
                intent.putExtras(bundle);
                setResult(10000,intent);
                finish();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            finish();
        }
        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    private void vibrate(long duration) {
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {
                0, duration
        };
        vibrator.vibrate(pattern, -1);
    }
}
