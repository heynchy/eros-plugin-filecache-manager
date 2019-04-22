package com.heyn.erosplugin.wx_filemanger.customInterface;

/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce:
 */
public interface onUploadListener {

    /**
     * 上传成功
     *
     * @param path 返回下载的路径
     */
    void onSuccess(String path);

    /**
     * 上传失败
     *
     * @param reason 返回下载失败的原因
     */
    void onFailure(String reason);

}
