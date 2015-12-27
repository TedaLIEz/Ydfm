package com.hustascii.ydfm.beans;

/**
 * Created by wei on 15/11/30.
 */
public class ContentEntry {
    private String music_id;
    private String title;
    private String author;
    private String speaker;
    private String time;
    private String listen;
    private String imgUrl;
    private String contentUrl;


    public ContentEntry() {
    }

    public ContentEntry(String author, String speaker, String time, String listen) {
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

    public ContentEntry(String title, String author, String speaker, String time, String listen, String imgUrl, String contentUrl) {
        this.title = title;
        this.author = author;
        this.speaker = speaker;
        this.time = time;
        this.listen = listen;
        this.imgUrl = imgUrl;
        this.contentUrl = contentUrl;
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
