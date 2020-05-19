package com.heyn.erosplugin.wx_filemanger.event;

import java.io.Serializable;

/**
 * Author: Heynchy
 * Date:   2019/4/16
 * <p>
 * Introduce:
 */
public class FileEvent implements Serializable {
    private String url;             // 上传接口
    private  String params;         // 上传附带参数 json
    private String header;          // 上传接口的头文件设置
    private String type;            // 系统文件过滤类型
    private String fileFolderPath;  // 指定打开目标文件夹的相对路径

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileFolderPath() {
        return fileFolderPath;
    }

    public void setFileFolderPath(String fileFolderPath) {
        this.fileFolderPath = fileFolderPath;
    }
}

