package com.hustascii.ydfm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewGroupCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.hustascii.ydfm.activity.SearchActivity;
import com.hustascii.ydfm.fragment.BaseFragment;
import com.hustascii.ydfm.fragment.SearchResultFragment;
import com.hustascii.ydfm.fragment.SettingFragment;
import com.hustascii.ydfm.util.FontHelper;
import com.hustascii.ydfm.util.Globles;
import com.umeng.analytics.MobclickAgent;
import com.umeng.update.UmengUpdateAgent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import it.neokree.materialnavigationdrawer.MaterialAccount;
import it.neokree.materialnavigationdrawer.MaterialAccountListener;
import it.neokree.materialnavigationdrawer.MaterialNavigationDrawer;
import it.neokree.materialnavigationdrawer.MaterialSection;
import it.neokree.materialnavigationdrawer.MaterialSectionListener;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends MaterialNavigationDrawer {
    MaterialSection section1, section2,section3,section4,section5,section6, collectSection,settingsSection;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private static Boolean isExit = false;
    @Override
    public void init(Bundle savedInstanceState) {

        AVAnalytics.trackAppOpened(getIntent());
//        UmengUpdateAgent.setUpdateOnlyWifi(false);
//        UmengUpdateAgent.update(this);

        View view = LayoutInflater.from(this).inflate(R.layout.custom_drawer,null);
        setDrawerHeaderCustom(view);
        // create sections
        section1 = this.newSection("悦读",getFragmentInstance(Globles.BASE_URL + "channel/1/"));
        section2 = this.newSection("情感",getFragmentInstance(Globles.BASE_URL + "channel/2/"));
        section3 = this.newSection("连播",getFragmentInstance(Globles.BASE_URL + "channel/3/"));
        section4 = this.newSection("校园",getFragmentInstance(Globles.BASE_URL + "channel/4/"));
        section5 = this.newSection("音乐",getFragmentInstance(Globles.BASE_URL + "channel/5/"));
        section6 = this.newSection("Labs",getFragmentInstance(Globles.BASE_URL + "channel/6/"));
        this.addDivisor();
        collectSection= this.newSection("收藏",getFragmentInstance(Globles.BASE_URL + "channel/6/"));
        settingsSection = this.newSection("设置",new SettingFragment());



        // add your sections to the drawer
        this.addSection(section1);
        this.addSection(section2);
        this.addSection(section3);
        this.addSection(section4);
        this.addSection(section5);
        this.addSection(section6);
        this.addDivisor();
        this.addSection(collectSection);
        this.addSection(settingsSection);

        this.allowArrowAnimation();


        int red = Color.parseColor("#F36B63");
        section1.setSectionColor(red,red);
        section2.setSectionColor(red,red);
        section3.setSectionColor(red,red);
        section4.setSectionColor(red,red);
        section5.setSectionColor(red,red);
        section6.setSectionColor(red,red);
        collectSection.setSectionColor(red, red);
        settingsSection.setSectionColor(red,red);
        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_BACK_TO_FIRST);
        getToolbar().setTitle("悦读");
    }


    @Override

    protected void attachBaseContext(Context newBase) {

        // TODO Auto-generated method stub

        super.attachBaseContext(new CalligraphyContextWrapper(newBase));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.action_search){
            Intent intent=new Intent();
            intent.setClass(MainActivity.this, SearchActivity.class);
            startActivityForResult(intent, 1000);
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out);
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        new BaseFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode){
            case 1000:
                Bundle b = data.getExtras();
                String str = b.getString("key");
                Log.v("Key",str);
                MaterialSection section = getCurrentSection();
                section.setTitle("搜索");
//                this.newSection("搜索",getFragmentInstance(Globles.BASE_URL + "search/" + str + "/"));
                BaseFragment fragment = getFragmentInstance(Globles.BASE_URL + "search/" + str + "/");
                fragmentTransaction.commit();
                break;
            default:
                break;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    private BaseFragment getFragmentInstance(String url){
        if(fragmentManager.findFragmentByTag(url)==null){
            BaseFragment fragment = new BaseFragment();
            fragment.setUrl(url);
            fragmentTransaction.add(fragment,url);
            return fragment;
        }else{
            return (BaseFragment)fragmentManager.findFragmentByTag(url);
        }

    }
    public void AboutUs(View view){
           Toast.makeText(MainActivity.this,"Click",Toast.LENGTH_SHORT).show();
    }
    public void ClearMem(View view){
        Toast.makeText(MainActivity.this,"Click",Toast.LENGTH_SHORT).show();
    }
    // always exit at section1
    @Override
    public void onBackPressed() {
        if (isDrawn()) {
            closeDrawer();
        } else {
            if (getCurrentSection() == section1) {
                exitBy2Click();
            } else {
                super.onBackPressed();
            }
        }
    }

    /**
     * 双击退出函数
     */


    private void exitBy2Click() {
        Timer tExit = null;
        if (!isExit) {
            isExit = true; // 准备退出
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            finish();
            MobclickAgent.onKillProcess(this);
            System.exit(0);
        }
    }
}
