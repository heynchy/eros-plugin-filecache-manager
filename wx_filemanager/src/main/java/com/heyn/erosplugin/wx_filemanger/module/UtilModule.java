package com.heyn.erosplugin.wx_filemanger.module;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Process;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewTreeObserver;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.google.gson.Gson;
import com.heyn.erosplugin.wx_filemanger.customInterface.IKeyBoardVisibleListener;
import com.heyn.erosplugin.wx_filemanger.event.AppIntentEvent;
import com.heyn.erosplugin.wx_filemanger.event.PxOrDpEvent;
import com.heyn.erosplugin.wx_filemanger.util.AndroidInfoUtil;
import com.heyn.erosplugin.wx_filemanger.util.PixInfoUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * Author: Heynchy
 * Date:   2018/10/9
 * <p>
 * Introduce: Android 与 Eros交互所用到的日常工具类
 */
@WeexModule(name = "UtilModule", lazyLoad = true)
public class UtilModule extends WXModule {
    boolean isVisiableForLast = false;

    /**
     * 获取Android手机软键盘的高度
     *
     * @param callback          软键盘弹出的回调
     * @param callbackInvisible 软键盘隐藏的回调
     */
    @JSMethod(uiThread = true)
    public void getSoftKeyInfoAlive(final JSCallback callback, final JSCallback callbackInvisible) {
        final Activity activity = (Activity) mWXSDKInstance.getContext();
        addOnSoftKeyBoardVisibleListener(activity, new IKeyBoardVisibleListener() {
            @Override
            public void onSoftKeyBoardVisible(boolean visible, int windowBottom, int width) {
                if (width != 0) {
                    windowBottom = (int) ((float) windowBottom / width * 750f);
                }
                PxOrDpEvent event = new PxOrDpEvent();
                event.setDpHeight(PixInfoUtil.px2dp(mWXSDKInstance.getContext(), windowBottom));
                event.setPxHeight(windowBottom);
                if (visible && callback != null) {
                    callback.invokeAndKeepAlive(new Gson().toJson(event));
                } else if (!visible && callbackInvisible != null) {
                    callbackInvisible.invokeAndKeepAlive(new Gson().toJson(event));
                }
            }
        });

    }

    /**
     * 获取Android手机软键盘的高度
     *
     * @param callback          软键盘弹出的回调
     * @param callbackInvisible 软键盘隐藏的回调
     */
    @JSMethod(uiThread = true)
    public void getSoftKeyInfo(final JSCallback callback, final JSCallback callbackInvisible) {
        final Activity activity = (Activity) mWXSDKInstance.getContext();
        addOnSoftKeyBoardVisibleListener(activity, new IKeyBoardVisibleListener() {
            @Override
            public void onSoftKeyBoardVisible(boolean visible, int windowBottom, int width) {
                if (width != 0) {
                    windowBottom = (int) ((float) windowBottom / width * 750f);
                }
                PxOrDpEvent event = new PxOrDpEvent();
                event.setDpHeight(PixInfoUtil.px2dp(mWXSDKInstance.getContext(), windowBottom));
                event.setPxHeight(windowBottom);
                if (visible && callback != null) {
                    callback.invoke(new Gson().toJson(event));
                } else if (!visible && callbackInvisible != null) {
                    callbackInvisible.invoke(new Gson().toJson(event));
                }
            }
        });

    }

    /**
     * 打开另外一个APP
     *
     * @param params         相关参数配置（JSON格式）
     * @param resultCallback 结果回调（true: 打开成功， false: 打开失败）
     * @param installed      安装回调（如果未安装，会响应---该回调）
     */
    @JSMethod(uiThread = true)
    public void openOtherApp(String params, JSCallback resultCallback, JSCallback installed) {
        AppIntentEvent event = new Gson().fromJson(params, AppIntentEvent.class);
        Context context = mWXSDKInstance.getContext();
        if (AndroidInfoUtil.checkApkExist(context, event.getPackageName())) {
            try {
                if (TextUtils.isEmpty(event.getActivityName())) {
                    AndroidInfoUtil.openApk(context, event.getPackageName(), event.getKey(),
                            event.getParams());
                } else {
                    AndroidInfoUtil.openApk(context, event.getPackageName(), event.getActivityName(),
                            event.getKey(), event.getParams());
                }
                resultCallback.invoke(true);
            } catch (Exception e) {
                resultCallback.invoke(false);
            }

        } else {
            // 应用包未安装
            installed.invoke(false);
        }
    }

    /**
     * 获取Android屏幕尺寸，但是不包括虚拟键的高度
     *
     * @param callback 返回值的回调（已转换为JS端可用数据）
     */
    @JSMethod(uiThread = true)
    public void getNoHasVirtualKey(final JSCallback callback) {
        final Activity activity = (Activity) mWXSDKInstance.getContext();
        // 获得屏幕整体的高度
        int hight = AndroidInfoUtil.getNoHasVirtualKey(activity);
        // 获得屏幕整体的宽度
        int width = AndroidInfoUtil.getScreenWidth(activity);
        if (width != 0) {
            hight = (int) ((float) hight / width * 750f);
        }
        if (callback != null) {
            callback.invoke(hight);
        }
    }

    public void addOnSoftKeyBoardVisibleListener(Activity activity, final IKeyBoardVisibleListener listener) {
        final View decorView = activity.getWindow().getDecorView();
        decorView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect rect = new Rect();
                decorView.getWindowVisibleDisplayFrame(rect);
                // 获得屏幕整体的高度
                int hight = decorView.getHeight();
                // 获得屏幕整体的宽度
                int width = decorView.getWidth();
                // 获取虚拟按键的高度
                int virtualBar = AndroidInfoUtil.getVirtualBarHeigh(mWXSDKInstance.getContext());
                //获得键盘高度
                int keyboardHeight = hight - rect.bottom - (virtualBar < 0 ? 0 : virtualBar);
                listener.onSoftKeyBoardVisible(keyboardHeight > 0, keyboardHeight, width);
                boolean visible = keyboardHeight > 0;
                if (visible != isVisiableForLast) {
                    listener.onSoftKeyBoardVisible(visible, keyboardHeight, width);
                }
                isVisiableForLast = visible;
            }
        });
    }

    /**
     * 获取Android APK 包的MD5值用于完整性校验
     *
     * @param success
     * @param failure
     */
    @JSMethod(uiThread = true)
    public void getAPKMD5Code(final JSCallback success, final JSCallback failure) {
        String apkPath = mWXSDKInstance.getContext().getPackageCodePath(); // 获取Apk包存储路径
        try {
            MessageDigest dexDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(apkPath)); // 读取apk文件
            while ((byteCount = fis.read(bytes)) != -1) {
                dexDigest.update(bytes, 0, byteCount);
            }
            BigInteger bigInteger = new BigInteger(1, dexDigest.digest()); // 计算apk文件的哈希值
            String sha = bigInteger.toString(16);
            fis.close();
            success.invoke(sha);
        } catch (NoSuchAlgorithmException e) {
            failure.invoke(e.getMessage());
        } catch (FileNotFoundException e) {
            failure.invoke(e.getMessage());
        } catch (IOException e) {
            failure.invoke(e.getMessage());
        }
    }

    /**
     * 强制退出APP（KILL PROGRESS）
     */
    @JSMethod(uiThread = true)
    public void exitAPP() {
        // 强制退出程序
        Process.killProcess(Process.myPid());
    }


}
