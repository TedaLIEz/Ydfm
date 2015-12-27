package com.hustascii.ydfm.util;

/**
 * Created by wei on 15/11/30.
 */

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.Build;

public class NetWorkUtils {
    /**
     * 网络是否已经连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkinfo = manager.getActiveNetworkInfo();

        return networkinfo != null && networkinfo.isAvailable()
                && networkinfo.isConnected();
    }

    /**
     * 返回网络状态
     *
     * @return 1为成功WiFi已连接，2为cmnet，3为cmwap，4为ctwap， -1为网络未连接
     */
    @SuppressWarnings("deprecation")
    public static int checkNetworkStatus(Context context) {
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);// 上下文连接服务
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();// 给定网络接口的状态类型
        if (info == null || !info.isAvailable()) {// 无网络判断
            return (Globles.CURRENT_NETWORK_STATE_TYPE = Globles.NETWORK_STATE_IDLE);
        }
        String typeName = info.getTypeName();
        if (typeName.equals("WIFI")) {
            return (Globles.CURRENT_NETWORK_STATE_TYPE = Globles.NETWORK_STATE_WIFI);
        }
        String extraName = info.getExtraInfo();
        if (extraName == null || extraName.trim().length() == 0) {
            String proxyHost = null;
            if (Build.VERSION.SDK_INT >= 13) {
                proxyHost = System.getProperties().getProperty("http.proxyHost");
            } else {
                proxyHost = Proxy.getHost(context);
            }
            return (Globles.CURRENT_NETWORK_STATE_TYPE = (proxyHost == null ? Globles.NETWORK_STATE_CMNET
                    : Globles.NETWORK_STATE_CMWAP));
        }

        if (extraName.equals("cmnet") || extraName.equals("3gnet")
                || extraName.equals("uninet") || extraName.equals("ctnet")
                || extraName.equals("ctnet:CDMA") || extraName.equals("CTC")) {
            return (Globles.CURRENT_NETWORK_STATE_TYPE = Globles.NETWORK_STATE_CMNET);
        } else if (extraName.equals("cmwap") || extraName.equals("3gwap")
                || extraName.equals("uniwap")) {
            return (Globles.CURRENT_NETWORK_STATE_TYPE = Globles.NETWORK_STATE_CMWAP);
        } else if ("ctwap:CDMA".equals(extraName) || extraName.equals("ctwap")
                || extraName.equals("#777")) {
            return (Globles.CURRENT_NETWORK_STATE_TYPE = Globles.NETWORK_STATE_CTWAP);
        }

        String proxyHost = null;
        if (Build.VERSION.SDK_INT >= 13) {
            proxyHost = System.getProperties().getProperty("http.proxyHost");
        } else {
            proxyHost = Proxy.getHost(context);
        }
        return (Globles.CURRENT_NETWORK_STATE_TYPE = (proxyHost == null ? Globles.NETWORK_STATE_CMNET
                : Globles.NETWORK_STATE_CMWAP));
    }
}
