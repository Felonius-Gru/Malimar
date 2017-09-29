package com.malimar.video.tv.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SAARA on 01-03-2017.
 */

public class UserBean {
    public List<Movie> getFavMovieList() {
        return favMovieList;
    }

    public void setFavMovieList(List<Movie> favMovieList) {
        this.favMovieList = favMovieList;
    }

    private List<Movie> favMovieList = new ArrayList<>();

    public void addMovie(Movie movie) {
        for(Movie m : favMovieList) {
            if(m.getId().length() > 0) {
                if(m.getId().equalsIgnoreCase(movie.getId())) {
                    return;
                }
            }
            else {
                if(m.getTitle().equalsIgnoreCase(movie.getTitle())) {
                   return;
                }
            }
        }

        favMovieList.add(movie);
    }

    public boolean isFavMovie(Movie movie) {
        for(Movie m : favMovieList) {
            if(m.getId().length() > 0) {
                if(m.getId().equalsIgnoreCase(movie.getId())) {
                    return true;
                }
            }
            else {
                if(m.getTitle().equalsIgnoreCase(movie.getTitle())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void removeMovie(Movie movie) {
        for(Movie m : favMovieList) {

            if(m.getId().length() > 0) {
                if(m.getId().equalsIgnoreCase(movie.getId())) {
                    favMovieList.remove(m);
                    return;
                }
            }
            else {
                if(m.getTitle().equalsIgnoreCase(movie.getTitle())) {
                    favMovieList.remove(m);
                    return;
                }
            }
        }
    }
}
