/*
 * Copyright (C) 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.malimar.video.tv.data;

import android.content.Context;
import android.util.Log;

import com.malimar.video.tv.Utils;
import com.malimar.video.tv.db.SessionData;
import com.malimar.video.tv.model.Category;
import com.malimar.video.tv.model.IntroData;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.ui.Content;
import com.malimar.video.tv.util.XMLParser;

import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

/*
 * This class loads videos from a backend and saves them into a HashMap
 */
public class VideoProvider {

    private static final String TAG = "VideoProvider";
    private static final String KEY_ITEM = "item";
    private static final String TITLE = "title";
    private static final String TITLE_L = "titlel";
    private static final String FEED = "feed";
    private static final String MEDIA = "media";
    private static SessionData sessionData;


    private static Context sContext;

    public static void setContext(Context context) {
        if (sContext == null)
            sContext = context;
    }

    public static List<Category> getCurrentMovieList() {
        return currentMovieList;
    }

    public static void setCurrentMovieList(List<Category> currentMovieList) {
        VideoProvider.currentMovieList = currentMovieList;
    }

    private static List<Category> currentMovieList = new ArrayList<>();

    public static List<Movie> buildFeedData(Context ctx, String urlFeed, String category, boolean paid) throws JSONException {

        List<Movie> fMovieList = new ArrayList<>();
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(urlFeed); // getting XML
        sessionData = SessionData.getInstance();
        if (xml == null) {
            return fMovieList;
        }

        Document doc = parser.getDomElement(xml); // getting DOM element
        NodeList movieList = doc.getElementsByTagName(KEY_ITEM); //.item(0).getChildNodes();
        for (int j = 0; j < movieList.getLength(); j++) {
            Element childElement = (Element) movieList.item(j);
            Movie movie = new Movie();
            movie.setCategory(category);
            movie.setCardImageUrl((childElement.getAttribute("hdImg")));
            movie.setTitle(parser.getValue(childElement, TITLE));

            String idleTime = parser.getValue(childElement, "idleTimeout");

            if (idleTime.length() > 0) {
                int value = Utils.getIntValue(idleTime);

                if (value > 0) {
                    movie.setIdleTimeout(value);
                }
            }

            movie.setId(parser.getValue(childElement, "id"));
            movie.setDuration(Utils.getIntValue(parser.getValue(childElement, "length")));
            movie.setLive(parser.getValue(childElement, "live").equalsIgnoreCase("true"));
            movie.setFeedUrl(parser.getValue(childElement, "feed"));
            movie.setAuthorizationURL((parser.getValue(childElement, "authorizationURL")));
            movie.setToken((parser.getValue(childElement, "MTOKEN")));
            movie.setmSub(parser.getValue(childElement, "MSUB"));
            movie.setPaid(!(parser.getValue(childElement, "MSUB").equalsIgnoreCase("FR")));
            NodeList feedNode = childElement.getElementsByTagName("feed");

            for (int l = 0; l < feedNode.getLength(); l++) {
                Element feedElement = (Element) feedNode.item(l);
                movie.setType((feedElement.getAttribute("type")));
            }
            movie.setDescription(parser.getValue(childElement, "description"));
            NodeList mediaData = childElement.getElementsByTagName(MEDIA);

            for (int k = 0; k < mediaData.getLength(); k++) {
                Element mediaElement = (Element) mediaData.item(k);
                movie.setVideoUrl(parser.getValue(mediaElement, "streamUrl"));
            }

            if (sessionData.isPremiumVideo() && movie.getmSub().equalsIgnoreCase("PR")) {
                fMovieList.add(movie);
            } else if (sessionData.isAdultVideo() && movie.getmSub().equalsIgnoreCase("AD")) {
                fMovieList.add(movie);
            } else if (movie.getmSub().equalsIgnoreCase("FR")) {
                fMovieList.add(movie);
            }

            //fMovieList.add(movie);

        }
        return fMovieList;

    }


    public static List<Category> buildMedia(Context ctx, String url)
            throws JSONException {
        List<Category> sMovieList = new ArrayList<>();
        XMLParser parser = new XMLParser();
        String xml = parser.getXmlFromUrl(url); // getting XML
        Document doc = parser.getDomElement(xml); // getting DOM element
        NodeList nl = doc.getElementsByTagName(KEY_ITEM); //.item(0).getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
            Element e = (Element) nl.item(i);
            Category category = new Category();
            category.setTitle(parser.getValue(e, TITLE));
            category.setTitle1(parser.getValue(e, TITLE_L));
            category.setFeedUrl(parser.getValue(e, FEED));
            category.getMovieList().addAll(buildFeedData(ctx, category.getFeedUrl(), category.getTitle(), parser.getValue(e, "authorizationURL").length() > 0));
            sMovieList.add(category);
        }
        return sMovieList;
    }


}
