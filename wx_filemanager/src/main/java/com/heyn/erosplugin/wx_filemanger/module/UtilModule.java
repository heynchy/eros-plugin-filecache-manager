package com.heyn.erosplugin.wx_filemanger.module;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.google.gson.Gson;
import com.heyn.erosplugin.wx_filemanger.customInterface.IKeyBoardVisibleListener;
import com.heyn.erosplugin.wx_filemanger.event.PxOrDpEvent;
import com.heyn.erosplugin.wx_filemanger.util.AndroidInfoUtil;
import com.heyn.erosplugin.wx_filemanger.util.PixInfoUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.lang.reflect.Method;


/**
 * Author: 崔海营
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


}
