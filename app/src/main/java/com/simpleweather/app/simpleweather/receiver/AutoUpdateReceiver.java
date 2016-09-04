package com.simpleweather.app.simpleweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.simpleweather.app.simpleweather.service.AutoUpdateService;

/**
 * Created by Xueliang Hua on 2016/9/4.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, AutoUpdateService.class);
        context.startService(i);
    }
}
