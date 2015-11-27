package com.hustascii.ydfm.util;

import com.hustascii.ydfm.beans.MusicContent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by wei on 15-1-16.
 */
public class Crawls {
    public static ArrayList<MusicContent> getListInfo(String responseBody) {

        return null;
    }

    public static String getMusicUrl(String responseBody) {
        String result;
        Document doc = Jsoup.parse(new String(responseBody));
        Elements mp3s=doc.body().select("script:not([src])");
        String reg="/static/file/pod/[\\S\\s]*?mp3";
        Pattern p= Pattern.compile(reg);
        Matcher m=p.matcher(mp3s.get(1).toString());
        if(m.find()) {
            result = m.group();
        }else{
            result = "";
        }
//        result = "http://yuedu.fm/static/file/pod/88c5e617cb844e1950b3c7cda33992d4.mp3";

        return result;
    }

    public static String getArticleUrl(String responseBody) {
        String result;
        Document doc = Jsoup.parse(new String(responseBody));
        Element mp3s = doc.select("div.item-intro").select(".row").select(":not(span)").first();
        mp3s.select("span").remove();
        mp3s.select("a").remove();
        mp3s.removeAttr("class").removeAttr("href");
        result = mp3s.toString().replace("<div>", "").replace("</div>", "");
        return result;
    }
}
