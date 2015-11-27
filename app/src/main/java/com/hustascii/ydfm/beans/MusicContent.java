package com.hustascii.ydfm.beans;

import android.os.Parcel;

import com.avos.avoscloud.AVClassName;
import com.avos.avoscloud.AVObject;

/**
 * Created by wei on 15-1-13.
 */
@AVClassName("MusicContent")
public class MusicContent extends AVObject{
//    private static final long serialVersionUID = -7060210544600464481L;
    public static final Creator CREATOR = AVObjectCreator.instance;


//    private String music_id;
//    private String title;
//    private String author;
//    private String speaker;
//    private String time;
//    private String listen;
//    private String imgUrl;
//    private String contentUrl;
    public String getMusic_id() {
        return getString("music_id");
    }

    public void setMusic_id(String music_id) {
        put("music_id",music_id);
    }


    public String getChannel() {
        return getString("channel");
    }

    public void setChannel(String channel) {
        put("channel",channel);
    }

    private String channel;

    public String getTitle() {
        return getString("title");
    }

    public void setTitle(String title) {
        put("title",title);
    }

    public String getAuthor() {
        return getString("author");
    }

    public void setAuthor(String author) {
        put("author",author);
    }

    public String getTime() {
        return getString("time");
    }

    public void setTime(String time) {
        put("time",time);
    }

    public String getSpeaker() {
        return getString("speaker");
    }

    public void setSpeaker(String speaker) {
        put("speaker",speaker);
    }

    public String getListen() {
        return getString("listen");
    }

    public void setListen(String listen) {
        put("listen",listen);
    }

    public String getImgUrl() {
        return getString("imgUrl");
    }

    public void setImgUrl(String imgUrl) {
        put("imgUrl",imgUrl);
    }

    public String getContentUrl() {
        return getString("contentUrl");
    }

    public void setContentUrl(String contentUrl) {
        put("contentUrl",contentUrl);
    }

//    public MusicContent(String author, String speaker, String time, String listen) {
//        this.author = author;
//        this.speaker = speaker;
//        this.time = time;
//        this.listen = listen;
//    }
//
//    public MusicContent(String id, String title, String author, String speaker, String time, String listen, String imgUrl, String contentUrl) {
//        this.id = id;
//        this.title = title;
//        this.author = author;
//        this.speaker = speaker;
//        this.time = time;
//        this.listen = listen;
//        this.imgUrl = imgUrl;
//        this.contentUrl = contentUrl;
//    }
//
//    public MusicContent(String title, String author, String speaker, String time, String listen, String imgUrl, String contentUrl) {
//        this.title = title;
//        this.author = author;
//        this.speaker = speaker;
//        this.time = time;
//        this.listen = listen;
//        this.imgUrl = imgUrl;
//        this.contentUrl = contentUrl;
//    }

    public MusicContent(Parcel in){
        super(in);
    }
    //此处为我们的默认实现，当然你也可以自行实现
}
