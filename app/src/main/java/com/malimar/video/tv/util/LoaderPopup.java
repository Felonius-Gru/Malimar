package com.malimar.video.tv.util;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.malimar.video.tv.R;

/**
 * Created by SAARA MOBITECH on 6/25/2016.
 */
public class LoaderPopup  {

    private PopupWindow popupWindow = null;
    private Context context = null;
    private View popUpView = null;

    public LoaderPopup(Context context) {
        this.context = context;
    }

    public synchronized void dismiss() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    public void show(View v) {
        LayoutInflater inflator = LayoutInflater.from(context);
        popUpView = inflator.inflate(R.layout.progress_wheel, null);
        popupWindow = new PopupWindow(popUpView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, true);

        try {
            popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        }
        catch(Throwable e) {
            Log.e("LoaderPopup" , e.toString(), e);
        }

    }
}

