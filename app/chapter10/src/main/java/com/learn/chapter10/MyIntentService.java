package com.learn.chapter10;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.Nullable;

//开启线程异步和自动停止
public class MyIntentService extends IntentService {

    public MyIntentService(){
        super("MyIntentService");//调用父类的有参构造函数
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
//        打印当前线程ID
        Log.d("MyIntentService","MyIntentThread id is"+Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyIntentService","MyIntentService onDestroy executed");
    }
}
