package com.heyn.erosplugin.wx_filemanger.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.benmu.framework.manager.ManagerFactory;
import com.benmu.framework.manager.impl.ParseManager;
import com.google.gson.Gson;
import com.heyn.erosplugin.wx_filemanger.customInterface.onUploadListener;
import com.heyn.erosplugin.wx_filemanger.event.FileEvent;
import com.heyn.erosplugin.wx_filemanger.util.DownloadFileUtil;
import com.heyn.erosplugin.wx_filemanger.util.FileUtil;
import com.heyn.erosplugin.wx_filemanger.util.PermissionUtil;
import com.heyn.erosplugin.wx_filemanger.util.ToastUtil;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static com.heyn.erosplugin.wx_filemanger.util.Constant.DATA_PARAMAS;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.FAILURE_CALLBACK;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.PROGRESS_CALLBACK;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.SUCCESS_CALLBACK;
import static com.heyn.erosplugin.wx_filemanger.util.FileUtil.getAndroidUri;
import static com.heyn.erosplugin.wx_filemanger.util.PermissionUtil.REQUEST_EXTERNAL_STORAGE;


/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce: 文件管理操作的活动窗口
 */
public class WxUploadFileActivity extends Activity implements IWXRenderListener {
    private JSCallback mSuccessCB;
    private JSCallback mFailureCB;
    private JSCallback mProgressCB;
    private String mDataParams;
    private FileEvent mEvent;

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
        mEvent = ManagerFactory.getManagerService(ParseManager.class).parseObject
            (mDataParams, FileEvent.class);
    }

    /**
     * 检查本地存储权限是否存在（不存在的就去申请）
     */
    private void checkPermission() {
        if (PermissionUtil.hasStoragePermission(this)) {
            if (TextUtils.isEmpty(mEvent.getFileFolderPath())){
                showFileChooser(mEvent.getType());  // 选择文件
            } else {
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                        mEvent.getFileFolderPath() ;
                openAssignFolder(filePath);
            }

        } else {
            PermissionUtil.getStoragePermissions(this);// 主动申请权限
        }
    }

    /**
     * 上传资料时打开的文件选择器
     */
    private void showFileChooser(String type) {
        if (TextUtils.isEmpty(type)) {
            type = "*/*";
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(type);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(intent, 1111);
        } catch (ActivityNotFoundException ex) {
            ToastUtil.getInstance().showToast("没有找到文件管理器");
        }
    }

    /**
     * 上传资料时打开的文件选择器
     */
    private void openAssignFolder(String path) {
        String type = mEvent.getType();
        if (TextUtils.isEmpty(type)) {
            type = "*/*";
        }
        File file = new File(path);
        if(null==file || !file.exists()){
            ToastUtil.getInstance().showToast("没有找到对应的文件夹, 请重试！");
            finish();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(getAndroidUri(path), type);
        try {
            startActivityForResult(intent, 1111);
        } catch (ActivityNotFoundException ex) {
            ToastUtil.getInstance().showToast("没有找到文件管理器");
        }
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
                if (TextUtils.isEmpty(mEvent.getFileFolderPath())){
                    showFileChooser(mEvent.getType());  // 选择文件
                } else {
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            mEvent.getFileFolderPath() ;
                    openAssignFolder(filePath);
                }
            }
        }
        finish();

    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1111:
                // 获取选中文件的Uri地址
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    if (uri == null) {
                        if (mFailureCB != null) {
                            mFailureCB.invoke("没有找到对应的文件");
                        }
                        finish();
                        return;
                    }
                    String path = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        path = FileUtil.getPathByUrikitkat(this, uri);
                    } else {
                        path = FileUtil.getPathByUriOld(this, uri);
                    }
                    if (path == null) {
                        path = "";
                    }
                    // 根据相应的接口上传资源至服务器
                    final File file = new File(path);
                    if (!file.exists()) {
                        if (mFailureCB != null) {
                            mFailureCB.invoke("没有找到对应的文件");
                        }
                        finish();
                        return;
                    }
                    // 文件上传
                    if (mEvent == null || TextUtils.isEmpty(mEvent.getUrl())) {
                        if (mFailureCB != null) {
                            mFailureCB.invoke("请求链接异常！");
                        }
                        finish();
                        return;
                    }
                    Map mapHeaders = new Gson().fromJson(mEvent.getHeader(), HashMap.class);
                    Map paramMap = new Gson().fromJson(mEvent.getParams(), HashMap.class);
                    DownloadFileUtil.getInstance().uploadFile(DownloadFileUtil.getRequest(
                        mapHeaders, paramMap, mEvent.getUrl(), file, "", mProgressCB),
                        new onUploadListener() {
                            @Override
                            public void onSuccess(String path) {
                                if (mSuccessCB != null) {
                                    mSuccessCB.invoke(path);
                                }
                                finish();
                            }

                            @Override
                            public void onFailure(String reason) {
                                if (mFailureCB != null) {
                                    mFailureCB.invoke(reason);
                                }
                                finish();
                            }
                        });
                }
                break;
            default:
                if (mFailureCB != null) {
                    mFailureCB.invoke("没有找到该文件！");
                }
                finish();
        }
    }

    /**
     * 跳转至该界面的静态方法
     *
     * @param context
     */
    public static final void start(Context context, String dataParams, JSCallback success, JSCallback failure,
                                   JSCallback progress) {
        Intent intent = new Intent(context, WxUploadFileActivity.class);
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
}
