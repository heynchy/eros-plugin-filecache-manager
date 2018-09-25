package com.heyn.erosplugin.wx_filemanger.customInterface;

/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce:
 */
public interface onDownloadListener {

    /**
     * 下载成功
     *
     * @param path 返回下载的路径
     */
    void onSuccess(String path);

    /**
     * 下载失败
     *
     * @param reason 返回下载失败的原因
     */
    void onFailure(String reason);

    /**
     * 下载的进度
     *
     * @param progress 当前的进度值
     */
    void onProgress(int progress);
}
