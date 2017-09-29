package com.malimar.video.tv.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.model.Movie;

import java.util.ArrayList;

/**
 * Created by SAARA MOBITECH on 7/23/2016.
 */
public class CoverFlowAdapter  extends BaseAdapter {

    private ArrayList<Movie> data;
    private Context activity;
    public CoverFlowAdapter(Context context, ArrayList<Movie> objects) {
        this.activity = context;
        this.data = objects;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Movie getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_item,null,false);

            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //viewHolder.gameImage.setImageResource(data.get(position).getId());
        viewHolder.gameName.setText(data.get(position).getTitle());

      try{
          Glide.with(activity)
                  .load(data.get(position).getCardImageUrl())
                  .override(Utils.convertDpToPixel(activity, 500),
                          Utils.convertDpToPixel(activity, 280))
                  .into(viewHolder.gameImage);

         }catch (Exception e){
}

        return convertView;

    }


    private static class ViewHolder {
        private TextView gameName;
        private ImageView gameImage;

        public ViewHolder(View v) {
            gameImage = (ImageView) v.findViewById(R.id.poster);
            gameName = (TextView) v.findViewById(R.id.description);
        }
    }
}