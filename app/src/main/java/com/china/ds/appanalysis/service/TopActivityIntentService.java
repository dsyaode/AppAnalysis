package com.china.ds.appanalysis.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.china.ds.appanalysis.receiver.TopActivityReceiver;
import com.china.ds.appanalysis.util.PackageManagerUtil;
import com.china.ds.appanalysis.util.ProcessUtil;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class TopActivityIntentService extends IntentService {

    public TopActivityIntentService() {
        super("TopActivityIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TopActivityIntentService", "onHandleIntent");
        MonitorService.startService(this);

        String packageName = PackageManagerUtil.getTopActivityTotal(this);
        Intent sendIntent = new Intent();
        sendIntent.setAction(TopActivityReceiver.ACTION_BROADCAST);
        sendIntent.putExtra(TopActivityReceiver.KEY_TOPACTIVITY_PACKAGENAME, packageName);
        this.sendBroadcast(sendIntent);
    }



}
