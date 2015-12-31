package com.hustascii.ydfm.adapter;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.beans.MusicContentLite;
import com.hustascii.ydfm.util.AnimateFirstDisplayListener;
import com.hustascii.ydfm.util.Globles;
import com.marshalchen.ultimaterecyclerview.UltimateDifferentViewTypeAdapter;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerviewViewHolder;
import com.marshalchen.ultimaterecyclerview.UltimateViewAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

/**
 * Created by JianGuo on 15/12/29.
 */
public class MusicContentLiteAdapter extends UltimateViewAdapter<MusicContentLiteAdapter.MyViewHolder> {

    public interface ContentClickListener {

        void onClick(MusicContentLite musicContentLite);

    }

    private ContentClickListener contentClickListener;
    private DisplayImageOptions displayOptions;
    private ImageLoader mImageLoader;
    private boolean mBusy = false;
    private List<MusicContentLite> mList;


    public MusicContentLiteAdapter(@NonNull List<MusicContentLite> mList) {
        this.mList = mList;
        this.mImageLoader = ImageLoader.getInstance();
        displayOptions = new DisplayImageOptions.Builder()
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
                        //TODO: RecyclerView seems to have default anim for view loaded.
//                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间
                .build();//构建完成
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder getViewHolder(View view) {
        return new MyViewHolder(view, false);
    }

    public void setContentClickListener(ContentClickListener contentClickListener) {
        this.contentClickListener = contentClickListener;
    }

    public void setFlagBusy(boolean busy) {
        this.mBusy = busy;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_info_card, parent, false);
        MyViewHolder vh = new MyViewHolder(view, true);
        return vh;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getAdapterItemCount() {
        return mList.size();
    }


    @Override
    public long generateHeaderId(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        if ((position < getItemCount() && customHeaderView != null ? position <= mList.size() : position < mList.size()) && (customHeaderView != null ? position > 0 : true)) {
            final MusicContentLite musicContent = mList.get(position);
            if (contentClickListener != null) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        contentClickListener.onClick(musicContent);
                    }
                });
            }
            if (!mBusy) {
                Log.v("musicContent.url", musicContent.getImgUrl());
                mImageLoader.displayImage(Globles.BASE_URL + musicContent.getImgUrl().substring(1), holder.mImg, displayOptions, new AnimateFirstDisplayListener());
            } else {
                mImageLoader.displayImage(musicContent.getImgUrl(), holder.mImg, displayOptions);
            }

            holder.mTitle.setText(musicContent.getTitle());
            holder.mAuthor.setText(musicContent.getAuthor());
            if (musicContent.getSpeaker().length() > 4) {
                holder.mSpeaker.setText(musicContent.getSpeaker().substring(0, 4) + "...");
            } else {
                holder.mSpeaker.setText(musicContent.getSpeaker());
            }
            holder.mListen.setText(musicContent.getListen());
            holder.mTimer.setText(musicContent.getTime());
        }


    }


    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public boolean onFailedToRecycleView(MyViewHolder holder) {
        return true;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    public void insert(MusicContentLite musicContentLite, int pos) {
        insert(mList, musicContentLite, pos);
    }

    public void addAll(List<MusicContentLite> data) {
        for (MusicContentLite musicContentLite : data) {
            insert(musicContentLite, 0);
        }
    }

    class MyViewHolder extends UltimateRecyclerviewViewHolder {

        TextView mTitle;
        TextView mAuthor;
        TextView mSpeaker;
        TextView mListen;
        TextView mTimer;
        ImageView mImg;

        public MyViewHolder(View itemView, boolean isItem) {
            super(itemView);
            if (isItem) {
                mTitle = (TextView) itemView.findViewById(R.id.title);
                mAuthor = (TextView) itemView.findViewById(R.id.author);
                mSpeaker = (TextView) itemView.findViewById(R.id.speaker);
                mListen = (TextView) itemView.findViewById(R.id.listen);
                mTimer = (TextView) itemView.findViewById(R.id.time);
                mImg = (ImageView) itemView.findViewById(R.id.list_img);
            }
        }

        @Override
        public void onItemSelected() {
            itemView.setBackgroundColor(Color.GRAY);
        }
    }
}
