package com.heyn.erosplugin.wx_filemanger.customInterface;

/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce:
 */
public interface OnProgressListener {
    /**
     * 上传的进度
     *
     * @param total   总的字节数
     * @param current 当前已上传的字节数
     */
    void onProgress(long total, long current);
}
