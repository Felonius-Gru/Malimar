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

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import com.malimar.video.tv.model.Category;

import java.util.ArrayList;
import java.util.List;

/*
 * This class asynchronously loads videos from a backend
 */
public class VideoItemLoader extends AsyncTaskLoader<List<Category>> {

    private static final String TAG = "VideoItemLoader";
    private final String mUrl;
    private Context mContext;

    public VideoItemLoader(Context context, String url) {
        super(context);
        mContext = context;
        mUrl = url;
    }

    @Override
    public List<Category> loadInBackground() {
        try {
            return VideoProvider.buildMedia(mContext, mUrl);
        } catch (Exception e) {
            Log.e(TAG, "Failed to fetch media data", e);
            return new ArrayList<>();
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Handles a request to stop the Loader.
     */
    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

}
