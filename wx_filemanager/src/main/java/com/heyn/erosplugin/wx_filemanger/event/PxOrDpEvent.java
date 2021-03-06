package com.heyn.erosplugin.wx_filemanger.event;

import java.io.Serializable;

/**
 * Author: Heynchy
 * Date:   2018/10/9
 * <p>
 * Introduce:
 */
public class PxOrDpEvent implements Serializable {
    int pxHeight;
    int dpHeight;
    private String phoneModel;  // 手机机型

    public int getDpHeight() {
        return dpHeight;
    }

    public void setDpHeight(int dpHeight) {
        this.dpHeight = dpHeight;
    }

    public int getPxHeight() {
        return pxHeight;
    }

    public void setPxHeight(int pxHeight) {
        this.pxHeight = pxHeight;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }
}
