package com.china.ds.appanalysis.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.china.ds.appanalysis.service.TopActivityListener;

public class TopActivityReceiver extends BroadcastReceiver {

    public static final String ACTION_BROADCAST = "com.china.ds.appanalysis.topactivity";
    public static final String KEY_TOPACTIVITY_PACKAGENAME = "packagename";

    TopActivityListener listener;
    public TopActivityReceiver(TopActivityListener listener) {
        this.listener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String packagename = intent.getStringExtra(KEY_TOPACTIVITY_PACKAGENAME);
        listener.updateTopActivity(packagename);
    }
}
