package com.hustascii.ydfm.util;

/**
 * Created by wei on 15-1-16.
 */


import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.Log;
import android.widget.SeekBar;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class MusicPlayer implements OnBufferingUpdateListener, OnCompletionListener,
        OnPreparedListener {

    public MediaPlayer mediaPlayer; // 媒体播放器
    private DiscreteSeekBar seekBar; // 拖动条
    private Timer mTimer = new Timer(); // 计时器

    private String url;

    private static MusicPlayer musicPlayer;// 初始化播放器

    public MusicPlayer(DiscreteSeekBar seekBar) {
        super();
        this.seekBar = seekBar;
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
            mediaPlayer.setOnBufferingUpdateListener(this);
            mediaPlayer.setOnPreparedListener(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 每一秒触发一次
        mTimer.schedule(timerTask, 0, 1000);
    }

    public static MusicPlayer getInstance(DiscreteSeekBar seekBar){
        if(musicPlayer==null)
            musicPlayer = new MusicPlayer(seekBar);
        else{
            musicPlayer.setSeekBar(seekBar);
        }
        return musicPlayer;
    }

    public DiscreteSeekBar getSeekBar() {
        return seekBar;
    }

    public void setSeekBar(DiscreteSeekBar seekBar) {
        this.seekBar = seekBar;
    }

    // 计时器
    TimerTask timerTask = new TimerTask() {

        @Override
        public void run() {
            if (mediaPlayer == null)
                return;
            if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
                handler.sendEmptyMessage(0); // 发送消息
            }
        }
    };

    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            if (duration > 0) {
                // 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);
            }
        }

        ;
    };

    public void play() {
        mediaPlayer.start();
    }


    public Boolean isplay(){
        return mediaPlayer.isPlaying();
    }

    public int getpos(){
        int position = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if(duration>0) {
            long pos = seekBar.getMax() * position / duration;
            return (int)pos;
        }
        return 0;
    }
    public void prepare() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mediaPlayer.reset();
                    mediaPlayer.setDataSource(url); // 设置数据源
                    mediaPlayer.prepareAsync(); // prepare自动播放
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // 暂停
    public void pause() {
        mediaPlayer.pause();
    }

    // 停止
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            //mediaPlayer.release();
            //mediaPlayer = null;
        }
    }

    // 播放准备
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Log.e("mediaPlayer", "onPrepared");
    }

    // 播放完成
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("mediaPlayer", "onCompletion");
    }

    /**
     * 缓冲更新
     */
    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        seekBar.setP
        //seekBar.setSecondaryProgress(percent);
        int currentProgress = seekBar.getMax()
                * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
        Log.e(currentProgress + "% play", percent + " buffer");
    }

    public void release(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
