package com.china.ds.appanalysis.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.TopActivityMonitor;
import com.china.ds.appanalysis.activity.MainActivity;
import com.china.ds.appanalysis.data.AppUseRecord;
import com.china.ds.appanalysis.db.DBAccessor;
import com.china.ds.appanalysis.db.DBHelper;
import com.china.ds.appanalysis.util.PackageManagerUtil;
import com.china.ds.appanalysis.util.ProcessUtil;

public class MonitorService extends Service implements TopActivityMonitor.TopActivityListener{

    private static final String TAG = MonitorService.class.getSimpleName();

    TopActivityMonitor monitor;
    String curPackageName;
    long startTime;

    public MonitorService() {
    }

    public void onCreate(){
        monitor = new TopActivityMonitor(this, this);
        monitor.start();
        putServiceToForeground();
    }

    public void onDestroy(){
        super.onDestroy();
        monitor.setCancel();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void updateTopActivity(String packagename) {
        Log.i(TAG, "updateTopActivity " + packagename);
        if(curPackageName != null && !curPackageName.isEmpty() && (packagename == null || !curPackageName.equals(packagename) )){
            checkRecord();
            curPackageName = packagename;
            startTime = System.currentTimeMillis();
        }else if(curPackageName == null || curPackageName.isEmpty() ){
            curPackageName = packagename;
            startTime = System.currentTimeMillis();
        }

    }

    private void checkRecord(){
        boolean ret = ProcessUtil.isHomeScreen(this, curPackageName);
        if(!ret){
            saveRecord();
        }
    }

    private void saveRecord(){
        Log.i(TAG, "saveRecord " + curPackageName + "," + startTime);
        long endTime = System.currentTimeMillis();
        AppUseRecord record = new AppUseRecord();
        record.packageName = curPackageName;
        record.startTime = startTime;
        record.endTime = endTime;
        DBAccessor.createOrUpdate(record);
    }

    final static int myID = 5656;
    Notification notif;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void putServiceToForeground() {
        if (notif == null) {
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder mBuilder =
                    new Notification.Builder(this.getApplicationContext())
                            .setContentTitle("monitor")
                            .setContentText("正在收集应用数据...")
                            .setSmallIcon(R.drawable.ic_un_secret)
                            .setContentIntent(pendingIntent);

            notif = mBuilder.build();
        }
        startForeground(myID, notif);
    }
}
