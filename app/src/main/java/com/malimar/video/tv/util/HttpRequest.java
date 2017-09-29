package com.malimar.video.tv.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SAARA on 10-01-2017.
 */
public class HttpRequest {

    public static byte[] getPage(String url) throws Exception {
        return getContent(url, "");
    }

    private static byte[] getContent(String httpUrl, String params) throws Exception {
        HttpURLConnection urlConnection = null;
        ByteArrayOutputStream boutStream = new ByteArrayOutputStream();

        try {
            URL url = new URL(httpUrl);
            urlConnection = (HttpURLConnection) url.openConnection();

            if (params != null && params.length() > 0)
                urlConnection.setRequestMethod("POST");

            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            if (params != null && params.length() > 0) {
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(params.getBytes());
                outputStream.flush();
            }

            int responseCode = urlConnection.getResponseCode();

            if (responseCode != 200) {
                throw new IOException("Response code not 200");
            }

            int contentLength = urlConnection.getContentLength();

            int read;
            byte[] buff = new byte[1024];

            InputStream inputStream = urlConnection.getInputStream();

            for (; ; ) {
                read = inputStream.read(buff);
                if (read < 0)
                    break;

                boutStream.write(buff, 0, read);
            }

        } catch (Exception e) {
            throw e;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return boutStream.toByteArray();


    }

}
