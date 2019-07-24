package com.shaikhutech.callrecorder.BroadcastReciver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.shaikhutech.callrecorder.service.CallDetectionService;

public class RestartServiceReceiver extends BroadcastReceiver {

    private static final String TAG = "AutoRestartApp";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        context.startService(new Intent(context.getApplicationContext(), CallDetectionService.class));

    }

}