package com.hustascii.ydfm;

import android.app.Application;

import com.avos.avoscloud.AVObject;
import com.hustascii.ydfm.beans.MusicContent;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVAnalytics;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

/**
 * Created by wei on 15-1-13.
 */
public class MyApp extends Application {

    private static MyApp mContext;


    public static MyApp getContext() {
        return mContext;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;

        // This configuration tuning is custom. You can tune every option, you may tune some of them,
        // or you can create default configuration by
        //  ImageLoaderConfiguration.createDefault(this);
        // method.
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 2)//设置线程的优先级
                .denyCacheImageMultipleSizesInMemory()//当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//设置缓存文件的名字
                .discCacheFileCount(60)//缓存文件的最大个数
                .tasksProcessingOrder(QueueProcessingType.LIFO)// 设置图片下载和显示的工作队列排序
//                .enableLogging() //是否打印日志用于检查错误
                .build();

        //Initialize ImageLoader with configuration
        ImageLoader.getInstance().init(config);

        AVObject.registerSubclass(MusicContent.class);
        AVOSCloud.initialize(this, "tISXRXiqDs321Tax16GnVFNS", "Lg8G6xASb8OSUyqlQFNY93Gk");
//        CalligraphyConfig.initDefault("fonts/custom.TTF", R.attr.fontPath);
    }



}
