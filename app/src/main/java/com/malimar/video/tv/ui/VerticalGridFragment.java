package com.malimar.video.tv.ui;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.malimar.video.tv.PicassoBackgroundManagerTarget;
import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.app.FightNetwork;
import com.malimar.video.tv.data.VideoItemLoader;
import com.malimar.video.tv.data.VideoProvider;
import com.malimar.video.tv.model.Category;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.presenter.CardPresenter;
import com.malimar.video.tv.util.LoaderPopup;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.malimar.video.tv.R.drawable.background_gradient;

/**
 * Created by SAARA on 10-02-2017.
 */

public class VerticalGridFragment extends android.support.v17.leanback.app.VerticalGridFragment {

    private static final String TAG = VerticalGridFragment.class.getSimpleName();
    private static final int NUM_COLUMNS = 3;

    private ArrayObjectAdapter mAdapter;
    private Movie movie;
    private LoaderPopup popup;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        movie = (Movie) getActivity().getIntent()
                .getSerializableExtra(Content.MOVIE);
        setupFragment();
        setupEventListeners();
        setTitle(movie.getTitle());
        FightNetwork.logEvent(getActivity(), "EPISODES_SCREEN");


        /*Glide
                .with(getActivity().getApplicationContext()) // could be an issue!
                .load(movie.getCardImageUrl())
                .into(target);*/

    }



    public void onViewCreated(View v, Bundle bundle) {
        super.onViewCreated(v, bundle);
        new Handler().postDelayed(new Runnable() {
            public void run() {
                popup = new LoaderPopup(getActivity());
                popup.show(getView());
                new FeedLoader(getActivity(), movie.getFeedUrl()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, 100);

    }

/*
    public void onResume() {
        super.onResume();

        if(mAdapter != null) {
            mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
        }
    }
*/

    private void setupFragment() {
        VerticalGridPresenter gridPresenter = new VerticalGridPresenter();
        gridPresenter.setNumberOfColumns(NUM_COLUMNS);
        setGridPresenter(gridPresenter);
        mAdapter = new ArrayObjectAdapter(new CardPresenter());
        setAdapter(mAdapter);
    }

    private void addMovies(List<Movie> movies) {
        for (Movie entry : movies) {
            // String categoryName = entry.getKey();
            mAdapter.add(entry);
        }
        mAdapter.notifyArrayItemRangeChanged(0, movies.size());
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Utils.handleMovieClick(getActivity(), movie, null);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
        }
    }

    private class FeedLoader extends AsyncTask<Void, Void, Void> {
        private final String mUrl;
        private Context mContext;
        private List<Movie> movieList = new ArrayList<>();

        public FeedLoader(Context context, String url) {
            mContext = context;
            mUrl = url;
        }

        @Override
        public Void doInBackground(Void... params) {
            try {
                Log.d("VerticalGridFragment", "doInBackground:::");

                movieList = VideoProvider.buildFeedData(mContext, mUrl, movie.getCategory(), movie.isPaid());
                Log.d("VerticalGridFragment", "doInBackground:::" + movieList.size());
            } catch (Exception e) {
                Log.e(TAG, "Failed to fetch media data", e);
            }

            return null;
        }

        @Override
        public void onPostExecute(Void result) {

            Log.d("VerticalGridFragment", "onPostExecute:::");
            if (popup != null) {
                popup.dismiss();
            }

            for(Movie m : movieList) {
                m.setParentMovie(movie);
            }

            addMovies(movieList);
            Log.d("VerticalGridFragment", "onPostExecute:::" + movieList.size());
        }
    }
}
