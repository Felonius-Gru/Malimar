package com.malimar.video.tv.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.adapter.ChooseLanguageAdapter;
import com.malimar.video.tv.app.FightNetwork;
import com.malimar.video.tv.db.SessionData;
import com.malimar.video.tv.model.Movie;

/**
 * Created by SAARA on 20-02-2017.
 */

public class ChooseLanguageFragment extends android.app.Fragment {
    private SessionData sessionData;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_language_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        sessionData = SessionData.getInstance();
        ListView listView = (ListView) v.findViewById(R.id.listView);
        ChooseLanguageAdapter adapter = new ChooseLanguageAdapter(getActivity().getApplicationContext(), 0);
        listView.setAdapter(adapter);
        adapter.addAll(sessionData.getIntroData());
        adapter.notifyDataSetChanged();
        FightNetwork.logEvent(getActivity(), "CHOOSE_LANGUAGE_SCREEN");

        final ImageView bigImage = (ImageView) v.findViewById(R.id.bigImageView);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Glide.with(getActivity())
                        .load(Utils.getHFDUrl(sessionData.getIntroData().get(position).getHdBackground())).asBitmap()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .error(getResources().getDrawable(R.drawable.malimar_logo))
                        .into(bigImage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), Launcher.class);
                intent.putExtra("home", true);
                intent.putExtra("URL", sessionData.getIntroData().get(position).getFeed());
                getActivity().startActivity(intent);
            }
        });
    }


}
