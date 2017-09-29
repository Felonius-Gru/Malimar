package com.malimar.video.tv.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SAARA on 10-01-2017.
 */
public class PreferenceManager extends PreferenceHelper {

    private static final String TAG = "PreferenceManager";
    protected static PreferenceManager instance;

    private Context context;

    public static PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context);
        }
        return instance;
    }

    protected PreferenceManager(Context context) {
        super(context);
        this.context = context;
    }

    public static final String ACCESS_TOKEN = "access_token";

    public void storeBooleanValue(String key, boolean value) {
        storeValue(key, value, false);
    }


    public boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }


    public String getStringValue(String key) {
        return getStringValue(key, false);
    }

    public void storeStringValue(String key, String value) {
        storeValue(key, value, false);
    }


    private static final String NAME = "settings.db";

    public void setContent(Context context) {
        context = context;
    }

    public int getProgress(String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        long offset = sharedPref.getLong(key + "_offset", 0);
        long duration = sharedPref.getLong(key + "_duration", 0);
        if (duration == 0) {
            return 0;
        }
        return (int) (offset * 100 / duration);
    }

    public long getOffset(String key) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        long offset = sharedPref.getLong(key + "_offset", 0);
        return offset;
    }

    public void setProgress(String key, long offset, long duration) {
        SharedPreferences sharedPref = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putLong(key + "_offset", offset);
        editor.putLong(key + "_duration", duration);
        editor.commit();
    }
}


