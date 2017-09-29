/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.malimar.video.tv;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malimar.video.tv.db.SessionData;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.ui.Content;
import com.malimar.video.tv.ui.GridViewActivity;
import com.malimar.video.tv.ui.Launcher;
import com.malimar.video.tv.util.XMLParser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

/**
 * A collection of utility methods, all static.
 */
public class Utils {

    /*
     * Making sure public utility methods remain static
     */
    private Utils() {
    }

    /**
     * Returns the screen/display size
     */
    public static Point getDisplaySize(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static String getDurattion(Movie movie) {
        int ms = movie.getDuration() * 1000;
        return formatMillis(ms);
    }

    /**
     * Shows a (long) toast
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a (long) toast.
     */
    public static void showToast(Context context, int resourceId) {
        Toast.makeText(context, context.getString(resourceId), Toast.LENGTH_LONG).show();
    }

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public static int getIntValue(String value) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 0;
        }
    }

    public static String getSerialNumber() {
        return Build.SERIAL.toUpperCase();
    }

    /**
     * Formats time in milliseconds to hh:mm:ss string format.
     */
    public static String formatMillis(int millis) {
        String result = "";
        int hr = millis / 3600000;
        millis %= 3600000;
        int min = millis / 60000;
        millis %= 60000;
        int sec = millis / 1000;
        if (hr > 0) {
            result += hr + ":";
        }
        if (min >= 0) {
            if (min > 9) {
                result += min + ":";
            } else {
                result += "0" + min + ":";
            }
        }
        if (sec > 9) {
            result += sec;
        } else {
            result += "0" + sec;
        }
        return result;
    }

    public static String getAppVersionName(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static void commonAlert(String title, Activity context) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(title);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                context.getResources().getString(R.string.OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static void commonAlert(String title, Activity context, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(title);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                context.getResources().getString(R.string.OK), clickListener
        );
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public static String[] split(String original, String separator) {
        if (original == null) {
            original = "";
        }
        final Vector nodes = new Vector();

        int index = original.indexOf(separator);

        while (index >= 0) {
            nodes.addElement(original.substring(0, index));
            original = original.substring(index + separator.length());
            index = original.indexOf(separator);
        }

        nodes.addElement(original);

        // Create splitted string array
        final String[] result = new String[nodes.size()];

        if (nodes.size() > 0) {
            for (int loop = 0; loop < nodes.size(); loop++) {
                result[loop] = (String) nodes.elementAt(loop);
            }
        }

        return result;
    }

    public static boolean isAppUpgrade(String currentVersion, String serverVersion) {
        String[] currVers = split(currentVersion, ".");
        String[] serVers = split(serverVersion, ".");

        int count = Math.min(currVers.length, serVers.length);
        try {
            for (int i = 0; i < count; i++) {
                if (Integer.parseInt(serVers[i]) > Integer.parseInt(currVers[i])) {
                    return true;
                }

                if (Integer.parseInt(serVers[i]) < Integer.parseInt(currVers[i])) {
                    return false;
                }
            }

        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public static void handleMovieClick(Activity activity, Movie movie, Bundle bundle) {
        if (movie.getFeedUrl() != null && movie.getFeedUrl().length() > 0) {
            if (movie.getType().equalsIgnoreCase("episodes")) {
                Intent intent = new Intent(activity, GridViewActivity.class);
                intent.putExtra(Content.MOVIE, movie);
                activity.startActivity(intent);
            } else {
                Intent intent = new Intent(activity, Launcher.class);
                intent.putExtra("URL", movie.getFeedUrl());
                activity.startActivity(intent);
            }
        } else {
            Intent intent = new Intent(activity, Content.class);
            intent.putExtra(Content.MOVIE, movie);

            if (bundle != null) {
                activity.startActivity(intent, bundle);
            } else {
                activity.startActivity(intent);
            }
        }
    }

    public static byte[] read(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = stream.read(buffer)) != -1) {
            baos.write(buffer, 0, length);
        }
        return baos.toByteArray();
    }

    public static String getHFDUrl(String file) {
        if (file.length() > 0) {
            if (file.indexOf("HDF") != -1) {
                return file;
            }

            if (file.indexOf("HD") != -1) {
                int lastIndex = file.lastIndexOf(".");
                if (lastIndex != -1) {
                    String ext = file.substring(lastIndex + 1);
                    file = file.substring(0, lastIndex) + "F." + ext;
                }
            }
        }
        return file;
    }


  /*  public static void loadGlideImage(Context context, String file, ImageView imageView, int width, int height, boolean defineHeight) {
        if (file.length() > 0) {
            if (file.indexOf("HD") != -1) {
                int lastIndex = file.lastIndexOf(".");
                if (lastIndex != -1) {
                    String ext = file.substring(lastIndex + 1);
                    file = file.substring(0, lastIndex) + "F." + ext;
                }
            }
        }
        Drawable mDefaultBackground = context.getResources().getDrawable(R.drawable.malimar_logo);
        if (defineHeight) {
            Glide.with(context)
                    .load(file).asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .override(width,
                            height)
                    .error(mDefaultBackground)
                    .into(imageView);
        } else {
            Glide.with(context)
                    .load(file).asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .error(mDefaultBackground)
                    .into(imageView);
        }


    }
*/


    public static boolean authorizationAdult(SessionData sessionData) {
        String url = "";
        XMLParser parser = null;
        NodeList adList = sessionData.getConfigDoc().getElementsByTagName("AD");
        parser = new XMLParser();
        String adultUrl = "";
        for (int i = 0; i < adList.item(0).getChildNodes().getLength(); i++) {
            if (adList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("url")) {
                adultUrl = parser.getElementValue(adList.item(0).getChildNodes().item(i));
                break;
            }
        }
        url = adultUrl + Utils.getSerialNumber();
        XMLParser urlParser = new XMLParser();
        String xml = urlParser.getXmlFromUrl(url);
        Log.e("ContentNav", "xml:::" + xml);

        if (xml == null) {
            return false;
        }

        if (xml.length() == 0) {
            return true;
        }

        Document doc = parser.getDomElement(xml);
        Log.e("ContentNav", "doc:::" + doc);
        NodeList errorNodeList = doc.getElementsByTagName("Error");

        if (errorNodeList != null && errorNodeList.getLength() > 0) {
            Element e = (Element) errorNodeList.item(0);
            String code = parser.getValue(e, "Code");

            if (code.equalsIgnoreCase("NoSuchKey")) {
                return false;
            }
        }
        return true;
    }

    public static boolean authorizationPremium(SessionData sessionData) {
        String url = "";
        XMLParser parser = null;
        NodeList prList = sessionData.getConfigDoc().getElementsByTagName("PR");
        parser = new XMLParser();
        String premiumUrl = "";
        for (int i = 0; i < prList.item(0).getChildNodes().getLength(); i++) {
            if (prList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("url")) {
                premiumUrl = parser.getElementValue(prList.item(0).getChildNodes().item(i));
                break;
            }
        }
        url = premiumUrl + Utils.getSerialNumber();
        XMLParser urlParser = new XMLParser();
        String xml = urlParser.getXmlFromUrl(url);
        Log.e("ContentNav", "xml:::" + xml);

        if (xml == null) {
            return false;
        }

        if (xml.length() == 0) {
            return true;
        }

        Document doc = parser.getDomElement(xml);
        Log.e("ContentNav", "doc:::" + doc);
        NodeList errorNodeList = doc.getElementsByTagName("Error");

        if (errorNodeList != null && errorNodeList.getLength() > 0) {
            Element e = (Element) errorNodeList.item(0);
            String code = parser.getValue(e, "Code");

            if (code.equalsIgnoreCase("NoSuchKey")) {
                return false;
            }
        }
        return true;
    }


}
