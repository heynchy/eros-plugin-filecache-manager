package com.heyn.erosplugin.wx_filemanger.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


import com.google.gson.Gson;
import com.heyn.erosplugin.wx_filemanger.R;
import com.heyn.erosplugin.wx_filemanger.customInterface.onDownloadListener;
import com.heyn.erosplugin.wx_filemanger.event.ParamsEvent;
import com.heyn.erosplugin.wx_filemanger.util.DownloadFileUtil;
import com.heyn.erosplugin.wx_filemanger.util.FileUtil;
import com.heyn.erosplugin.wx_filemanger.util.PermissionUtil;
import com.heyn.erosplugin.wx_filemanger.util.ToastUtil;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.StatusUtil;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;

import java.io.File;
import java.util.List;
import java.util.Map;

import static com.heyn.erosplugin.wx_filemanger.util.Constant.DATA_PARAMAS;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.FAILURE_CALLBACK;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.PROGRESS_CALLBACK;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.SUCCESS_CALLBACK;
import static com.heyn.erosplugin.wx_filemanger.util.PermissionUtil.REQUEST_EXTERNAL_STORAGE;


/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce: 文件管理操作的活动窗口
 */
public class WxDownloadFileActivity extends Activity implements IWXRenderListener {
    private JSCallback mSuccessCB;
    private JSCallback mFailureCB;
    private JSCallback mProgressCB;
    private String mDataParams;
    private boolean mBreakPoint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();            // 初始化配置
        checkPermission(); // 检查权限
    }


    /**
     * 初始化基本配置
     */
    private void init() {
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        params.alpha = 0.0f;
        window.setAttributes(params);
        mSuccessCB = (JSCallback) getIntent().getSerializableExtra(SUCCESS_CALLBACK);
        mFailureCB = (JSCallback) getIntent().getSerializableExtra(FAILURE_CALLBACK);
        mProgressCB = (JSCallback) getIntent().getSerializableExtra(PROGRESS_CALLBACK);
        mDataParams = getIntent().getStringExtra(DATA_PARAMAS);
        mBreakPoint = getIntent().getBooleanExtra("Break_Point", true);
    }

    /**
     * 检查本地存储权限是否存在（不存在的就去申请）
     */
    private void checkPermission() {
        if (PermissionUtil.hasStoragePermission(this)) {
            // 下载文件
            if (mBreakPoint){
                downloadBreakPoint();
            } else {
                downloadFile();
            }
        } else {
            PermissionUtil.getStoragePermissions(this);// 主动申请权限
        }
    }

    /**
     * 下载文件
     */
    private void downloadFile() {
        if (TextUtils.isEmpty(mDataParams)) {
            mFailureCB.invoke("下载参数错误");
            finish();
            return;
        }
        ParamsEvent paramsEvent = new Gson().fromJson(mDataParams, ParamsEvent.class);
        String fileId = paramsEvent.getFileId() == null ? "" : paramsEvent.getFileId();
        String fileName = paramsEvent.getFileName();
        String url = paramsEvent.getUrl();
        String token = paramsEvent.getToken();
        String saveDir = getExternalCacheDir() + "/" + fileId;
        DownloadFileUtil.getInstance().downloadFile(DownloadFileUtil.getRequest(token, url),
                saveDir, fileName, new onDownloadListener() {
                    @Override
                    public void onSuccess(String path) {
                        if (mSuccessCB != null) {
                            mSuccessCB.invoke("下载完成");
                        }
//                        try {
//                            startActivity(FileUtil.openFile(path));
//                        } catch (ActivityNotFoundException e) {
//                            ToastUtil.getInstance().showToast(getResources()
//                                    .getString(R.string.no_find_app));
//                        }
                        finish();
                    }

                    @Override
                    public void onFailure(String reason) {
                        if (mFailureCB != null) {
                            mFailureCB.invoke("下载失败：" + reason);
                        }
                        finish();
                    }

                    @Override
                    public void onProgress(int progress) {
                        if (mProgressCB != null) {
                            mProgressCB.invokeAndKeepAlive(progress);
                        }
                    }
                });
    }

    /**
     * 根据权限的返回值进行相关处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获取存储权限成功
                if (mBreakPoint){
                    downloadBreakPoint();
                } else {
                    downloadFile();
                }

            }
        }
        finish();

    }


    /**
     * 跳转至该界面的静态方法
     *
     * @param context
     */
    public static final void start(Context context, String dataParams, JSCallback success, JSCallback failure,
                                   JSCallback progress, boolean breakPoint) {
        Intent intent = new Intent(context, WxDownloadFileActivity.class);
        intent.putExtra(DATA_PARAMAS, dataParams);
        Bundle bundle = new Bundle();
        if (success != null) {
            bundle.putSerializable(SUCCESS_CALLBACK, success);
        }
        if (failure != null) {
            bundle.putSerializable(FAILURE_CALLBACK, failure);
        }
        if (progress != null) {
            bundle.putSerializable(PROGRESS_CALLBACK, progress);
        }
        bundle.putBoolean("Break_Point",breakPoint);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public void onViewCreated(WXSDKInstance instance, View view) {

    }

    @Override
    public void onRenderSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onRefreshSuccess(WXSDKInstance instance, int width, int height) {

    }

    @Override
    public void onException(WXSDKInstance instance, String errCode, String msg) {

    }

    /**
     * 断点下载文件
     */
    private void downloadBreakPoint() {
        if (TextUtils.isEmpty(mDataParams) && mFailureCB!= null ) {
            mFailureCB.invoke("下载参数错误");
            finish();
            return;
        }
        ParamsEvent paramsEvent = new Gson().fromJson(mDataParams, ParamsEvent.class);
        String fileId = paramsEvent.getFileId() == null ? "" : paramsEvent.getFileId();
        String fileName = paramsEvent.getFileName();
        String url = paramsEvent.getUrl();
        String token = paramsEvent.getToken();
        String saveDir = getExternalCacheDir() + "/" + fileId;
        DownloadTask.Builder builder = new DownloadTask.Builder(url, new File(saveDir));
        if (!TextUtils.isEmpty(token)){
            builder.addHeader("Authorization", token);
        }
        builder.setFilename(fileName)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(30)
                // do re-download even if the task has already been completed in the past.
                .setPassIfAlreadyCompleted(false);
        builder.build().enqueue(new DownloadListener4WithSpeed() {
            @Override
            public void taskStart(@NonNull DownloadTask task) {

            }

            @Override
            public void connectStart(@NonNull DownloadTask task, int blockIndex, @NonNull Map<String, List<String>> requestHeaderFields) {

            }

            @Override
            public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode, @NonNull Map<String, List<String>> responseHeaderFields) {

            }

            @Override
            public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info, boolean fromBreakpoint, @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {

            }

            @Override
            public void progressBlock(@NonNull DownloadTask task, int blockIndex, long currentBlockOffset, @NonNull SpeedCalculator blockSpeed) {

            }

            @Override
            public void progress(@NonNull DownloadTask task, long currentOffset, @NonNull SpeedCalculator taskSpeed) {
                double length = StatusUtil.getCurrentInfo(task).getTotalLength();
                int percent = (int) (currentOffset/length *100);
                if (mProgressCB != null) {
                    mProgressCB.invokeAndKeepAlive(percent);
                }
            }

            @Override
            public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info, @NonNull SpeedCalculator blockSpeed) {

            }

            @Override
            public void taskEnd(@NonNull DownloadTask task, @NonNull EndCause cause, @Nullable Exception realCause, @NonNull SpeedCalculator taskSpeed) {
                if (StatusUtil.isCompleted(task) && mSuccessCB != null) {
                    mSuccessCB.invoke("下载完成");
                } else if (mFailureCB != null) {
                    if (realCause != null) {
                        mFailureCB.invoke("下载失败" + realCause.getMessage());
                    } else {
                        mFailureCB.invoke("下载失败");
                    }
                }
                finish();
            }
        });
    }
}
