package com.finance.library.utils;


import com.finance.library.BuildConfig;
import com.finance.library.listener.HttpListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class HttpUtil {
    private final static String TAG = "HttpUtil";
    private static volatile HttpUtil INSTANCE;

    private final OkHttpClient client;


    public static HttpUtil getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpUtil.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpUtil();

                }
            }
        }
        return INSTANCE;
    }

    public HttpUtil() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
    }

    public void post(final String url, FormBody formBody, final HttpListener httpListener) {
        final Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                httpListener.onFailure(e);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                httpListener.onResponse(response.body().string());
            }
        });
    }
}
