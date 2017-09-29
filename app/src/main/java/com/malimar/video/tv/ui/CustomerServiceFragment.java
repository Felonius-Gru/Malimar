package com.malimar.video.tv.ui;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.malimar.video.tv.R;
import com.malimar.video.tv.Utils;
import com.malimar.video.tv.db.SessionData;
import com.malimar.video.tv.model.Movie;
import com.malimar.video.tv.util.CoverFlowAdapter;
import com.malimar.video.tv.util.XMLParser;
import com.sample.amazon.asbuilibrary.list.CarouselView;

import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

/**
 * Created by SAARA on 17-02-2017.
 */

public class CustomerServiceFragment extends android.app.Fragment {

    private Movie mSelectedMovie;
    private boolean isError;
    private Document configDocument;


    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message m) {
            if (m.what == 0) {
                new AuthorizedTask().execute();
            }
        }
    };

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle
                                     savedInstanceState) {

        View view = inflater.inflate(R.layout.customer_setting_activity, container, false);
        mSelectedMovie = (Movie) getActivity().getIntent().getSerializableExtra(Content.MOVIE);


        isError = getActivity().getIntent().getExtras().getBoolean("isError");

        init(view);

        if (!isError) {
            view.findViewById(R.id.mainlay).setVisibility(View.VISIBLE);
            view.findViewById(R.id.error).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.mainlay).setVisibility(View.GONE);
            view.findViewById(R.id.error).setVisibility(View.VISIBLE);
        }

        return view;
    }

    public void onPause() {
        super.onPause();
        handler.removeMessages(0);
    }

    private void init(View v) {
        configDocument = SessionData.getInstance().getConfigDoc();
        XMLParser xmlParser = new XMLParser();
        TextView serialNumber = (TextView) v.findViewById(R.id.SerialNumber);
        TextView title_1 = (TextView) v.findViewById(R.id.title_1);
        TextView title_2 = (TextView) v.findViewById(R.id.title_2);

        if (mSelectedMovie != null && isError) {
            NodeList nodeList = configDocument.getElementsByTagName("video");
            String errorHeader = "";
            String errorMessage = ""; //xmlParser.getElementValue(nodeList.item(0).getChildNodes().item(1));

            for (int i = 0; i < nodeList.item(0).getChildNodes().getLength(); i++) {
                if (nodeList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("header")) {
                    errorHeader = xmlParser.getElementValue(nodeList.item(0).getChildNodes().item(i));
                } else if (nodeList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("message")) {
                    errorMessage = xmlParser.getElementValue(nodeList.item(0).getChildNodes().item(i));
                }
            }

            TextView error = (TextView) v.findViewById(R.id.errorHeader);
            TextView errorMsg = (TextView) v.findViewById(R.id.errorMsg);
            error.setText(errorHeader);
            errorMsg.setText(errorMessage);
        }

        if (mSelectedMovie.getmSub().equalsIgnoreCase("ad")) {
            String errorHeaderAd = "";
            String errorMessageAd = "";
            NodeList adList = configDocument.getElementsByTagName("AD");
            for (int i = 0; i < adList.item(0).getChildNodes().getLength(); i++) {
                if (adList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("header")) {
                    errorHeaderAd = xmlParser.getElementValue(adList.item(0).getChildNodes().item(i));
                } else if (adList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("message")) {
                    errorMessageAd = xmlParser.getElementValue(adList.item(0).getChildNodes().item(i));
                }
            }
            title_1.setText(errorHeaderAd);
            title_2.setText(errorMessageAd);
        } else {
            NodeList prList = configDocument.getElementsByTagName("PR");
            String errorHeaderPr = "";
            String errorMessagePr = "";
            for (int i = 0; i < prList.item(0).getChildNodes().getLength(); i++) {
                if (prList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("header")) {
                    errorHeaderPr = xmlParser.getElementValue(prList.item(0).getChildNodes().item(i));
                } else if (prList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("message")) {
                    errorMessagePr = xmlParser.getElementValue(prList.item(0).getChildNodes().item(i));
                }
            }
            title_1.setText(errorHeaderPr);
            title_2.setText(errorMessagePr);
        }
        serialNumber.setText(Utils.getSerialNumber());
        ImageButton settingIcon = (ImageButton) v.findViewById(R.id.settingIcon);
        settingIcon.setVisibility(View.INVISIBLE);
        Button closeAction = (Button) v.findViewById(R.id.closeButton);
        closeAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        if (!isError) {
            new AuthorizedTask().execute();
        }
    }

    private class AuthorizedTask extends AsyncTask<Void, Void, Void> {
        boolean authorized;

        public AuthorizedTask() {
        }


        protected void onPostExecute(Void result) {

            if (authorized) {
                getActivity().finish();
            } else {
                handler.sendEmptyMessageDelayed(0, 10000);
            }
        }


        protected Void doInBackground(Void... args) {
            try {
                authorized = authorizationProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    private boolean authorizationProcess() {
        XMLParser parserSerialNumber;
        String url;
        if (mSelectedMovie.getmSub().equalsIgnoreCase("ad")) {
            NodeList adList = configDocument.getElementsByTagName("AD");
            parserSerialNumber = new XMLParser();
            String adultUrl = "";
            for (int i = 0; i < adList.item(0).getChildNodes().getLength(); i++) {
                if (adList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("url")) {
                    adultUrl = parserSerialNumber.getElementValue(adList.item(0).getChildNodes().item(i));
                }
            }
            url = adultUrl + Utils.getSerialNumber();
        } else {
            NodeList prList = configDocument.getElementsByTagName("PR");
            parserSerialNumber = new XMLParser();
            String premiumUrl = "";
            for (int i = 0; i < prList.item(0).getChildNodes().getLength(); i++) {
                if (prList.item(0).getChildNodes().item(i).getNodeName().equalsIgnoreCase("url")) {
                    premiumUrl = parserSerialNumber.getElementValue(prList.item(0).getChildNodes().item(i));
                }
            }
            url = premiumUrl + Utils.getSerialNumber();
        }

        url = mSelectedMovie.getAuthorizationURL() + Utils.getSerialNumber();
        XMLParser parser = new XMLParser();
        Log.d("url::", url);
        String xml = parser.getXmlFromUrl(url); // getting XML

        Log.d("xml::", xml == null ? "null" : xml);

        if (xml == null) {
            return false;
        }

        if (xml.length() == 0) {
            return true;
        }

        Document doc = parser.getDomElement(xml);
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

}
