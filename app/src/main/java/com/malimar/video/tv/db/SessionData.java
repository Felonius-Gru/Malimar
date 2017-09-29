package com.malimar.video.tv.db;

import android.content.Context;

import com.malimar.video.tv.model.IntroData;
import com.malimar.video.tv.model.UserBean;
import com.malimar.video.tv.util.XMLParser;

import org.w3c.dom.Document;

import java.util.ArrayList;


/**
 * Created by SAARA on 11-01-2017.
 */
public class SessionData {
    private static SessionData instance = null;

    public static synchronized SessionData getInstance() {
        if (instance == null) {
            instance = new SessionData();
        }
        return instance;
    }


    private ArrayList<IntroData> iList = new ArrayList<>();

    public ArrayList<IntroData> getIntroData() {
        return iList;
    }

    public void addData(IntroData shows) {
        iList.add(shows);
    }


    public Document getConfigDoc() {
        return configDoc;
    }

    public void setConfigDoc(Document configDoc) {
        this.configDoc = configDoc;
    }

    public UserBean getUserBean() {
        return userBean;
    }

    public void setUserBean(UserBean userBean) {
        this.userBean = userBean;
    }

    public void save(Context context) {
        UserBeanProvider.getInstance(context).store(userBean);
    }

    private UserBean userBean = null;

    private Document configDoc = null;

    public boolean isAdultVideo() {
        return adultVideo;
    }

    public void setAdultVideo(boolean adultVideo) {
        this.adultVideo = adultVideo;
    }

    public boolean isPremiumVideo() {
        return premiumVideo;
    }

    public void setPremiumVideo(boolean premiumVideo) {
        this.premiumVideo = premiumVideo;
    }

    private boolean adultVideo = false;
    private boolean premiumVideo = false;

}
