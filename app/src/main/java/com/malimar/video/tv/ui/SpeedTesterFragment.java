package com.malimar.video.tv.ui;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.malimar.video.tv.R;
import com.malimar.video.tv.app.FightNetwork;

/**
 * Created by SAARA on 17-02-2017.
 */

public class SpeedTesterFragment extends Fragment {

    private ProgressWheel progressBar;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        View view = inflater.inflate(R.layout.speed_tester_fragment, container, false);
        init(view);
        return view;
    }

    private void init(View v) {
        WebView webView = (WebView) v.findViewById(R.id.webView);
        ImageButton settingIcon = (ImageButton) v.findViewById(R.id.settingIcon);
        settingIcon.setVisibility(View.INVISIBLE);
        progressBar = (ProgressWheel) v.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.spin();
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new myWebClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://fast.com/");
        FightNetwork.logEvent(getActivity(), "SPEEDTEST_SCREEN");
    }

    public class myWebClient extends WebViewClient {
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.i("xx page start url :", url);
            progressBar.setVisibility(View.VISIBLE);
            progressBar.spin();
        }


        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }


        public void onPageFinished(WebView view, String url) {
            Log.i("xx page start url :", url);

            progressBar.stopSpinning();
            progressBar.setVisibility(View.GONE);

        }

        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            progressBar.stopSpinning();
            progressBar.setVisibility(View.GONE);

        }
    }
}
