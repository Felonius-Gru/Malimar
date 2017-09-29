package com.malimar.video.tv.util;

/**
 * Created by SAARA on 10-01-2017.
 */
public interface Constant {
    String[] MAIN_URLS = {"https://malimartv-west.s3.amazonaws.com/firetv/config.xml", "https://malimartv-east.s3.amazonaws.com/firetv/config.xml"};
    long REFRESH_TIME = 30 * 60 * 1000;
    String SPlASH_URL = "http://images.malimar.tv.s3.amazonaws.com/firetv/splashscreen/splash_hd.jpg";
    String SPEED_TEST_IMAGE = "http://images.malimar.tv.s3.amazonaws.com/speedtestHDF.jpg";
    String SERIAL_NUMBER_IMAGE = "http://images.malimar.tv.s3.amazonaws.com/serialnumberHDF.jpg";

}
