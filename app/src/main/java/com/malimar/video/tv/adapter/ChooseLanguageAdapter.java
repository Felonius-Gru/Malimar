package com.malimar.video.tv.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.malimar.video.tv.R;
import com.malimar.video.tv.model.IntroData;

import java.util.List;
import java.util.Locale;

/**
 * Created by SAARA on 20-02-2017.
 */

public class ChooseLanguageAdapter extends ArrayAdapter<IntroData> {
    private Context context;

    public ChooseLanguageAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    public View getView(int position, View mView, ViewGroup parent) {
        IntroData model = getItem(position);
        ViewHolder holder;
        if (mView == null) {
            mView = LayoutInflater.from(context).inflate(R.layout.choose_list_item, null);
            holder = new ViewHolder();
            holder.title = (TextView) mView.findViewById(R.id.title);
            mView.setTag(holder);

        } else {
            holder = (ViewHolder) mView.getTag();
        }


        holder.title.setText(model.getTitle());
        return mView;
    }

    private static class ViewHolder {
        TextView title;
    }

}

