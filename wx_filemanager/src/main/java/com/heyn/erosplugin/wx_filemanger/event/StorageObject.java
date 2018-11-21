package com.heyn.erosplugin.wx_filemanger.event;

import java.io.Serializable;

/**
 * Author: Heynchy
 * Date:   2018/11/20
 * <p>
 * Introduce:
 */
public class StorageObject implements Serializable {
    private String value;
    private long time;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
