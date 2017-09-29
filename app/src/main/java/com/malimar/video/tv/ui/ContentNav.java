package com.malimar.video.tv.ui;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.DetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v4.app.ActivityOptionsCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.data.VideoProvider;
import com.malimar.video.tv.db.SessionData;
import com.malimar.video.tv.db.UserBeanProvider;
import com.malimar.video.tv.exoplayer.PlayerActivity;
import com.malimar.video.tv.model.Category;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.model.UserBean;
import com.malimar.video.tv.presenter.CardPresenter;
import com.malimar.video.tv.presenter.DetailsDescriptionPresenter;
import com.malimar.video.tv.util.Constant;
import com.malimar.video.tv.util.HttpRequest;
import com.malimar.video.tv.util.LoaderPopup;
import com.malimar.video.tv.util.RichCardView;
import com.malimar.video.tv.util.XMLParser;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ContentNav extends DetailsFragment {
    private static final String TAG = "DetailsFragment";

    private static final int ACTION_WATCH_NOW = 1;
    private static final int ACTION_GO_BACK = 2;
    private static final int ACTION_MARK_FAV = 3;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NO_NOTIFICATION = -1;

    private Movie mSelectedMovie;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;

    private BackgroundManager mBackgroundManager;
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private SessionData sessionData;


    private Action favAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);
        prepareBackgroundManager();
        mSelectedMovie = (Movie) getActivity().getIntent()
                .getSerializableExtra(Content.MOVIE);
        if (null != mSelectedMovie) {
            Log.d(TAG, "DetailsActivity movie: " + mSelectedMovie.toString());
            removeNotification(getActivity().getIntent()
                    .getIntExtra(Content.NOTIFICATION_ID, NO_NOTIFICATION));
            setupAdapter();
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();
            setupMovieListRow();
            setupMovieListRowPresenter();
            setOnItemViewClickedListener(new ItemViewClickedListener());
            sessionData = SessionData.getInstance();
        }
    }

    private void removeNotification(int notificationId) {
        if (notificationId != NO_NOTIFICATION) {
            NotificationManager notificationManager = (NotificationManager) getActivity()
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(notificationId);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    /*
     * Check if there is a global search intent
     */

    private void prepareBackgroundManager() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = getResources().getDrawable(R.drawable.background_gradient);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupAdapter() {
        mPresenterSelector = new ClassPresenterSelector();
        mAdapter = new ArrayObjectAdapter(mPresenterSelector);
        setAdapter(mAdapter);
    }

    private void setupDetailsOverviewRow() {

        Log.d(TAG, "doInBackground: " + mSelectedMovie.toString());
        final DetailsOverviewRow row = new DetailsOverviewRow(mSelectedMovie);
        row.setImageDrawable(getResources().getDrawable(R.drawable.malimar_logo));
        int width = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = Utils.convertDpToPixel(getActivity()
                .getApplicationContext(), DETAIL_THUMB_HEIGHT);

        Glide.with(getActivity().getApplicationContext())
                .load(Utils.getHFDUrl(mSelectedMovie.getCardImageUrl())).diskCacheStrategy(DiskCacheStrategy.ALL)
                .fitCenter()
                .error(R.drawable.malimar_logo)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        Log.d(TAG, "details overview card image url ready: " + resource);
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());

                    }
                });

        Glide.with(getActivity().getApplicationContext())
                .load(Utils.getHFDUrl(mSelectedMovie.getCardImageUrl())).diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.malimar_logo)
                .into(new SimpleTarget<GlideDrawable>(mMetrics.widthPixels, mMetrics.heightPixels) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });

        row.addAction(new Action(ACTION_WATCH_NOW, getActivity().getResources().getString(R.string.WatchNow), ""));
        row.addAction(new Action(ACTION_GO_BACK, getActivity().getResources().getString(R.string.GoBack), ""));
        favAction = new Action(ACTION_MARK_FAV, getActivity().getResources().getString(R.string.favorite), "");

        UserBean userBean = SessionData.getInstance().getUserBean();

        if (userBean.isFavMovie(getSelectedMovie())) {
            favAction.setLabel1(getActivity().getResources().getString(R.string.unfavorite));
        } else {
            favAction.setLabel1(getActivity().getResources().getString(R.string.favorite));
        }

        row.addAction(favAction);
        mAdapter.add(row);

    }

    private void updateAction() {
        UserBean userBean = SessionData.getInstance().getUserBean();
        if (userBean.isFavMovie(getSelectedMovie())) {
            favAction.setLabel1(getActivity().getResources().getString(R.string.unfavorite));
        } else {
            favAction.setLabel1(getActivity().getResources().getString(R.string.favorite));
        }

        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
    }


    private void setupDetailsOverviewRowPresenter() {
        // Set detail background and style.
        DetailsOverviewRowPresenter detailsPresenter =
                new DetailsOverviewRowPresenter(new DetailsDescriptionPresenter());


        detailsPresenter.setBackgroundColor(getResources().getColor(R.color.detail_dark));
        detailsPresenter.setStyleLarge(true);

        // Hook up transition element.
        detailsPresenter.setSharedElementEnterTransition(getActivity(),
                Content.SHARED_ELEMENT_NAME);

        detailsPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            @Override
            public void onActionClicked(Action action) {
                if (action.getId() == ACTION_WATCH_NOW) {
                    processWatchNow();
                } else if (action.getId() == ACTION_GO_BACK) {
                    getActivity().finishAfterTransition();
                } else if (action.getId() == ACTION_MARK_FAV) {
                    UserBean userBean = SessionData.getInstance().getUserBean();

                    if (userBean.isFavMovie(getSelectedMovie())) {
                        userBean.removeMovie(getSelectedMovie());
                    } else {
                        userBean.addMovie(getSelectedMovie());
                    }

                    SessionData.getInstance().save(getActivity());
                    updateAction();

                } else {
                    Toast.makeText(getActivity(), action.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private Movie getSelectedMovie() {
        Movie m = mSelectedMovie.getParentMovie() != null ? mSelectedMovie.getParentMovie() : mSelectedMovie;
        return m;
    }

    private void setupMovieListRow() {
        String subcategories[] = {getString(R.string.related_movies), ""};
        List<Category> movies = VideoProvider.getCurrentMovieList();

        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new CardPresenter());
        List<Movie> list = null;
        for (Category category : movies) {

            if (mSelectedMovie.getCategory().equalsIgnoreCase(category.getTitle())) {
                list = category.getMovieList();

                for (int j = 0; j < list.size(); j++) {
                    if (!list.get(j).getId().equals(
                            mSelectedMovie.getId())) {
                        listRowAdapter.add(list.get(j));
                    }
                }
            }
        }

        if (list != null) {
            HeaderItem header = null;
            if (list.size() == 1) {
                header = new HeaderItem(0, subcategories[1], null);
            } else {
                header = new HeaderItem(0, subcategories[0], null);
            }

            mAdapter.add(new ListRow(header, listRowAdapter));
        }
    }

    private void setupMovieListRowPresenter() {
        mPresenterSelector.addClassPresenter(ListRow.class, new ListRowPresenter());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((RichCardView) itemViewHolder.view).getMainImageView(),
                        Content.SHARED_ELEMENT_NAME).toBundle();
                Log.d(TAG, "Item: " + item.toString());
                Utils.handleMovieClick(getActivity(), movie, null);
            }
        }
    }

    private LoaderPopup popup;

    private void processWatchNow() {
        popup = new LoaderPopup(getActivity());
        popup.show(getView());
        new VideoUrlTask().execute();
    }

    private class VideoUrlTask extends AsyncTask<Void, Void, Void> {
        boolean authorized;
        boolean success;

        private boolean processData() {

            String urlString = mSelectedMovie.getVideoUrl();

            if (urlString.startsWith("https://goo.gl/")) {
                //need to check the redirect one
                try {
                    URL url = new URL(urlString);
                    HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
                    ucon.setInstanceFollowRedirects(false);
                    urlString = ucon.getHeaderField("Location");
                } catch (Exception e) {

                }
            }

            NodeList tokenNode = sessionData.getConfigDoc().getElementsByTagName("tokens");
            XMLParser parser = new XMLParser();
            String url = urlString;

            String token = mSelectedMovie.getToken();

            if (token != null && token.length() > 0) {
                for (int i = 0; i < tokenNode.item(0).getChildNodes().getLength(); i++) {
                    if (tokenNode.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase(token)) {
                        url = parser.getElementValue(tokenNode.item(0).getChildNodes().item(i)) + "?url=" + urlString;
                        break;
                    }
                }
            }
            try {
                if (urlString.indexOf("vimeo") != -1) {
                    mSelectedMovie.setFinalVideoUrl(urlString);
                } else {
                    byte[] mRespData = HttpRequest.getPage(url);
                    JSONObject jsonObject = new JSONObject(new String(mRespData));
                    mSelectedMovie.setFinalVideoUrl(jsonObject.getString("url"));
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }

        private boolean authorizationProcess() {
            String url = "";
            XMLParser parser = null;
            if (mSelectedMovie.getmSub().equalsIgnoreCase("ad")) {
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
            } else {
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
                Log.e("ContentNav", "URL:::" + url);
            }

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


        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (!mSelectedMovie.isPaid()) {
                    authorized = true;
                    success = processData();
                } else {
                    authorized = authorizationProcess();
                    if (authorized) {
                        success = processData();
                    }
                }
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (popup != null) {
                popup.dismiss();
            }

            if (!authorized) {
                Intent intent = new Intent(getActivity(), CustomerSettingActivity.class);
                intent.putExtra(Content.MOVIE, mSelectedMovie);
                intent.putExtra("isError", false);
                getActivity().startActivity(intent);
                return;
            }

            if (!success) {
                Utils.commonAlert(getResources().getString(R.string.problem_occurred), getActivity(), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                return;
            }


            Log.d("CONTENTNAV:", "FinalUrl::" + mSelectedMovie.getFinalVideoUrl());

            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.setAction(PlayerActivity.ACTION_VIEW);
            intent.setData(Uri.parse(mSelectedMovie.getFinalVideoUrl()));
            intent.putExtra(Content.MOVIE, mSelectedMovie);
            getActivity().startActivity(intent);
        }
    }
}
