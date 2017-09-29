package com.malimar.video.tv.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.db.SessionData;
import com.malimar.video.tv.db.UserBeanProvider;
import com.malimar.video.tv.model.IntroData;
import com.malimar.video.tv.model.UserBean;
import com.malimar.video.tv.util.Constant;
import com.malimar.video.tv.util.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.nostra13.universalimageloader.core.ImageLoader.TAG;

/**
 * Created by SAARA on 20-02-2017.
 */

public class SplashActivity extends BaseActivity {

    private SessionData sessionData;
    private ImageView mainImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        mainImage = (ImageView) findViewById(R.id.mainImage);

        UserBean userBean = UserBeanProvider.getInstance(SplashActivity.this).load();
        SessionData.getInstance().setUserBean(userBean);

        loadSplashImage();

    }


    private void loadSplashImage() {
        Glide.with(this)
                .load(Constant.SPlASH_URL)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        new LoadIntroTask().execute();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        new LoadIntroTask().execute();
                        return false;
                    }
                })
                .into(mainImage);
    }

    private void loadMainData() {
        sessionData = SessionData.getInstance();
        sessionData.getIntroData().clear();
        boolean authorized;
        Document docMainFeed = null;
        XMLParser mainParser = new XMLParser();
        for (int i = 0; i < Constant.MAIN_URLS.length; i++) {
            String xmlHomeFeed = mainParser.getXmlFromUrl(Constant.MAIN_URLS[i]);
            if (xmlHomeFeed != null) {
                docMainFeed = mainParser.getDomElement(xmlHomeFeed);
                NodeList errorFeed = docMainFeed.getElementsByTagName("Error");
                if (errorFeed != null && errorFeed.getLength() > 0) {
                    docMainFeed = null;
                }
            }

            if (docMainFeed != null) {
                sessionData.setConfigDoc(docMainFeed);
                break;
            }
        }

        NodeList configNode = sessionData.getConfigDoc().getElementsByTagName("config");
        Element element = (Element) configNode.item(0);
        sessionData.setPremiumVideo(Utils.authorizationPremium(sessionData));
        sessionData.setAdultVideo(Utils.authorizationAdult(sessionData));
        homeFeedData(mainParser.getValue(element, "homeFeedUrl"));
    }

    private void homeFeedData(String url) {
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(url);
        Document doc = parser.getDomElement(xml);
        NodeList introList = doc.getElementsByTagName("item");
        for (int i = 0; i < introList.getLength(); i++) {
            Element element = (Element) introList.item(i);
            IntroData model = new IntroData();
            model.setHdBackground(element.getAttribute("hdBackground"));
            model.setSdBackground(element.getAttribute("sdBackground"));
            model.setTitle(parser.getValue(element, "title"));
            model.setFeed(parser.getValue(element, "feed"));
            sessionData.addData(model);
        }
    }


    private class LoadIntroTask extends AsyncTask<Void, Void, Void> {
        public LoadIntroTask() {
        }

        protected void onPostExecute(Void result) {
            try {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(SplashActivity.this, ChooseLanguageActivity.class);
                        startActivity(i);
                        finish();
                    }
                }, 4000);

            } catch (Exception e) {

            }
        }

        protected Void doInBackground(Void... args) {
            loadMainData();
            return null;
        }
    }
}
