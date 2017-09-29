package com.malimar.video.tv.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.data.VideoProvider;
import com.malimar.video.tv.model.Category;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.util.CoverFlowAdapter;
import com.sample.amazon.asbuilibrary.list.CarouselView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAARA MOBITECH on 7/23/2016.
 */


public class CustomSearchFragment extends android.app.Fragment implements
        AdapterView.OnItemSelectedListener, AdapterView.OnItemClickListener {

    private EditText searchVideo;
    private ArrayList<Movie> searchList;
    private ArrayObjectAdapter mRowsAdapter;
    private static Context mContext;
    private CoverFlowAdapter adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }


    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        View view = inflater.inflate(R.layout.custom_search, container, false);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        searchVideo = (EditText) view.findViewById(R.id.searchVideo);
        searchList = new ArrayList<>();
        addSearchAction();
        adapter = new CoverFlowAdapter(mContext, searchList);
        CarouselView<CoverFlowAdapter> boxesCarousel = (CarouselView<CoverFlowAdapter>) view.findViewById(R.id.coverflow);
        boxesCarousel.setAdapter(adapter);
        boxesCarousel.addItemSelectedListener(this);
        boxesCarousel.setOnItemClickListener(this);
        return view;
    }

    private void addSearchAction() {


        searchVideo.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {

                TextView sr = (TextView) getView().findViewById(R.id.textResult);
                sr.setText("Search Results");

                List<Category> mainMovieList = VideoProvider.getCurrentMovieList();

                for (Category category : mainMovieList) {
                    for (Movie movie : category.getMovieList()) {
                        if (movie.getTitle().toLowerCase().contains(s.toString().toLowerCase())) {
                            searchList.add(movie);
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });

    }

    public void onStart() {
        super.onStart();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
    }


    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Movie movie = adapter.getItem(position);
        Utils.handleMovieClick(getActivity(), movie, null);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onNothingSelected(AdapterView<?> parent) {

    }
}

