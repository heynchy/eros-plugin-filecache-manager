package com.heyn.erosplugin.wx_filemanger.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.heyn.erosplugin.wx_filemanger.R;
import com.heyn.erosplugin.wx_filemanger.util.FileUtil;
import com.heyn.erosplugin.wx_filemanger.util.PermissionUtil;
import com.heyn.erosplugin.wx_filemanger.util.ToastUtil;
import com.taobao.weex.IWXRenderListener;
import com.taobao.weex.WXSDKInstance;
import com.taobao.weex.bridge.JSCallback;

import java.io.File;

import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_FOUR;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_NUM;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_ONE;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_THREE;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.ACTION_TWO;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.CUSTOM_CALLBACK;
import static com.heyn.erosplugin.wx_filemanger.util.Constant.DATA_PARAMAS;
import static com.heyn.erosplugin.wx_filemanger.util.PermissionUtil.REQUEST_EXTERNAL_STORAGE;

/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce: 相关权限行为处理的Activity
 */
public class PermissionActionActivity extends Activity implements IWXRenderListener {
    private int mAction;          // 操作行为
    private String mParamas;      // 传递的参数
    private JSCallback mCallback; // 回调参数


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        checkPermission();
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
        mAction = getIntent().getIntExtra(ACTION_NUM, -1);
        mParamas = getIntent().getStringExtra(DATA_PARAMAS);
        mCallback = (JSCallback) getIntent().getSerializableExtra(CUSTOM_CALLBACK);
    }

    /**
     * 检查本地存储权限是否存在（不存在的就去申请）
     */
    private void checkPermission() {
        if (PermissionUtil.hasStoragePermission(this)) {
            finish();
        } else {
            PermissionUtil.getStoragePermissions(this);
        }
    }

    /**
     * 主动获取权限后的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (REQUEST_EXTERNAL_STORAGE == requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获取存储权限成功
                if (mAction == -1) {
                    finish();
                    return;
                }
                switch (mAction) {
                    case ACTION_ONE: // 文件是否存在的操作（有params(此处为绝对路径，已处理过) 和 callback）
                        if (TextUtils.isEmpty(mParamas) || mCallback == null) {
                            finish();
                            return;
                        }
                        if (FileUtil.isFileExist(mParamas)) {
                            mCallback.invoke(true);
                        } else {
                            mCallback.invoke(false);
                        }
                        break;
                    case ACTION_TWO: // 文件预览的操作 (仅有paramas(此处为绝对路径，已处理过))
                        if (new File(mParamas).exists()) {
                            try {
                                startActivity(FileUtil.openFile(mParamas));
                            } catch (ActivityNotFoundException e) {
                                ToastUtil.getInstance().showToast(getResources()
                                        .getString(R.string.no_find_app));
                            }
                        } else {
                            ToastUtil.getInstance().showToast(getResources()
                                    .getString(R.string.file_not_exist));
                        }
                        break;
                    case ACTION_THREE: // 清除缓存的操作（仅有callback）
                        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                            FileUtil.deleteFiles(getExternalCacheDir());
                            FileUtil.deleteFiles(getCacheDir());
                            if (mCallback != null) {
                                mCallback.invoke(1);
                            }
                        } else {
                            if (mCallback != null) {
                                mCallback.invoke(0);
                            }
                        }
                        break;
                    case ACTION_FOUR:  // 获取缓存大小的操作（仅有callback）
                        try {
                            long size = FileUtil.getFolderSize(getExternalCacheDir());
                            size = size + FileUtil.getFolderSize(getCacheDir());
                            mCallback.invoke(FileUtil.getFormatSize(size));
                        } catch (Exception e) {
                            mCallback.invoke(getResources().getString(R.string.default_size));
                        }
                        break;
                }
            }
        }
        finish();
    }

    /**
     * 跳转至该页面的静态方法
     *
     * @param context
     */
    public static void start(Context context, int action, String params, JSCallback callback) {
        Intent intent = new Intent(context, PermissionActionActivity.class);
        intent.putExtra(ACTION_NUM, action);
        intent.putExtra(DATA_PARAMAS, params);
        Bundle bundle = new Bundle();
        bundle.putSerializable(CUSTOM_CALLBACK, callback);
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
