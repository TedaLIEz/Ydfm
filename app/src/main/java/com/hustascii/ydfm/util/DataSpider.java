package com.hustascii.ydfm.util;

import android.content.Context;

import com.hustascii.ydfm.beans.Item;

import org.apache.http.util.ByteArrayBuffer;
import org.apache.http.util.EncodingUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wei on 15-1-13.
 */
public class DataSpider {
    private String url;
    private Context context;
    public DataSpider(Context context,String url){
        this.url = url;
        this.context = context;
    }

    public ArrayList<Item> getData(){
        Document document = Jsoup.parse(getHtmlString(this.url));

        Elements authors=document.select("div.channel-meta").select("span:has(i.fa-pencil)");
        Elements speakers=document.select("div.channel-meta").select("span:has(i.fa-microphone)");
        Elements times=document.select("div.channel-meta").select("span:has(i.fa-clock-o)");
        Elements clicks=document.select("div.channel-meta").select("span:has(.fa-headphones)");
        Elements titles=document.select("div.channel-title").select("a");
        Elements urls=document.select("div.channel-title").select("a");
        Elements imgs=document.select("div.channel-pic").select("img");

        ArrayList<Item> list=new ArrayList<Item>();
        for(int i=0;i<authors.size();i++){
            Item item=new Item(authors.get(i).text(),speakers.get(i).text(),times.get(i).text(),clicks.get(i).text());
            item.setImgUrl(imgs.get(i).attr("abs:src"));
            item.setTitle(titles.get(i).text());
            item.setContentUrl(urls.get(i).attr("abs:href"));
            list.add(item);
        }
        return list;
    }

    public String getHtmlString(String urlString) {
        try {
            URL url = new URL(urlString);
            URLConnection ucon = url.openConnection();
            InputStream instr = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(instr);
            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            return EncodingUtils.getString(baf.toByteArray(), "utf-8");
        } catch (Exception e) {
            return "";
        }
    }

}