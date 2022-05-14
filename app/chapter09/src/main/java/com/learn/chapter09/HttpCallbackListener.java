package com.learn.chapter09;

//回调接口
public interface HttpCallbackListener {
    void onFInish(String response);//成功响应调用
    void onError(Exception e);//出现错误调用
}
