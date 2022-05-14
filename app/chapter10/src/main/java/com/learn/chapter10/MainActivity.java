package com.learn.chapter10;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final int UPDATE_TEXT = 1;
    private TextView text;
    private MyService.DownloadBinder downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {

//      成功绑定时使用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (MyService.DownloadBinder)service;//可根据居停场景调用DownloadBinderpublic的任何方法
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }
//       解除绑定时使用
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

//    异步消息处理机制
//    1.创建一个Handler对象，重写handleMessage()方法
//    2.子线程需要进行UI操作时就创建一个Message对象，并通过Handler将这条消息发送出去
//    3.这条消息会被添加到MessageQueue队列等待被处理
//    4.Loop从MessageQueue取出待处理消息，最后分发回Handler的handlerMessage方法中。
//    5.handler在主线程创建，handleMessage也会在主线程进行
    private Handler handler = new Handler(){
        @Override
        //此时在主线程中
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case UPDATE_TEXT:
//                    在这里可以进行UI操作
                    text.setText("Nice to meet you");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        text =(TextView)findViewById(R.id.text);
//        Button changeText =(Button)findViewById(R.id.change_text);
//        changeText.setOnClickListener(this);
        Button startService =findViewById(R.id.start_service);
        Button stopService =findViewById(R.id.stop_service);
        startService.setOnClickListener(this);
        stopService.setOnClickListener(this);
        Button bindService = (Button) findViewById(R.id.bind_service);
        Button unbindService = (Button) findViewById(R.id.unbind_service);
        bindService.setOnClickListener(this);
        unbindService.setOnClickListener(this);
        findViewById(R.id.start_intent_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
//        UI库不安全，UI元素更新只能在主线程中进行Only the original thread that created a view hierarchy can touch its views.
        switch (v.getId()){
//            case R.id.change_text:
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        text.setText("Nice to meet you");
//                    }
//                }).start();
//                break;
//            default:
//
//                break;

            case R.id.change_text:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Message message = new Message();
                        message.what=UPDATE_TEXT;
                        handler.sendMessage(message);
                    }
                }).start();

            case R.id.start_service:
                Intent startIntent = new Intent(this,MyService.class);
//                启动服务
                startService(startIntent);
                break;
            case R.id.stop_service:
                Intent stopIntent = new Intent(this,MyService.class);
//                停止服务
                stopService(stopIntent);
                break;
            case R.id.bind_service:
                Intent bindIntent = new Intent(this,MyService.class);
                bindService(bindIntent,connection,BIND_AUTO_CREATE);//绑定服务，BIND_AUTO_CREATE自动创建服务
                break;
            case R.id.unbind_service:
                unbindService(connection);
                break;
            case R.id.start_intent_service:
//                打印主线程的id
                Log.d("MainActivity","MainThread id is"+Thread.currentThread().getId());
                Intent intentService = new Intent(this,MyIntentService.class);
                startService(intentService);
                break;
            default:
                break;
        }
    }


}
