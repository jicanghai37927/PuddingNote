package com.haiyunshan.pudding.utils;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;

import com.haiyunshan.pudding.App;

import java.util.List;

public class PackageUtils {

    public static final boolean checkSupport(Context context, ResolveInfo resolveInfo, Intent intent) {
        List<ResolveInfo> list = queryActivities(context, intent);
        if (list == null || list.isEmpty()) {
            return false;
        }

        for (ResolveInfo entry : list) {
            if (entry.activityInfo.packageName.equalsIgnoreCase(resolveInfo.activityInfo.packageName)
                    && entry.activityInfo.name.equalsIgnoreCase(resolveInfo.activityInfo.name)) {
                return true;
            }
        }

        return false;
    }

    public static final List<ResolveInfo> queryActivities(Context context, Intent intent) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        boolean print = false;
        if (print) {
            for (ResolveInfo entry : list) {
                Log.w("AA", entry.activityInfo.name);
            }
        }

        return list;
    }

    public static Drawable getIcon(@NonNull Context context, @NonNull String pkgName) {

        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            if (info != null) {
                return info.loadIcon(pm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean exist(@NonNull Context context, @NonNull String pkgName) {
        PackageManager pm = context.getPackageManager();

        try {
            ApplicationInfo info = pm.getApplicationInfo(pkgName, 0);
            return (info != null);

        } catch (PackageManager.NameNotFoundException e) {

        }

        return false;
    }

    public static boolean launch(@NonNull Context context, @NonNull String pkgName) {

        try{
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(pkgName);
            if (intent == null) {
                return false;
            }

            context.startActivity(intent);
            return true;

        }catch(Exception e){

        }

        return false;
    }

    public static boolean start(@NonNull Context context, @NonNull String pkgName) {

        boolean result = false;

        try {
            PackageManager pm = context.getPackageManager();

            PackageInfo pi = pm.getPackageInfo(pkgName, 0);
            if (pi == null) {
                return result;
            }

            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.setPackage(pi.packageName);

            List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
            if (apps == null || apps.isEmpty()) {
                return result;
            }

            ResolveInfo ri = apps.iterator().next();
            if (ri != null) {
                result = start(context, ri.activityInfo.packageName, ri.activityInfo.name);
            }

        } catch (Exception e) {

        }

        return result;
    }

    public static boolean start(@NonNull Context context, @NonNull String pkgName, @NonNull String className) {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(new ComponentName(pkgName, className));
        intent.setFlags(launchFlags);

        try {
            context.startActivity(intent);

            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean canExecute(Context context, String action, Uri uri) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(action, uri);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);

        return (list != null && !list.isEmpty());
    }

    public static boolean executeAction(Context context, String action, Uri uri) {
        int launchFlags = Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED;

        Intent intent = new Intent(action, uri);
        intent.setFlags(launchFlags);

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean showDetailsSettings(Context context, String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            pkgName = context.getPackageName();
        }

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", pkgName, null));

        try {
            context.startActivity(intent);
            return true;
        } catch (Exception e) {

        }

        return false;
    }

    public static boolean canRead() {
        App context = App.getInstance();

        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }
}
