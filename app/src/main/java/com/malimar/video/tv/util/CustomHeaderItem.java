package com.malimar.video.tv.util;

import android.support.v17.leanback.widget.HeaderItem;

/**
 * Created by SAARA on 17-02-2017.
 */

public class CustomHeaderItem extends HeaderItem {


    private static final String TAG = CustomHeaderItem.class.getSimpleName();

    private int mCount = 0;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String title;

    public CustomHeaderItem(int index, String name, int count, String title) {
        super(index, title, null);
        mCount = count;
        this.title = name;
    }

    public int getCount() {
        return mCount;
    }
}
