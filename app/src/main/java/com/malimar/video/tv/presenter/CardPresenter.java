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

package com.malimar.video.tv.presenter;

import android.content.Context;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.BaseCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.db.PreferenceManager;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.util.RichCardView;

import java.net.URI;

/*
 * A CardPresenter is used to generate Views and bind Objects to them on demand. 
 * It contains an Image CardView
 */


public class CardPresenter extends Presenter {
    private static final String TAG = "CardPresenter";

    private static Context mContext;
    private static int CARD_WIDTH = 500;
    private static int CARD_HEIGHT = 280;

    static class ViewHolder extends Presenter.ViewHolder {
        private Movie mMovie;
        private RichCardView mCardView;
        private static Drawable mDefaultCardImage;


        public ViewHolder(View view) {
            super(view);
            mCardView = (RichCardView) view;

            if (mDefaultCardImage == null)
                mDefaultCardImage = mContext.getResources().getDrawable(R.drawable.malimar_logo);
        }

        public void setMovie(Movie m) {
            mMovie = m;
        }

        public Movie getMovie() {
            return mMovie;
        }


        protected void updateCardViewImage(URI uri) {
            String url = uri == null ? "" : uri.toString();
            try {
                if (!url.startsWith("http")) {
                    Glide.with(mContext.getApplicationContext())
                            .load(R.drawable.speedtest_hdf)
                            .placeholder(R.drawable.malimar_setting)
                            .override(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                                    Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                            .error(mDefaultCardImage)
                            .into(mCardView.getMainImageView());
                } else {

                    mCardView.setMainImage(mDefaultCardImage, false);

                    Glide.with(mContext.getApplicationContext())
                            .load(Utils.getHFDUrl(url)).asBitmap()
                            .override(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                                    Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                            .placeholder(R.drawable.malimar_logo)
                            .error(mDefaultCardImage)
                            .into(mCardView.getMainImageView());


                  /*  Glide.with(mContext)
                            .load(url)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(R.drawable.fntsy_small_app_icon)
                            .override(,
                                    Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                            .error(mDefaultCardImage)
                            .into(mCardView.getMainImageView());*/
                }
            } catch (Exception e) {
            }

        }

        protected void defaultCardViewImage() {
            mCardView.setBackgroundResource(R.drawable.malimar_logo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        Log.d(TAG, "onCreateViewHolder");
        mContext = parent.getContext();

        final RichCardView cardView = new RichCardView(mContext);

        // We should change something when the view is focused: show more information.
        cardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, final boolean isFocused) {
                cardView.setSelected(isFocused);
                updateCardBackgroundColor(cardView, isFocused);
            }
        });

        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mContext.getResources().getColor(R.color.material_blue_grey_950));

        TextView mContentView = (TextView) cardView.findViewById(R.id.content_text);

        if (mContentView != null) {
            mContentView.setMaxLines(2);
            mContentView.setVisibility(View.INVISIBLE);
        }

        return new ViewHolder(cardView);
    }

    private static void updateCardBackgroundColor(RichCardView view, boolean selected) {
        TextView mContentView = (TextView) view.findViewById(R.id.content_text);

        if (selected) {
            view.setInfoAreaBackgroundColor(mContext.getResources().getColor(R.color.detail_bg));

            if (mContentView != null) {
                mContentView.setMaxLines(2);
                mContentView.setVisibility(View.VISIBLE);

                if (mContentView.getText().toString().length() > 0) {
                    View mInfoArea = view.findViewById(R.id.info_field);
                    mInfoArea.getLayoutParams().height = mContext.getResources().getDimensionPixelSize(R.dimen.lb_basic_card_info_height_expanded);
                }
            }
        } else {
            view.setInfoAreaBackgroundColor(mContext.getResources().getColor(R.color.material_blue_grey_950));
            View mInfoArea = view.findViewById(R.id.info_field);
            mInfoArea.getLayoutParams().height = mContext.getResources().getDimensionPixelSize(R.dimen.lb_basic_card_info_height);

            if (mContentView != null) {
                mContentView.setMaxLines(2);
                mContentView.setVisibility(View.INVISIBLE);
            }
        }

        if (view.getTag() != null) {
            Movie movie = (Movie) view.getTag();

            int duration = PreferenceManager.getInstance(mContext).getProgress(movie.getId());
            ProgressBar progressBar = view.getProgressBar();
            progressBar.setProgress(duration);

            if (duration == 0) {
                progressBar.setVisibility(View.INVISIBLE);
            } else {
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }


    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        Movie movie = (Movie) item;
        ((ViewHolder) viewHolder).setMovie(movie);

        Log.d(TAG, "onBindViewHolder");

        if (movie.getCardImageUrl() != null) {
            ((ViewHolder) viewHolder).mCardView.setTitleText(movie.getTitle());
            ((ViewHolder) viewHolder).mCardView.setContentText(movie.getDescription());
            ((ViewHolder) viewHolder).mCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);
            ((ViewHolder) viewHolder).updateCardViewImage(movie.getCardImageURI());
        }

        ((ViewHolder) viewHolder).mCardView.setTag(movie);

        int duration = PreferenceManager.getInstance(mContext).getProgress(movie.getId());
        ProgressBar progressBar = ((ViewHolder) viewHolder).mCardView.getProgressBar();
        progressBar.setProgress(duration);

        if (duration == 0) {
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        Log.d(TAG, "onUnbindViewHolder");

    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TO DO
    }


}
