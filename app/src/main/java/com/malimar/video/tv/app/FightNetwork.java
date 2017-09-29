package com.malimar.video.tv.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.exoplayer2.BuildConfig;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.malimar.video.tv.R;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.malimar.video.tv.db.PreferenceManager;


/**
 * Created by SAARA MOBITECH on 3/16/2016.
 */
public class FightNetwork extends MultiDexApplication implements
        Application.ActivityLifecycleCallbacks {


    private Tracker mTracker;
    private Handler handler = new Handler();
    private static final int DELAY = 500;
    private int resumed = 0;
    private int paused = 0;
    private static boolean inForeground = true;
    private static int numActivities = 0;

    synchronized public Tracker getDefaultTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUGA
            mTracker = analytics.newTracker(R.xml.analytics);
        }
        return mTracker;
    }


    public void onCreate() {
        super.onCreate();
        PreferenceManager.getInstance(getApplicationContext()).setContent(this);

        //    GoogleAnalytics.getInstance(this).getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            public void uncaughtException(Thread thread, Throwable ex) {
                // TODO Auto-generated method stub
            }
        });
        registerActivityLifecycleCallbacks(this);
        numActivities = 0;
        inForeground = true;
        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");
    }


    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    private void onForeground() {

    }

    private void onBackground() {
       //LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent("app_finish"));
    }

    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        ++numActivities;
    }

    public void onActivityStarted(Activity activity) {

    }

    public void onActivityResumed(Activity activity) {
        ++resumed;

        if (!inForeground) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    foregroundOrBackground();
                }
            }, DELAY);
        }
    }

    public void onActivityPaused(Activity activity) {
        ++paused;

        if (inForeground) {
            handler.postDelayed(new Runnable() {
                public void run() {
                    foregroundOrBackground();
                }
            }, DELAY);
        }
    }


    private void foregroundOrBackground() {
        if (paused >= resumed && inForeground) {
            inForeground = false;
            onBackground();

        } else if (resumed > paused && !inForeground) {
            inForeground = true;
            onForeground();
        }
    }

    public void onActivityStopped(Activity activity) {


    }

    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    public void onActivityDestroyed(Activity activity) {
        numActivities--;

    }


    public static void logEvent(Activity context, String screenName) {
        FightNetwork fightNetwork = (FightNetwork) context.getApplication();
        fightNetwork.logEvent(screenName);
    }

    public static void logVideoEvent(Activity context, String action, String videoName, Long value) {
        FightNetwork fightNetwork = (FightNetwork) context.getApplication();
        Tracker tracker = fightNetwork.getDefaultTracker();
        tracker.send(new HitBuilders.EventBuilder().setCategory("VIDEO").setAction(action).setLabel(videoName).setValue(value).build());
    }

    private void logEvent(String screenName) {
        Tracker tracker = getDefaultTracker();
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    protected String userAgent;


    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    public boolean useExtensionRenderers() {
        return BuildConfig.FLAVOR.equals("withExtensions");
    }
}


