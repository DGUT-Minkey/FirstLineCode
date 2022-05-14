package com.learn.chapter09.util;

import com.learn.chapter09.HttpCallbackListener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.HttpURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;

//开启线程，使用回调机制(回调方法在调用方写)，避免服务器没有响应就结束,子线程无法返回return
public class HttpUtil {

    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                try {
                    URL url = new URL(address);
                    httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setConnectTimeout(8000);
                    httpURLConnection.setReadTimeout(8000);
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setDoOutput(true);
                    InputStream in = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line =reader.readLine())!=null){
                        response.append(line);
                    }
                    if (listener != null) {
                        // 回调onFinish()方法(调用传入的实现类A的方法)
                        listener.onFInish(response.toString());
                    }
                }catch (Exception e){
                    if (listener != null) {
                        // 回调onError()方法(调用传入的实现类A的方法)
                        listener.onError(e);
                    }
                }finally {
                    if(httpURLConnection !=null){
                        httpURLConnection.disconnect();
                    }
                }
            }

        }).start();
    }

    public static void sendOkHttpRequest(String address,okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                                     .url(address)
                                     .build();
//        enqueue开启子线程执行HTTP请求
        client.newCall(request).enqueue(callback);
    }
}
