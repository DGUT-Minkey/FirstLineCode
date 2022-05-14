package com.learn.chapter05;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.learn.chapter05.broadcastbestpractice.util.BaseActivity;

public class MainActivity extends BaseActivity {
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private LocalReceiver localReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intentFilter = new IntentFilter();
//        想听什么样的广播就添加相应的action
//        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");//当网络状态发生变化时，系统发出的正是一条值为android.net.conn.CONNECTIVITY_CHANGE的广播
//        networkChangeReceiver = new NetworkChangeReceiver();
//        注册NetworkChangeRecelver 就会收到所有值为 android.net.conn.CONNECTIVITYCHANGE 的广播
//        registerReceiver(networkChangeReceiver,intentFilter);
//        System.out.println("Boot is CompLeted");

//        Button button =(Button)findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent("com.learn.chapter05.broadcasttest.MY_BROADCAST");//发送一条广播com.learn.chapter05.broadcasttest.MY_BROADCAST
////标准广播
////                sendBroadcast(intent);
////有序广播
//                sendOrderedBroadcast(intent,null);
//            }
//        });

//        获取本地广播实例
        localBroadcastManager = LocalBroadcastManager.getInstance(this);//this=context
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.learn.chapter05.broadcasttest.LOCAL_BROADCAST");
//                发送本地广播
                localBroadcastManager.sendBroadcast(intent);
            }
        });
        intentFilter.addAction("com.learn.chapter05.broadcasttest.LOCAL_BROADCAST");
        localReceiver = new LocalReceiver();
//        注册本地广播监听器
        localBroadcastManager.registerReceiver(localReceiver,intentFilter);

//        强制下线
        Button forceOffline = (Button) findViewById(R.id.force_offline);
        forceOffline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent("com.learn.broadcastbestpractice.FORCE_OFFLINE");
                sendBroadcast(intent);
            }
        });

    }

//    动态注册的广播接收器一定都要取消注册
    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(localReceiver);
        unregisterReceiver(networkChangeReceiver);
    }

    class NetworkChangeReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);//系统服务类，专门管理网络连接
            NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
            if(networkInfo != null && networkInfo.isAvailable()){
                Toast.makeText(context,"nextwork is available",Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(context,"network is unavailabLe",Toast.LENGTH_SHORT).show();
            }

        }
    }

    class LocalReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context,"receive local broadcast",Toast.LENGTH_SHORT).show();
        }
    }

}
