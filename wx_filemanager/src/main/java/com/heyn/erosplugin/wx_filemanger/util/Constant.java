package com.heyn.erosplugin.wx_filemanger.util;

/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce: 相关的常量值
 */
public class Constant {
    public static final String SUCCESS_CALLBACK = "success_callback";// 下载文件成功的回调参数
    public static final String FAILURE_CALLBACK = "failure_callback";// 下载文件失败的回调参数
    public static final String PROGRESS_CALLBACK = "progress_callback";// 下载文件进度的回调参数
    public static final String DATA_PARAMAS = "data_params"; // 交互参数

    public static final String ACTION_NUM = "action_num";           // 操作的参数
    public static final int ACTION_ONE = 1;                         // 判断文件是否存在
    public static final int ACTION_TWO = 2;                         // 预览文件操作
    public static final int ACTION_THREE = 3;                       // 清除缓存操作
    public static final int ACTION_FOUR = 4;                        // 获取缓存大小的操作
    public static final int ACTION_FIVE = 5;                        // 预览本地文件操作
    public static final String CUSTOM_CALLBACK = "custom_callback"; // 普通的回调参数
}
