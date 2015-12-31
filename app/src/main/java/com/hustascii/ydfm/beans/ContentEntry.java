package com.hustascii.ydfm.beans;

import java.io.Serializable;

/**
 * Created by wei on 15/11/30.
 */
public class ContentEntry implements Serializable{
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

    @Override
    public String toString() {
        return "ContentEntry{" +
                "music_id='" + music_id + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", speaker='" + speaker + '\'' +
                ", time='" + time + '\'' +
                ", listen='" + listen + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", contentUrl='" + contentUrl + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContentEntry that = (ContentEntry) o;

        if (music_id != null ? !music_id.equals(that.music_id) : that.music_id != null)
            return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (author != null ? !author.equals(that.author) : that.author != null) return false;
        if (speaker != null ? !speaker.equals(that.speaker) : that.speaker != null) return false;
        if (time != null ? !time.equals(that.time) : that.time != null) return false;
        if (listen != null ? !listen.equals(that.listen) : that.listen != null) return false;
        if (imgUrl != null ? !imgUrl.equals(that.imgUrl) : that.imgUrl != null) return false;
        return !(contentUrl != null ? !contentUrl.equals(that.contentUrl) : that.contentUrl != null);

    }

    @Override
    public int hashCode() {
        int result = music_id != null ? music_id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (author != null ? author.hashCode() : 0);
        result = 31 * result + (speaker != null ? speaker.hashCode() : 0);
        result = 31 * result + (time != null ? time.hashCode() : 0);
        result = 31 * result + (listen != null ? listen.hashCode() : 0);
        result = 31 * result + (imgUrl != null ? imgUrl.hashCode() : 0);
        result = 31 * result + (contentUrl != null ? contentUrl.hashCode() : 0);
        return result;
    }
}
