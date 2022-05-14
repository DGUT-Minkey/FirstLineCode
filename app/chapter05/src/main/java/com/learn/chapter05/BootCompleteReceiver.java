package com.learn.chapter05;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class BootCompleteReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"Boot is CompLeted",Toast.LENGTH_LONG).show();
        Log.d("xu","Boot is CompLeted");
        System.out.println("Boot is CompLeted");
    }

}
