package com.heyn.erosplugin.wx_filemanger.module;

import android.app.Activity;
import android.os.Environment;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.heyn.erosplugin.wx_filemanger.R;
import com.heyn.erosplugin.wx_filemanger.activity.PermissionActionActivity;
import com.heyn.erosplugin.wx_filemanger.util.FileUtil;
import com.heyn.erosplugin.wx_filemanger.util.PermissionUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_FOUR;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_THREE;

/**
 * Author: Heynchy
 * Date:   2018/9/19
 * <p>
 * Introduce:
 */
@WeexModule(name = "CacheModule", lazyLoad = true)
public class CacheModule extends WXModule {

    /**
     * 获取应用缓存的大小
     */
    @JSMethod(uiThread = true)
    public void getCachesSize(JSCallback callback) {
        Activity activity = (Activity) mWXSDKInstance.getContext();
        if (PermissionUtil.hasStoragePermission(activity)) {
            try {
                long size = FileUtil.getFolderSize(mWXSDKInstance.getContext().getExternalCacheDir());
                size = size + FileUtil.getFolderSize(mWXSDKInstance.getContext().getCacheDir());
                callback.invoke(FileUtil.getFormatSize(size));
            } catch (Exception e) {
                callback.invoke(activity.getResources().getString(R.string.default_size));
            }
        } else {
            PermissionActionActivity.start(mWXSDKInstance.getContext(), ACTION_FOUR, null,
                    callback);
        }
    }

    /**
     * 清除应用缓存
     */
    @JSMethod(uiThread = true)
    public void clearCaches(JSCallback callback) {
        Activity activity = (Activity) mWXSDKInstance.getContext();
        if (PermissionUtil.hasStoragePermission(activity)) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                FileUtil.deleteFiles(mWXSDKInstance.getContext().getExternalCacheDir());
                FileUtil.deleteFiles(mWXSDKInstance.getContext().getCacheDir());
                if (callback != null) {
                    callback.invoke(1);
                }
            } else {
                if (callback != null) {
                    callback.invoke(0);
                }
            }
        } else {
            PermissionActionActivity.start(mWXSDKInstance.getContext(), ACTION_THREE, null,
                    callback);
        }
    }
}
