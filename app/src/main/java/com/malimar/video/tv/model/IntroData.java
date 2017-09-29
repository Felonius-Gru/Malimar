package com.malimar.video.tv.model;

import android.util.Log;

import java.io.Serializable;

/**
 * Created by SAARA on 20-02-2017.
 */

public class IntroData implements Serializable {

    String title = "";
    String feed = "";

    public String getSdBackground() {
        return sdBackground;
    }

    public void setSdBackground(String sdBackground) {
        this.sdBackground = sdBackground;
    }

    public String getHdBackground() {
        return hdBackground;
    }

    public void setHdBackground(String hdBackground) {
        this.hdBackground = hdBackground;
    }

    public String getFeed() {
        return feed;
    }

    public void setFeed(String feed) {
        this.feed = feed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    String sdBackground = "";
    String hdBackground = "";


}
