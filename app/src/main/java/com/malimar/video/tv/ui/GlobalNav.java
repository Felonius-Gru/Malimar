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

package com.malimar.video.tv.ui;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.PresenterSelector;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.malimar.video.tv.PicassoBackgroundManagerTarget;
import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.app.FightNetwork;
import com.malimar.video.tv.data.VideoItemLoader;
import com.malimar.video.tv.data.VideoProvider;
import com.malimar.video.tv.db.SessionData;
import com.malimar.video.tv.db.UserBeanProvider;
import com.malimar.video.tv.model.Category;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.model.UserBean;
import com.malimar.video.tv.presenter.CardPresenter;
import com.malimar.video.tv.presenter.CustomHeaderItemPresenter;
import com.malimar.video.tv.util.Constant;
import com.malimar.video.tv.util.CustomHeaderItem;
import com.malimar.video.tv.util.LoaderPopup;
import com.malimar.video.tv.util.RichCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class GlobalNav extends BrowseFragment implements
        LoaderManager.LoaderCallbacks<List<Category>> {
    private static final String TAG = "GlobalNav";

    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private PicassoBackgroundManagerTarget mBackgroundTarget;
    private DisplayMetrics mMetrics;

    private LoaderPopup popup;
    private boolean home;
    private List<Category> movieList = new ArrayList<>();
    private long lastRefreshTime = System.currentTimeMillis();

    private ArrayObjectAdapter favListRowAdapter = null;
    private boolean refreshing;


    private Handler mHandler = new Handler() {
        public void handleMessage(Message m) {
            switch (m.what) {
                case 0: {
                    loadVideos();
                    Log.d(TAG, "mHandler_0");
                }
                break;

                case 1: {
                    refreshVideos();
                    Log.d(TAG, "mHandler_1");
                }
                break;
            }

        }
    };


    public void onActivityCreated(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);


        if (getActivity().getIntent().hasExtra("home")) {
            home = getActivity().getIntent().getBooleanExtra("home", false);
        }

        prepareBackgroundManager();
        setupUIElements();
        setupEventListeners();

        if (home) {
            FightNetwork.logEvent(getActivity(), "HOME_SCREEN");
        } else {
            FightNetwork.logEvent(getActivity(), "GRID_SCREEN");
        }

        lastRefreshTime = System.currentTimeMillis();
        mHandler.sendEmptyMessageDelayed(0, 1000);
        Log.d("GlobalNav", "onActivityCreated()");
    }

    private void loadVideos() {
        refreshing = true;
        popup = new LoaderPopup(getActivity());
        popup.show(getView());
        loadVideoData();
        Log.d("GlobalNav", "loadVideos()");
    }

    private void refreshVideos() {
        if (!home) {
            return;
        }

        if (System.currentTimeMillis() - lastRefreshTime >= Constant.REFRESH_TIME) {
            lastRefreshTime = System.currentTimeMillis();
            loadVideos();
            Log.d(TAG, "refreshVideos()-IF part");
        } else {
            mHandler.removeMessages(1);
            mHandler.sendEmptyMessageDelayed(1, 60 * 1000);
            Log.d(TAG, "refreshVideos()-Else part");
        }
        Log.d(TAG, "refreshVideos()");
    }

    private void loadVideoData() {
        VideoProvider.setContext(getActivity());
        getLoaderManager().restartLoader(0, null, this);
        Log.d(TAG, "loadVideoData()");
    }

    private void setupEventListeners() {
        //  setOnSearchClickedListener(onclick);
        setOnItemViewClickedListener(new ItemViewClickedListener());
      /*  setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            }
        });*/
    }

    public void onPause() {
        super.onPause();
        mHandler.removeMessages(1);
        Log.d(TAG, "onPause()");
    }

    public void onResume() {
        super.onResume();
        VideoProvider.setCurrentMovieList(movieList);
        Log.d(TAG, "onResume()");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                refreshVideos();
                Log.d(TAG, "onResume()-run()");
                if (favListRowAdapter != null) {
                    UserBean userBean = SessionData.getInstance().getUserBean();
                    favListRowAdapter.clear();
                    favListRowAdapter.addAll(0, userBean.getFavMovieList());
                    favListRowAdapter.notifyArrayItemRangeChanged(0, favListRowAdapter.size());
                }
            }
        }, 1000);
    }

    @Override
    public Loader<List<Category>> onCreateLoader(int arg0, Bundle arg1) {
        Log.d(TAG, "VideoItemLoader created ");
        Log.d(TAG, "onCreateLoader()");
        return new VideoItemLoader(getActivity(), ((Launcher) getActivity()).getURL());
    }

    public void onLoadFinished(Loader<List<Category>> arg0,
                               List<Category> data) {
        Log.d(TAG, "onLoadFinished()");
        if (popup != null) {
            popup.dismiss();
            popup = null;
        }

        if (!refreshing)
            return;

        refreshing = false;

        movieList.clear();
        movieList.addAll(data);

        VideoProvider.setCurrentMovieList(data);
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        int i = 0;
        if (home) {
            UserBean userBean = SessionData.getInstance().getUserBean();
            CustomHeaderItem header = new CustomHeaderItem(i, getActivity().getResources().getString(R.string.myfavorite),
                    userBean.getFavMovieList().size(), getActivity().getResources().getString(R.string.myfavorite));
            favListRowAdapter = new ArrayObjectAdapter(cardPresenter);

            favListRowAdapter.addAll(0, userBean.getFavMovieList());
            mRowsAdapter.add(new ListRow(header, favListRowAdapter));
            Log.d(TAG, "onLoadFinished()-home-If Part");
        }
        Log.d(TAG, "onLoadFinished()-home-Else Part");

        for (Category show : data) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            List<Movie> list = show.getMovieList();
            if (list == null || list.size() == 0) {
                continue;
            }

            for (int j = 0; j < list.size(); j++) {
                listRowAdapter.add(list.get(j));
            }

            CustomHeaderItem header = new CustomHeaderItem(i, show.getTitle(), list.size(), show.getTitle() + "  " + show.getTitle1());
            i++;

            mRowsAdapter.add(new ListRow(header, listRowAdapter));

        }

        if (home) {

            CustomHeaderItem header = new CustomHeaderItem(i, getActivity().getResources().getString(R.string.settings), 1, getActivity().getResources().getString(R.string.settings));
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            Movie movie = new Movie();
            movie.setId(getActivity().getResources().getString(R.string.settings));
            movie.setCardImageUrl(Constant.SPEED_TEST_IMAGE);
            movie.setTitle(getActivity().getResources().getString(R.string.speedTest));
            listRowAdapter.add(movie);


            Movie movie1 = new Movie();
            movie1.setId("serial");
            movie1.setCardImageUrl(Constant.SERIAL_NUMBER_IMAGE);
            movie1.setTitle(getActivity().getResources().getString(R.string.your_serial_number) + "\n" + Utils.getSerialNumber());
            listRowAdapter.add(movie1);

            mRowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        setAdapter(mRowsAdapter);

        lastRefreshTime = System.currentTimeMillis();
        refreshVideos();
    }

    @Override
    public void onLoaderReset(Loader<List<Category>> arg0) {
        Log.d(TAG, "onLoaderReset()");
        mRowsAdapter.clear();
    }

    private void prepareBackgroundManager() {
        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);
        mDefaultBackground = getResources().getDrawable(R.drawable.background_gradient);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
        updateBackground(R.drawable.background_gradient);
        Log.d(TAG, "prepareBackgroundManager()");
    }

    private void setupUIElements() {
      /*  setBadgeDrawable(getActivity().getResources().getDrawable(
                R.drawable.ic_launcher));*/
        setHeadersState(HEADERS_DISABLED);

        setHeadersTransitionOnBackEnabled(true);
        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.default_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));

        setHeaderPresenterSelector(new PresenterSelector() {
            @Override
            public Presenter getPresenter(Object o) {
                return new CustomHeaderItemPresenter();
            }
        });
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                Movie movie = (Movie) item;

                if (movie.getId().equalsIgnoreCase("Settings")) {
                    Intent intent = new Intent(getActivity(), SpeedTesterActivity.class);
                    getActivity().startActivity(intent);
                } else if (movie.getId().equalsIgnoreCase("serial")) {

                } else {
                    Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            getActivity(),
                            ((RichCardView) itemViewHolder.view).getMainImageView(),
                            Content.SHARED_ELEMENT_NAME).toBundle();
                    Utils.handleMovieClick(getActivity(), movie, bundle);
                }

            } else if (item instanceof String) {
                if (((String) item).indexOf(getString(R.string.error_fragment)) >= 0) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT)
                            .show();
                }
            }

        }
    }

    protected void updateBackground(int resourceID) {
        Picasso.with(getActivity())
                .load(resourceID)
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .error(mDefaultBackground)
                .into(mBackgroundTarget);

    }


}

