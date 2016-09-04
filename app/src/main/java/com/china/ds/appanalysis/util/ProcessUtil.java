package com.china.ds.appanalysis.util;

/**
 * Created by Administrator on 2016/9/1 0001.
 */

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

/**
 *
 * @author chj
 *
 */
public class ProcessUtil {

    private static List<String> homePackageNameList;

    /**
     * 获得属于桌面的应用的应用包名称
     * @return 返回包含所有包名的字符串列表
     */
    public static List<String> getHomes(Context context) {
        if(context == null){
            return null;
        }
        List<String> names = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        //属性
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> resolveInfo = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo ri : resolveInfo){
            names.add(ri.activityInfo.packageName);
        }
        return names;
    }

    /**
     * 判断当前运行的界面是否是桌面应用
     * @param context
     * @return
     */
    public static boolean isHomeScreen(Context context){
        if(context == null){
            return false;
        }
        ActivityManager mActivityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> rti = mActivityManager.getRunningTasks(1);
        return isHomeScreen(context, rti.get(0).topActivity.getPackageName());
    }

    /**
     * 判断指定的packagename是否桌面应用
     * @param context
     * @param packagename
     * @return
     */
    public static boolean isHomeScreen(Context context, String packagename){
        if(homePackageNameList == null){
            homePackageNameList = getHomes(context);
            if(homePackageNameList == null){
                return false;
            }
        }
        return homePackageNameList.contains(StringUtils.toString(packagename));
    }

}

