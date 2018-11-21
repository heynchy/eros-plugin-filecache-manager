package com.heyn.erosplugin.wx_filemanger.event;

import java.io.Serializable;

/**
 * Author: HeynChy
 * Date:   2018/11/21
 * <p>
 * Introduce:
 */
public class AppIntentEvent implements Serializable {
    private String packageName;   // 应用包名
    private String activityName;  // 应用界面的路径名
    private String params;        // 所传参数（Json格式）
    private String key;           // 传参的KEY

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
