package com.china.ds.appanalysis.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.china.ds.appanalysis.service.MonitorService;

/**
 * Created by Administrator on 2016/9/4 0004.
 */
public class RestartReceiver extends BroadcastReceiver {

    public static final String ACTION_BROADCAST = "com.china.ds.appanalysis.restart_broadcast";

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, MonitorService.class));
    }
}
