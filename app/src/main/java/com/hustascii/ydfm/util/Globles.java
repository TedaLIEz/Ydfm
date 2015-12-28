package com.hustascii.ydfm.util;

import android.content.Context;
import android.content.res.TypedArray;

import java.lang.reflect.Field;

/**
 * Created by wei on 15-1-14.
 */
public class Globles {
    public static boolean DEBUG = false;
    public static final String TAG = "Ydfm";
    public static final int NETWORK_STATE_IDLE = -1;
    public static final int NETWORK_STATE_WIFI = 1;
    public static final int NETWORK_STATE_CMNET = 2;
    public static final int NETWORK_STATE_CMWAP = 3;
    public static final int NETWORK_STATE_CTWAP = 4;

    public static int CURRENT_NETWORK_STATE_TYPE = NETWORK_STATE_IDLE;


    public static String BASE_URL = "http://yuedu.fm/";

    public static int getStatusBarHeight(Context context){
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return statusBarHeight;
    }

    //获取应用标题栏高度
    public static int getActionBarHeight(Context context){
        return getThemeAttributeDimensionSize(context, android.R.attr.actionBarSize);
    }


    public static int getThemeAttributeDimensionSize(Context context, int attr)
    {
        TypedArray a = null;
        try{
            a = context.getTheme().obtainStyledAttributes(new int[] { attr });
            return a.getDimensionPixelSize(0, 0);
        }finally{
            if(a != null){
                a.recycle();
            }
        }
    }
}
