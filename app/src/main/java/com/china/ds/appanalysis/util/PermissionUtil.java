package com.china.ds.appanalysis.util;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;

import java.util.List;

/**
 * Created by Administrator on 2016/9/2.
 */
public class PermissionUtil {


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isPermissionAllow(Context context, String permission){
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        return appOpsManager.checkOpNoThrow(permission, applicationInfo.uid, context.getPackageName())
                == AppOpsManager.MODE_ALLOWED;
    }

    public static boolean hasActivityByAction(Context context, String action) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent intent = new Intent(action);
        //检索所有可用于给定的意图进行的活动。如果没有匹配的活动，则返回一个空列表。
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);
        return list.size() > 0;
    }
}
