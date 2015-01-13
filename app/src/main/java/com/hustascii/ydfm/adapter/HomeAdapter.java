package com.hustascii.ydfm.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.beans.Item;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wei on 15-1-12.
 */
public class HomeAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<Item> mList;
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;
    private Context mContext;

    private boolean mBusy = false;
    public void setFlagBusy(boolean busy){
        this.mBusy = busy;
    }
    public HomeAdapter(Context context,ArrayList<Item> list){
        this.mInflater = LayoutInflater.from(context);
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
            holder.mImg = (ImageView)view.findViewById(R.id.list_img);
            holder.mAuthor = (TextView)view.findViewById(R.id.author);
            holder.mSpeaker = (TextView)view.findViewById(R.id.speaker);
            holder.mTimer= (TextView)view.findViewById(R.id.time);
            holder.mListen= (TextView)view.findViewById(R.id.listen);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }
        String img = "http://yuedu.fm/static/file/large/bb962816793704ba4cdb9516ce4a829f";
        final Item item = mList.get(i) ;
        if(!mBusy){
            mImageLoader.displayImage(img, holder.mImg, options);
        }else{
            mImageLoader.displayImage(img, holder.mImg, options);

        }

        holder.mAuthor.setText(item.getAuthor());
        holder.mSpeaker.setText(item.getSpeaker());
        holder.mListen.setText(item.getListen());
        holder.mTimer.setText(item.getTime());
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


    public final class ViewHolder{
        public ImageView mImg;
        public TextView mAuthor;
        public TextView mSpeaker;
        public TextView mTimer;
        public TextView mListen;
    }
}
