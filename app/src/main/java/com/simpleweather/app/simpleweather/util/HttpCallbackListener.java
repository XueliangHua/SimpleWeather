package com.simpleweather.app.simpleweather.util;

/**
 * Created by Dennis on 2016/8/31.
 */
public interface HttpCallbackListener {
    void onFinish(String response);

    void onError(Exception e);
}
