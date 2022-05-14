package com.learn.chapter09;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView responseText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        WebView webView = (WebView) findViewById(R.id.web_view);
//        webView.getSettings().setJavaScriptEnabled(true);//设置属性支持脚本
//        webView.setWebViewClient(new WebViewClient());
//        webView.loadUrl("http://www.baidu.com");
        Button sendRequest = (Button) findViewById(R.id.send_request);
        responseText = (TextView) findViewById(R.id.response_text);
        sendRequest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.send_request) {
            sendRequestWithOkHttp();
            Log.d("xu","xx");
        }
    }

    public void sendRequestWithHttpURLConnection(){
//        开启线程发起网络请求 HttpClient方式
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpURLConnection connection = null;
//                BufferedReader reader = null;
//                try {
//                    URL url = new URL("http://www.baidu.com");
//                    connection = (HttpURLConnection) url.openConnection();//传入目标网络地址，创建Http连接
////                    connection.setRequestMethod("GET");//GET获取，Post提交
//                    connection.setRequestMethod("POST");
//                    DataOutputStream out = new DataOutputStream(connection.getOutputStream());
//                    out.writeBytes("username=admin&&password=123456");
//                    connection.setConnectTimeout(8000);//连接超时
//                    connection.setReadTimeout(8000);//读取超时
//                    InputStream in = connection.getInputStream();//获得输入流
////                   下面对获取到的输入流进行读取
//                    reader = new BufferedReader(new InputStreamReader(in));
//                    StringBuilder response = new StringBuilder();
//                    String line;
//                    while((line = reader.readLine())!=null){
//                        response.append(line);
//                    }
//                    showResponse(response.toString());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                } finally{
//                    if(reader!=null){
//                        try {
//                            reader.close();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    if(connection!=null){
//                        connection.disconnect();
//                    }
//                }
//            }
//        }).start();//开启线程
    }

//    使用OkHttp库发送Http请求
    private void sendRequestWithOkHttp(){
//        开启线程发送网络请求
        new Thread(new Runnable() {
            @Override
            public void run() {
//                创建一个OkHttpClient实例
                OkHttpClient okHttpClient=new OkHttpClient();
//                创建Request对象，发起Http请求,可以在build之前丰富这个Request对象
                Request request =new Request.Builder()
                        .url("http://192.168.1.131/get_data.xml")
                        .build();
//                post请求 1.构建resquestBody存放提交参数 2.Request.Builder().post()方法传入RequestBody
//                RequestBody requestBody = new FormBody.Builder()
//                                                      .add("username","xuminjie")
//                                                      .add("password",123456)
//                                                       .build();
//
//                Request request1 = new Request.Builder()
//                                              .post(requestBody)
//                                              .url("http://www.baidu.com")
//                                              .build();
//                调用OkHttpClient的newCall()方法来创建一个Call对象，并调用execute()方法来发送并获取服务器返回数据
                try {
                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
//                    showResponse(responseData);
                    parseXMLWithPull(responseData);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void showResponse(final String response) {

        runOnUiThread(new Runnable() {//Android不允许在子线程中进行UI操作，需要将线程切换到主线程
            @Override
            public void run() {
//                在这里进行UI操作，将结果显示到界面上
                responseText.setText(response);
            }
        });
    }

    //Pull解析XML格式文件数据
    private void parseXMLWithPull(String xmlData){
        try {
//            获取一个XmlPullParserFactory实例
            XmlPullParserFactory factory =XmlPullParserFactory.newInstance();
//            得到XmlpullParser对象
            XmlPullParser xmlPullParser = factory.newPullParser();
//            调用setInput将返回的XML数据设置进去开始解析
            xmlPullParser.setInput(new StringReader(xmlData));
//            getEventType得到当前解析的事件，getName得到当前节点的名字，nextText获得节点内的具体内容
            int evenType = xmlPullParser.getEventType();
            String id="";
            String name="";
            String version="";
            while(evenType !=xmlPullParser.END_DOCUMENT){
                String nodeName =xmlPullParser.getName();
                switch (evenType){
//                    开始解析某个节点
                    case XmlPullParser.START_TAG:{
                        if("id".equals(nodeName)){
                            id=xmlPullParser.nextText();
                        }else if("name".equals(nodeName)){
                            name = xmlPullParser.nextText();
                        }else if("version".equals(nodeName)){
                            version = xmlPullParser.nextText();
                        }
                        break;
                    }
//                    完成解析某个节点
                    case XmlPullParser.END_TAG:{
                        if("app".equals(nodeName)){
                            Log.d("MainActivity","id is"+id);
                            Log.d("MainActivity","name is"+name);
                            Log.d("MainActivity","version is"+version);
                        }
                        break;
                    }
                    default:
                        break;
                }
                evenType =xmlPullParser.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
