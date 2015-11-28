package com.hustascii.ydfm.beans;

import android.os.Parcelable;

import com.avos.avoscloud.AVObject;

import org.apache.http.entity.SerializableEntity;

import java.io.Serializable;

/**
 * Created by wei on 15/11/28.
 */
public class MusicContentLite implements Serializable {
    private static final long serialVersionUID = -7060210544600464481L;


    private String music_id;
    private String title;
    private String author;
    private String speaker;
    private String time;
    private String listen;
    private String imgUrl;
    private String contentUrl;


    public MusicContentLite(String author, String speaker, String time, String listen) {
        this.author = author;
        this.speaker = speaker;
        this.time = time;
        this.listen = listen;
    }
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

    public MusicContentLite(String title, String author, String speaker, String time, String listen, String imgUrl, String contentUrl) {
        this.title = title;
        this.author = author;
        this.speaker = speaker;
        this.time = time;
        this.listen = listen;
        this.imgUrl = imgUrl;
        this.contentUrl = contentUrl;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getMusic_id() {
        return music_id;
    }

    public void setMusic_id(String music_id) {
        this.music_id = music_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getListen() {
        return listen;
    }

    public void setListen(String listen) {
        this.listen = listen;
    }

    public String getContentUrl() {
        return contentUrl;
    }

    public void setContentUrl(String contentUrl) {
        this.contentUrl = contentUrl;
    }
}
