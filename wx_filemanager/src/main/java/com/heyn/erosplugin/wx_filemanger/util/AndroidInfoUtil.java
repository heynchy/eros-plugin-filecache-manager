package com.heyn.erosplugin.wx_filemanger.util;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Author: 崔海营
 * Date:   2018/10/10
 * <p>
 * Introduce: Android手机 相关信息的工具类
 */
public class AndroidInfoUtil {


    /**
     * 获取包含虚拟键的整体屏幕高度
     *
     * @param activity
     * @return
     */
    public static int getHasVirtualKey(Activity activity) {
        int pxHeight = 0;
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            pxHeight = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pxHeight;
    }

    /**
     * 获取屏幕尺寸，但是不包括虚拟键的高度
     *
     * @return
     */
    public static int getNoHasVirtualKey(Activity activity) {
        int height = activity.getWindowManager().getDefaultDisplay().getHeight();
        return height;
    }

    /**
     * 获取屏幕宽度，但是不包括虚拟键的高度
     *
     * @return
     */
    public static int getScreenWidth(Activity activity) {
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        return width;
    }

    /**
     * 获取虚拟功能键高度
     *
     * @param context
     * @return
     */
    public static int getVirtualBarHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    /**
     * 根据包名打开 APK
     *
     * @param context
     * @param packageName
     * @param key
     * @param params
     */
    public static void openApk(Context context, String packageName, String key, String params) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent();
        intent = packageManager.getLaunchIntentForPackage(packageName);
        if (!(TextUtils.isEmpty(key) || TextUtils.isEmpty(params))) {
            intent.putExtra(key, params);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * 根据包名和类名打开APK
     *
     * @param context
     * @param packageName
     * @param activityName
     * @param key
     * @param params
     */
    public static void openApk(Context context, String packageName, String activityName, String key, String params) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        ComponentName componentName = new ComponentName(packageName, activityName);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!(TextUtils.isEmpty(key) || TextUtils.isEmpty(params))) {
            intent.putExtra(key, params);
        }
        context.startActivity(intent);
    }

    /**
     * 检测应用是否已安装
     *
     * @param context
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName)) return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
