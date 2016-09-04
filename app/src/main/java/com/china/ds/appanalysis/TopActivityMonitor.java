package com.china.ds.appanalysis;

import android.content.Context;
import android.content.pm.PackageManager;

import com.china.ds.appanalysis.service.TopActivityListener;
import com.china.ds.appanalysis.util.PackageManagerUtil;

/**
 * Created by Administrator on 2016/9/1 0001.
 */
public class TopActivityMonitor extends Thread {

    private Context context;
    private TopActivityListener listener;
    boolean isCancel = false;

    public TopActivityMonitor(Context context, TopActivityListener listener){
        this.context = context;
        this.listener = listener;
    }

    public void setCancel(){
        isCancel = true;
    }

    @Override
    public void run(){
        while (!isCancel){
            String packageName = PackageManagerUtil.getTopActivityTotal(context);
            listener.updateTopActivity(packageName);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
