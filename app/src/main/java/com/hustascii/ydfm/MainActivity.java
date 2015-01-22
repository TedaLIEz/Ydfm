package com.hustascii.ydfm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Toast;

import com.hustascii.ydfm.activity.SearchActivity;
import com.hustascii.ydfm.fragment.BaseFragment;
import com.hustascii.ydfm.fragment.HomeFragment;
import com.hustascii.ydfm.util.FontHelper;
import com.hustascii.ydfm.util.Globles;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    @Override
    public void init(Bundle savedInstanceState) {


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
        settingsSection = this.newSection("设置",getFragmentInstance(Globles.BASE_URL + "channel/6/"));



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


        this.setBackPattern(MaterialNavigationDrawer.BACKPATTERN_CUSTOM);
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
            startActivity(intent);
        }
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new BaseFragment();
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
}
