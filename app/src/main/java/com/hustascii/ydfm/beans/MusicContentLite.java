package com.hustascii.ydfm.beans;

import android.os.Parcelable;

import com.avos.avoscloud.AVObject;

import org.apache.http.entity.SerializableEntity;

import java.io.Serializable;

/**
 * Created by wei on 15/11/28.
 */
public class MusicContentLite extends ContentEntry {

    public static final int TYPE_DEFAULT = 0;
    public static final int TYPE_NEW = 1;
    public static final int TYPE_HOT = 2;
//    public static final int TYPE_BANNER = 3;



    private int is_download = 0;

    private static final long serialVersionUID = -7060210544600464481L;

    public int getIs_download() {
        return is_download;
    }

    public void setIs_download(int is_download) {
        this.is_download = is_download;
    }

    public MusicContentLite(){};

    public MusicContentLite(String author, String speaker, String time, String listen) {
        super(author, speaker, time, listen);
    }


    public MusicContentLite(String title, String author, String speaker, String time, String listen, String imgUrl, String contentUrl) {
        super(title, author, speaker, time, listen, imgUrl, contentUrl);
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    @Override
    public String toString() {
        return super.toString() + "MusicContentLite{" +
                "is_download=" + is_download +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        MusicContentLite that = (MusicContentLite) o;

        return is_download == that.is_download;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + is_download;
        return result;
    }
}
