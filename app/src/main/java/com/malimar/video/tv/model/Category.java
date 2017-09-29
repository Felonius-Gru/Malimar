package com.malimar.video.tv.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAARA on 08-02-2017.
 */

public class Category implements Serializable {
    private String title = "";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle1() {
        return title1;
    }

    public void setTitle1(String title1) {
        this.title1 = title1;
    }

    public String getFeedUrl() {
        return feedUrl;
    }

    public void setFeedUrl(String feedUrl) {
        this.feedUrl = feedUrl;
    }

    private String title1 = "";
    private String feedUrl = "";

    public List<Movie> getMovieList() {
        return movieList;
    }

    private List<Movie> movieList = new ArrayList();

    public void addMovie(Movie movie) {
        movieList.add(movie);
    }
}
