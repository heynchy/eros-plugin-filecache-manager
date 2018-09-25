package com.heyn.erosplugin.wx_filemanger.util;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce: Android 6.0 内存卡权限读取工具类
 */

public class PermissionUtil {
    // Storage Permissions
    public static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 判断是否拥有内存卡读取权限
     *
     * @param activity
     * @return
     */
    public static boolean hasStoragePermission(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
            Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取内存卡读取权限
     */
    public static void getStoragePermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
            REQUEST_EXTERNAL_STORAGE);
    }
}
