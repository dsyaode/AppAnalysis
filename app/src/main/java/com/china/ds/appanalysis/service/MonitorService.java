package com.china.ds.appanalysis.service;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.china.ds.appanalysis.R;
import com.china.ds.appanalysis.TopActivityMonitor;
import com.china.ds.appanalysis.activity.MainActivity;
import com.china.ds.appanalysis.data.AppUseRecord;
import com.china.ds.appanalysis.db.DBAccessor;
import com.china.ds.appanalysis.receiver.RestartReceiver;
import com.china.ds.appanalysis.receiver.TopActivityReceiver;
import com.china.ds.appanalysis.util.ProcessUtil;

public class MonitorService extends Service implements TopActivityListener{

    private static final String TAG = MonitorService.class.getSimpleName();

    public static final String ACTION_START = "start_service";
    public static final String ACTION_STOP = "stop_service";

    TopActivityMonitor monitor;
    String curPackageName;
    long startTime;

//    TopActivityReceiver receiver;
//    PendingIntent pendIntent;
    boolean isFocusStop;

    public MonitorService() {
    }

    public void onCreate(){
        monitor = new TopActivityMonitor(this, this);
//        pendIntent = null;
        isFocusStop = false;
//        receiver = new TopActivityReceiver(this);
//        this.registerReceiver(receiver, new IntentFilter(TopActivityReceiver.ACTION_BROADCAST));
    }

    public static void startService(Context context){
        Intent intent = new Intent(context, MonitorService.class);
        intent.setAction(ACTION_START);
        context.startService(intent);
    }

    public static void stopService(Context context){
        Intent intent = new Intent(context, MonitorService.class);
        intent.setAction(ACTION_STOP);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        if(intent.getAction().equals(ACTION_START)){
//            if(pendIntent == null){
//                Intent startIntent = new Intent(this, TopActivityIntentService.class);
//                pendIntent = PendingIntent.getService(this, 0, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                AlarmManager am = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
//                am.setRepeating(AlarmManager.RTC_WAKEUP,System.currentTimeMillis(), 1000, pendIntent);
//                putServiceToForeground();
//            }
            if(!monitor.isAlive()){
                monitor.start();
                putServiceToForeground();
            }

        }else if(intent.getAction().equals(ACTION_STOP)){
            isFocusStop = true;
            stopSelf();
        }

        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    public void onDestroy(){
        super.onDestroy();
        monitor.setCancel();

        stopForeground(true);

        if(!isFocusStop){
            this.sendBroadcast(new Intent(RestartReceiver.ACTION_BROADCAST));
        }else{
//            if(pendIntent != null){
//                AlarmManager am = (AlarmManager) this.getSystemService(ALARM_SERVICE);
//                am.cancel(pendIntent);
//            }

        }
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
