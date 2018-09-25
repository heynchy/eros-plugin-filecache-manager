package com.heyn.erosplugin.wx_filemanger;

import android.content.Context;

import com.benmu.framework.BMWXApplication;

/**
 * Created by Carry on 2017/8/23.
 */

public class Application extends BMWXApplication {
    private static volatile Application mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * 创建App的单例对象
     */
    public synchronized static Application getInstance() {
        if (mInstance == null) {
            synchronized (Application.class) {
                if (null == mInstance) {
                    mInstance = new Application();
                }
            }
        }
        return mInstance;
    }

    public static Context getAppContext() {
        return getWXApplication();
    }

}
