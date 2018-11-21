package com.heyn.erosplugin.wx_filemanger.event;

import java.io.Serializable;

/**
 * Author: Heynchy
 * Date:   2018/9/19
 * <p>
 * Introduce: 下载文件的参数实体
 */
public class ParamsEvent implements Serializable {
    String url;      // 下载链接
    String fileId;   // 文件ID
    String fileName; // 文件名称
    String token;    // 权限token

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
