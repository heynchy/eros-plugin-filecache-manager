package com.heyn.erosplugin.wx_filemanger.util;

import android.content.Context;
import android.text.TextUtils;

import com.benmu.framework.manager.ManagerFactory;
import com.benmu.framework.manager.StorageManager;
import com.google.gson.Gson;
import com.heyn.erosplugin.wx_filemanger.event.StorageObject;

/**
 * Author: Heynchy
 * Date:   2018/11/21
 * <p>
 * Introduce:
 */
public class ErosStorageUtil {
    /**
     * 基于Eros框架下的数据存储
     *
     * @param context
     * @param key
     * @param value
     */
    public static void put(Context context, String key, String value) {
        if (TextUtils.isEmpty(key) || TextUtils.isEmpty(value)) {
            return;
        }
        StorageManager storageManager = ManagerFactory.getManagerService(StorageManager.class);
        StorageObject object = new StorageObject();
        object.setTime(System.currentTimeMillis());
        object.setValue(value);
        if (storageManager != null) {
            storageManager.setData(context, key, new Gson().toJson(object));
        }
    }

    /**
     * 基于Eros框架下的数据移除
     *
     * @param context
     * @param key     key值
     */
    public static void remove(Context context, String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        StorageManager storageManager = ManagerFactory.getManagerService(StorageManager.class);
        if (storageManager != null) {
            storageManager.deleteData(context, key);
        }
    }

    /**
     * 基于Eros框架下的数据移除
     *
     * @param context
     * @param key     key值
     */
    public static String get(Context context, String key) {
        if (TextUtils.isEmpty(key)) {
            return null;
        }
        StorageManager storageManager = ManagerFactory.getManagerService(StorageManager.class);
        if (storageManager != null) {
            String result = storageManager.getData(context, key);
            if (!TextUtils.isEmpty(result)) {
                StorageObject object = new Gson().fromJson(result, StorageObject.class);
                result = object.getValue();
            }
            return result;
        }
        return null;
    }
}
