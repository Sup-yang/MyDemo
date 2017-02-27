package com.example.yang.kuo.util;

/**
 * Created by yang on 2017/2/27.
 */

public interface HttpCallbackListener {

    void onFinish(String response);
    void onError(Exception e);
}
