package com.china.ds.appanalysis.util;

import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/8/26.
 */
public class PackageManagerUtil {

    private final static String TAG = PackageManagerUtil.class.getSimpleName();

    /**
     * 取得应用版本号 versionCode
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        if(context == null){
            return versionCode;
        }
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 取得应用版本号 versionCode
     * @param context
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = "";
        if(context == null){
            return versionName;
        }
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    public static String getTopActivityTotal(Context context){
        String packname = PackageManagerUtil.getTopActivityPackageName(context);
        if(StringUtils.isBlank(packname)){
            /**
             * 若未获取到包名，则使用android sdk 的Usage去获取
             */
            packname = PackageManagerUtil.getTopPackageNameUseUsage(context);
        }
        return packname;
    }

    /**
     * 获取栈顶应用包名
     * @param context
     * @return
     */
    public static String getTopActivityPackageName(Context context){
        if(context == null){
            return "";
        }
        String topPkg = "";
        //String className = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            topPkg = getTopPackageNameFor50(context);
        } else {
			/*ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			final ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
			topPkg = topActivity.getPackageName();
			className = topActivity.getClassName();*/
            topPkg = getTopPackageName(context);
        }
        return topPkg;
    }

    /**
     * 获取栈顶应用包名(android5.0以下)
     * @param context
     * @return
     */
    private static String getTopPackageName(Context context) {
        if(context == null){
            return "";
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
        String topPkg = topActivity.getPackageName();
        //className = topActivity.getClassName();
        return topPkg;
    }

    /**
     * 获取栈顶应用包名(android5.0以上)
     * @param context
     * @return
     */
    private static String getTopPackageNameFor50(Context context) {
        if(context == null){
            return "";
        }
        //PackageManager packageManager = context.getPackageManager();
        ActivityManager.RunningAppProcessInfo currentInfo = null;
        Field field = null;
        int START_TASK_TO_FRONT = 2;
        String topPkg = null;
        try {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) {
            e.printStackTrace();
        }
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        if(appList != null){
            for (ActivityManager.RunningAppProcessInfo app : appList) {
                if (app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Integer state = null;
                    try {
                        state = field.getInt(app);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (state != null && state == START_TASK_TO_FRONT) {
                        currentInfo = app;
                        break;
                    }
                }
            }
        }

        if (currentInfo != null) {
            //如果activity中设置了android:process=".aaa"，则currentInfo.processName获取到的不是应用包名
            topPkg = currentInfo.processName;
            String[] pkgList = currentInfo.pkgList;
            if(pkgList != null && pkgList.length > 0){
                topPkg = pkgList[0];
            }
			/*
			String[] arrs = packageManager.getPackagesForUid(currentInfo.uid);
			if(arrs != null && arrs.length > 0){
				topPkg = arrs[0];
			}
			*/
        }
        //LogUtil.i("soso", "topPkg:::" + topPkg);
        return topPkg;
    }

    /**
     * 通过UsageStatsManager获取当前交互应用包名，android sdk 21以上才有此接口，低于21的将返回""<br/>
     * need android.permission.PACKAGE_USAGE_STATS<br/>
     * grant permission through the Settings application. <br/>
     * 需要在设置中授权
     * @param context
     * @return
     */
    public static String getTopPackageNameUseUsage(Context context){
        String packageName = "";
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//				List<UsageStats> list = null;
                UsageEvents eventsList = null;
                UsageStatsManager usageStatsManager = (UsageStatsManager)context.getSystemService(Context.USAGE_STATS_SERVICE);
                if(usageStatsManager != null){
                    long now = System.currentTimeMillis();
                    //获得最近一分钟内使用过的APP的统计数据
//					list = usageStatsManager.queryUsageStats(usageStatsManager.INTERVAL_BEST, now-60*1000, now);
                    eventsList = usageStatsManager.queryEvents(now-60*1000, now);
                }

//				if(list != null && !list.isEmpty()){
//					/**
//					 * 按【最后使用时间】降序排列
//					 */
//					Collections.sort(list, new Comparator<UsageStats>(){
//
//						@Override
//						public int compare(UsageStats lhs, UsageStats rhs) {
////							return (int)(rhs.getLastTimeUsed() - lhs.getLastTimeUsed());
//							return (int)(rhs.getLastTimeStamp() - lhs.getLastTimeStamp());
//						}
//
//					});
//					packageName = list.get(0).getPackageName();
//				}

                if(eventsList != null){
                    UsageEvents.Event event = new UsageEvents.Event();
                    long lastTimeStamp = 0;

                    while (eventsList.hasNextEvent()) {
                        eventsList.getNextEvent(event);
                        if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                            if(lastTimeStamp == 0 || event.getTimeStamp() > lastTimeStamp){
                                packageName = event.getPackageName();
                                lastTimeStamp = event.getTimeStamp();
                            }
                        }
                    }
                }
            }else{
                Log.i("soso", "the sdk version is lower than " + Build.VERSION_CODES.LOLLIPOP);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

        return packageName;
    }

    /**
     * 获取栈顶应用的activity<br/>
     * android sdk 21 之后，不能获取到第三方应用的信息，但还是可以获取到本应用的。
     * @param context
     * @return
     */
    public static String getTopActivityClassName(Context context) {
        if(context == null){
            return "";
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final ComponentName topActivity = am.getRunningTasks(1).get(0).topActivity;
        if(topActivity == null){
            return "";
        }
        //String topPkg = topActivity.getPackageName();
        //String className = topActivity.getClassName();
        return topActivity.getClassName();
    }

    /**
     * 获取已安装应用
     * @param context
     * @return
     */

    public static List<ApplicationInfo> getAppInstalled(Context context){
        PackageManager pm = context.getPackageManager();
        return pm.getInstalledApplications(0);
    }

    /**
     * 获取可见的应用列表
     * @param context
     * @return
     */
    public static List<ApplicationInfo> getAppInLauncher(Context context){
        List<ApplicationInfo> resultList = new ArrayList<ApplicationInfo>();
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.GET_INTENT_FILTERS);
//        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.GET_RESOLVED_FILTER);

        Intent uiIntent = new Intent(Intent.ACTION_MAIN);
        uiIntent.addCategory(Intent.CATEGORY_HOME);
        List<ResolveInfo> uiList = pm.queryIntentActivities(uiIntent, PackageManager.MATCH_DEFAULT_ONLY);

        List<String> s = new ArrayList<String>();
        List<String> uiS = new ArrayList<String>();
        if(uiList != null && !uiList.isEmpty()){
            for(ResolveInfo info:uiList){
                uiS.add(info.activityInfo.packageName);
            }
        }
        for(ResolveInfo obj : list){
            if(s.contains(obj.activityInfo.packageName)){
                continue;
            }
            if(obj.activityInfo.packageName.equals(context.getPackageName())){
                continue;
            }

            if(uiS.contains(obj.activityInfo.packageName)){
                continue;
            }
            resultList.add(obj.activityInfo.applicationInfo);
            s.add(obj.activityInfo.packageName);
        }
        return resultList;
    }

    /**
     * 获取从apk文件解析出的应用信息，若不是apk文件，返回null
     * @param context
     * @param apkPath
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context, String apkPath) {
        try{
            PackageManager pm = context.getPackageManager();
            PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                ApplicationInfo appInfo = info.applicationInfo;
                appInfo.sourceDir = apkPath;
                appInfo.publicSourceDir = apkPath;
                try {
                    return appInfo;
                } catch (OutOfMemoryError e) {
                    Log.e("ApkIconLoader", e.toString());
                }
            }
        }catch(Exception e){

        }

        return null;
    }

    /**
     * 打开系统应用详情界面
     * @param context
     * @param packageName
     */
    public static void openAppDetail(Context context, String packageName){
        if(context == null){
            return;
        }
        if(StringUtils.isBlank(packageName)){
            Log.v("PackageManagerUtil", "配置错误,包名packageName为空！");
            return;
        }
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if(intent != null){
            //卸载
            Uri packageURI = Uri.parse("package:"+packageName);
            Intent intent2 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent2.setData(packageURI);
            context.startActivity(intent2);
        }
    }

    /**
     * 卸载
     * @param context
     * @param packageName
     */
    public static void uninstalledApp(Context context, String packageName){
        if(context == null){
            return;
        }
        if(StringUtils.isBlank(packageName)){
            Log.v("PackageManagerUtil", "配置错误,包名packageName为空！");
            return;
        }
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        if(intent != null){
            //卸载
            Uri packageURI = Uri.parse("package:"+packageName);
            Intent intent2 = new Intent(Intent.ACTION_DELETE);
            intent2.setData(packageURI);
            context.startActivity(intent2);
        }
    }

    /**
     * 是否存在应用
     * @param context
     * @param pkg
     * @return
     */
    public static boolean isExistInstallApp(Context context,String pkg){
        if(context==null || StringUtils.isBlank(pkg)){
            return false;
        }
        PackageManager pm = context.getApplicationContext().getPackageManager();
        ApplicationInfo info;
        try {
            info = pm.getApplicationInfo(pkg,PackageManager.GET_META_DATA);
            if(info != null){
                return true;
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "there is no app of " + pkg);
            e.printStackTrace();
        }

        return false;
    }


    public static boolean isSystemApp(PackageInfo pInfo) {
        if(pInfo == null){
            return false;
        }
        return isSystemApp(pInfo.applicationInfo);
    }

    public static boolean isSystemUpdateApp(PackageInfo pInfo) {
        if(pInfo == null){
            return false;
        }
        return isSystemUpdateApp(pInfo.applicationInfo);
    }

    public static boolean isUserApp(PackageInfo pInfo) {
        if(pInfo == null){
            return false;
        }
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }

    public static boolean isSystemApp(ApplicationInfo pInfo) {
        if(pInfo == null){
            return false;
        }
        return ((pInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static boolean isSystemUpdateApp(ApplicationInfo pInfo) {
        if(pInfo == null){
            return false;
        }
        return ((pInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public static boolean isUserApp(ApplicationInfo pInfo) {
        if(pInfo == null){
            return false;
        }
        return (!isSystemApp(pInfo) && !isSystemUpdateApp(pInfo));
    }
}
