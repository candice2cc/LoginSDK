package com.finance.library.utils;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtil {
    public static final String TAG = "NetworkUtil";

    public static final int LOGIN_AUTH = 1;
    public static final int GET_USER_INFO = 2;


    public static void sendAPI(Handler handler, String url, int msgTag){
        HttpThread httpThread = new HttpThread(handler,url,msgTag);
    }

    static class HttpThread extends Thread{
        private Handler handler;
        private String httpUrl;
        private int msgTag;

        public HttpThread(Handler handler, String url, int msgTag) {
            this.handler = handler;
            this.httpUrl = url;
            this.msgTag = msgTag;
        }

        @Override
        public void run() {
            int resCode;
            InputStream in;
            String httpResult = null;
            try {
                URL url = new URL(httpUrl);
                URLConnection urlConnection = url.openConnection();
                HttpsURLConnection httpsConn = (HttpsURLConnection) urlConnection;
                httpsConn.setAllowUserInteraction(false);
                httpsConn.setInstanceFollowRedirects(true);
                httpsConn.setRequestMethod("GET");
                httpsConn.connect();
                resCode = httpsConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    in = httpsConn.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            in, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    in.close();
                    httpResult = sb.toString();
                    Log.i(TAG, httpResult);

                    Message msg = Message.obtain();
                    msg.what = msgTag;
                    Bundle data = new Bundle();
                    data.putString("result", httpResult);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

}
