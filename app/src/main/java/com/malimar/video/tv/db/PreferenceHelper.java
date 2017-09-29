package com.malimar.video.tv.db;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by SAARA on 10-01-2017.
 */
public class PreferenceHelper {


    private SharedPreferences prefs;

    protected PreferenceHelper(Context context) {
        prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(context);
    }

    protected String getStringValue(String key, boolean useDecryption) {
        String value = prefs.getString(key, "");
        if (useDecryption && value.length() > 0)
            return SecurityHelper.decodeString(value);
        else
            return value;
    }

    protected long getLongValue(String key, boolean useDecryption) {
        String stringValue = getStringValue(key, useDecryption);
        if (stringValue == null)
            return 0L;
        else
            return Long.valueOf(stringValue);
    }

    protected int getIntValue(String key, boolean useDecryption) {
        String stringValue = getStringValue(key, useDecryption);
        if (stringValue == null)
            return 0;
        else
            return Integer.valueOf(stringValue);
    }

    public void logout() {
        prefs.edit().clear().commit();
    }

    protected boolean getBooleanValue(String key, boolean useDecryption) {
        return getBooleanValue(key, useDecryption, false);
    }

    protected boolean getBooleanValue(String key, boolean useDecryption, boolean defaultValue) {
        String stringValue = getStringValue(key, useDecryption);
        if (stringValue == null)
            return defaultValue;
        else
            return Boolean.valueOf(stringValue);
    }

    protected <U> void storeValue(String key, U value, boolean useEncryption) {
        String resultValue;
        if (useEncryption)
            resultValue = SecurityHelper.encodeString(String.valueOf(value));
        else
            resultValue = String.valueOf(value);
        prefs.edit().putString(key, resultValue).commit();
    }

    protected <U> void removeValue(String key) {
        prefs.edit().remove(key).commit();
    }


}

