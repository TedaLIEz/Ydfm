package com.hustascii.ydfm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.beans.MusicContent;
import com.hustascii.ydfm.beans.MusicContentLite;
import com.hustascii.ydfm.util.AnimateFirstDisplayListener;
import com.hustascii.ydfm.util.Globles;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by wei on 15-1-12.
 */
@Deprecated
public class HomeAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<MusicContentLite> mList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private Context mContext;

    private boolean mBusy = false;
    public void setFlagBusy(boolean busy){
        this.mBusy = busy;
    }

    public HomeAdapter(Context context, List<MusicContentLite> list) {
        this.mInflater = LayoutInflater.from(context);
//        this.mList = list;
        this.mList = list;
        this.mContext = context;
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
                //.delayBeforeLoading(int delayInMillis)//int delayInMillis为你设置的下载前的延迟时间
                //设置图片加入缓存前，对bitmap进行设置
                //.preProcessor(BitmapProcessor preProcessor)
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_info, null);
            holder = new ViewHolder();
            holder.mTitle = (TextView)view.findViewById(R.id.title);

            holder.mImg = (ImageView)view.findViewById(R.id.list_img);
            holder.mAuthor = (TextView)view.findViewById(R.id.author);
            holder.mSpeaker = (TextView)view.findViewById(R.id.speaker);
            holder.mTimer= (TextView)view.findViewById(R.id.time);
            holder.mListen= (TextView)view.findViewById(R.id.listen);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        final MusicContentLite musicContent = mList.get(i);
        if(!mBusy){
            Log.v("musicContent.url", musicContent.getImgUrl());
            mImageLoader.displayImage(Globles.BASE_URL+ musicContent.getImgUrl().substring(1), holder.mImg, options,new AnimateFirstDisplayListener());
        }else{
            mImageLoader.displayImage(musicContent.getImgUrl(), holder.mImg, options);

        }
        holder.mTitle.setText(musicContent.getTitle());
        holder.mAuthor.setText(musicContent.getAuthor());
        if(musicContent.getSpeaker().length()>4){
            holder.mSpeaker.setText(musicContent.getSpeaker().substring(0,4)+"...");
        }else{
            holder.mSpeaker.setText(musicContent.getSpeaker());
        }
        holder.mListen.setText(musicContent.getListen());
        holder.mTimer.setText(musicContent.getTime());

        return view;

    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    public class ViewHolder{
        TextView mTitle;
        TextView mAuthor;
        TextView mSpeaker;
        TextView mListen;
        TextView mTimer;
        ImageView mImg;

    }

}
