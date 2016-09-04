package com.china.ds.appanalysis;

import android.app.Application;

/**
 * Created by Administrator on 2016/9/2.
 */
public class MyApplication extends Application {

    private static MyApplication application;
    public static MyApplication getInstance(){
        return application;
    }
    @Override
    public void onCreate(){
        super.onCreate();

        application = this;
    }
}