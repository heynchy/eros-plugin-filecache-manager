package com.heyn.erosplugin.wx_filemanger.module;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.weex.plugin.annotation.WeexModule;
import com.google.gson.Gson;
import com.heyn.erosplugin.wx_filemanger.R;
import com.heyn.erosplugin.wx_filemanger.activity.PermissionActionActivity;
import com.heyn.erosplugin.wx_filemanger.activity.WxDownloadFileActivity;
import com.heyn.erosplugin.wx_filemanger.customInterface.onDownloadListener;
import com.heyn.erosplugin.wx_filemanger.event.ParamsEvent;
import com.heyn.erosplugin.wx_filemanger.util.DownloadFileUtil;
import com.heyn.erosplugin.wx_filemanger.util.FileUtil;
import com.heyn.erosplugin.wx_filemanger.util.PermissionUtil;
import com.heyn.erosplugin.wx_filemanger.util.ToastUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.bridge.JSCallback;
import com.taobao.weex.common.WXModule;

import java.io.File;

import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_ONE;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_TWO;
import static com.heyn.erosplugin.wx_filemanger.util.FileUtil.*;


/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce: 文件相关的操作，主要包括下载，预览功能
 */
@WeexModule(name = "FileModule", lazyLoad = true)
public class FileModule extends WXModule {

    /**
     * 下载文件的方法，带有进度的
     *
     * @param params   相关参数包含url, fileId, fileName
     * @param success  下载成功的回调
     * @param failure  下载失败的回调
     * @param progress 下载进度的回调
     */
    @JSMethod(uiThread = true)
    public void downloadFile(String params, final JSCallback success, final JSCallback failure,
                             final JSCallback progress) {
        final Activity activity = (Activity) mWXSDKInstance.getContext();
        if (PermissionUtil.hasStoragePermission(activity)) {
            // 如果有存储权限就进行下载
            if (TextUtils.isEmpty(params)) {
                failure.invoke("下载参数丢失，请重试！");
                return;
            }
            ParamsEvent paramsEvent = new Gson().fromJson(params, ParamsEvent.class);
            String fileId = paramsEvent.getFileId() == null ? "" : paramsEvent.getFileId();
            String fileName = paramsEvent.getFileName();
            String url = paramsEvent.getUrl();
            String token = paramsEvent.getToken();
            String saveDir = activity.getExternalCacheDir() + "/" + fileId;
            DownloadFileUtil.getInstance().downloadFile(DownloadFileUtil.getRequest(token, url),
                    saveDir, fileName, new onDownloadListener() {
                        @Override
                        public void onSuccess(String path) {
                            if (success != null) {
                                success.invoke("下载完成");
                            }
//                            try {
//                                activity.startActivity(openFile(path));
//                            } catch (ActivityNotFoundException e) {
//                                ToastUtil.getInstance().showToast(activity.getResources()
//                                        .getString(R.string.no_find_app));
//                            }
                        }

                        @Override
                        public void onFailure(String reason) {
                            if (failure != null) {
                                failure.invoke("下载失败：" + reason);
                            }
                        }

                        @Override
                        public void onProgress(int pro) {
                            if (progress != null) {
                                progress.invokeAndKeepAlive(pro);
                            }
                        }
                    });

        } else {
            // 如果没有有存储权限就去申请
            WxDownloadFileActivity.start(mWXSDKInstance.getContext(), params, success, failure,
                    progress);
        }
    }

    /**
     * 判断文件是否存在
     *
     * @param params
     * @param resultCallback
     */
    @JSMethod(uiThread = true)
    public void isFileExist(String params, JSCallback resultCallback) {
        ParamsEvent event = new Gson().fromJson(params, ParamsEvent.class);
        if (TextUtils.isEmpty(event.getFileId()) || TextUtils.isEmpty(event.getFileName())) {
            resultCallback.invoke(false);
            return;
        }
        final Activity activity = (Activity) mWXSDKInstance.getContext();
        String filePath = mWXSDKInstance.getContext().getExternalCacheDir() + "/" + event.getFileId()
                + "/" + event.getFileName();
        if (PermissionUtil.hasStoragePermission(activity)) {
            if (FileUtil.isFileExist(filePath)) {
                resultCallback.invoke(true);
            } else {
                resultCallback.invoke(false);
            }
        } else {
            PermissionActionActivity.start(mWXSDKInstance.getContext(), ACTION_ONE, filePath,
                    resultCallback);
        }
    }

    /**
     * 预览文件
     *
     * @param params
     */
    @JSMethod(uiThread = true)
    public void previewFile(String params) {
        ParamsEvent event = new Gson().fromJson(params, ParamsEvent.class);
        if (TextUtils.isEmpty(event.getFileId()) || TextUtils.isEmpty(event.getFileName())) {
            ToastUtil.getInstance().showToast("参数错误，文件打开失败！");
            return;
        }
        String filePath = mWXSDKInstance.getContext().getExternalCacheDir() + "/" +
                event.getFileId() + "/" + event.getFileName();
        final Activity activity = (Activity) mWXSDKInstance.getContext();
        if (PermissionUtil.hasStoragePermission(activity)) {
            if (new File(filePath).exists()) {
                try {
                    activity.startActivity(openFile(filePath));
                } catch (ActivityNotFoundException e) {
                    ToastUtil.getInstance().showToast(activity.getResources()
                            .getString(R.string.no_find_app));
                }
            } else {
                ToastUtil.getInstance().showToast(activity.getResources()
                        .getString(R.string.file_not_exist));
            }
        } else {
            PermissionActionActivity.start(mWXSDKInstance.getContext(), ACTION_TWO, filePath,
                    null);
        }
    }

    /**
     * 跳转至应用市场的评价界面
     */
    @JSMethod(uiThread = true)
    public void marketComment() {
        String appId = mWXSDKInstance.getContext().getPackageName();
        try {
            if (android.os.Build.MANUFACTURER.equals("samsung")) {
                // 如果当前手机是三星手机
                Uri uri = Uri.parse("http://www.samsungapps.com/appquery/appDetail.as?appId=" + appId);
                Intent goToMarket = new Intent();
                goToMarket.setClassName("com.sec.android.app.samsungapps",
                        "com.sec.android.app.samsungapps.Main");
                goToMarket.setData(uri);
                mWXSDKInstance.getContext().startActivity(goToMarket);
            } else {
                // 如果当前是其他类型的手机
                Uri uri = Uri.parse("market://details?id=" + appId);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mWXSDKInstance.getContext().startActivity(intent);
            }
        } catch (Exception e) {
            ToastUtil.getInstance().showToast("您的手机没有安装Android应用市场");
            e.printStackTrace();
        }
    }
}
