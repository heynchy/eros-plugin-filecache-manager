package com.heyn.erosplugin.wx_filemanger.util;

import android.content.Context;

/**
 * Author: 崔海营
 * Date:   2018/10/9
 * <p>
 * Introduce:
 */
public class PixInfoUtil {
    /**
     * dp转换成px
     */
    public static int dpTopx(Context context, int dp) {
        return (int) (context.getResources().getDisplayMetrics().density * dp + 0.5f);
    }

    /**
     * px转换成dp
     */
    public static int px2dp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
