package com.heyn.erosplugin.wx_filemanger.util;


import android.text.TextUtils;

import com.heyn.erosplugin.wx_filemanger.customInterface.OnProgressListener;
import com.heyn.erosplugin.wx_filemanger.customInterface.onDownloadListener;
import com.heyn.erosplugin.wx_filemanger.customInterface.onUploadListener;
import com.taobao.weex.bridge.JSCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by yufs on 2017/8/16.
 */

public class DownloadFileUtil {
    public static final int DOWNLOAD_FAIL = 0;
    public static final int DOWNLOAD_PROGRESS = 1;
    public static final int DOWNLOAD_SUCCESS = 2;
    private static volatile DownloadFileUtil downloadInstance;
    private final OkHttpClient okHttpClient;

    /**
     * DownloadFileUtil 的单例
     *
     * @return
     */
    public static DownloadFileUtil getInstance() {
        if (downloadInstance == null) {
            synchronized (DownloadFileUtil.class) {
                if (downloadInstance == null) {
                    downloadInstance = new DownloadFileUtil();
                }
            }
        }
        return downloadInstance;
    }

    private DownloadFileUtil() {
        okHttpClient = new OkHttpClient();
    }

    /**
     * 带有进度返回的下载
     *
     * @param request  请求的配置request
     * @param saveDir  本地保存文件的绝对路径
     * @param fileName 文件名称
     * @param listener 下载过程监听器
     */
    public void downloadFile(final Request request, final String saveDir, final String fileName, final onDownloadListener listener) {
//        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFailure(e.toString());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream is = null;
                    byte[] buf = new byte[500];
                    int len = 0;
                    FileOutputStream fos = null;
                    try {
                        is = response.body().byteStream();
                        long total = response.body().contentLength();
                        String savePath = isExistDir(saveDir);
                        File file = new File(savePath, fileName);
                        fos = new FileOutputStream(file);
                        long sum = 0;
                        while ((len = is.read(buf)) != -1) {
                            fos.write(buf, 0, len);
                            sum += len;
                            int progress = (int) (sum * 1.0f / total * 100);
                            //下载中
                            if (listener != null) {
                                listener.onProgress(progress);
                            }
                        }
                        fos.flush();
                        //下载完成
                        if (listener != null) {
                            listener.onSuccess(file.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        if (listener != null) {
                            listener.onFailure(e.toString());
                        }
                    } finally {
                        try {
                            if (is != null)
                                is.close();
                            if (fos != null) {
                                fos.close();
                            }
                        } catch (IOException e) {

                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onFailure("url请求异常，无法建立链接");
                    }
                }
            }
        });
    }

    /**
     * 下载不带进度
     *
     * @param url      请求的url
     * @param saveDir  文件的保存路径
     * @param fileName 文件名
     * @param listener 下载监听器
     */
    public void downloadNoProgress(final String url, final String saveDir, final String fileName,
                                   final onDownloadListener listener) {
        Request request = new Request.Builder().url(url).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFailure(e.toString());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    InputStream is = response.body().byteStream();
                    String filePath = FileUtil.saveToSDCard(saveDir, fileName, is);
                    if (listener != null) {
                        listener.onSuccess(filePath);
                    }
                } else {
                    if (listener != null) {
                        listener.onFailure("url请求异常，无法建立链接！");
                    }
                }
            }
        });
    }

    /**
     * 文件上传
     *
     * @param request
     * @param listener
     */
    public void uploadFile(final Request request, final onUploadListener listener) {
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (listener != null) {
                    listener.onFailure(e.toString());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (response.isSuccessful()) {
                        //下载完成
                        if (listener != null) {
                            listener.onSuccess(response.body().string());
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure("文件上传失败");
                        }
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        listener.onFailure("文件上传失败: " + e.getMessage());
                    }
                } finally {
                    if (response.body() != null) {
                        response.close();
                    }
                }
            }
        });
    }

    /**
     * 判断内存中是否存在该文件，如果不存在则新建该文件
     *
     * @param saveDir
     * @return
     * @throws IOException
     */
    private String isExistDir(String saveDir) throws IOException {
        File downloadFile = new File(saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * 根据是否存在token返回合适的Request
     *
     * @param token
     * @return
     */
    public static Request getRequest(String token, String url) {
        Request request;
        if (TextUtils.isEmpty(token)) {
            request = new Request.Builder().url(url).build();
        } else {
            request = new Request.Builder().url(url)
                    .addHeader("Authorization", token)
                    .build();
        }
        return request;
    }

    /**
     * 文件上传创建合适的Request
     *
     * @param headers 头文件的相关参数值（Map 类型）
     * @param params  其他表单参数
     * @param url     上传的接口路径
     * @return request
     */
    public static Request getRequest(Map<String, String> headers, Map<String, Object> params, String url, File file, String fileKey, final JSCallback progress) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        // 添加头文件的相关参数（token和其他）
        if (headers != null && !headers.isEmpty()) {
            Headers.Builder headerBuilder = new Headers.Builder();
            for (String key : headers.keySet()) {
                headerBuilder.add(key, headers.get(key));
            }
            requestBuilder.headers(headerBuilder.build());
        }
        MultipartBody.Builder bodyBulid = new MultipartBody.Builder();
        // 设置类型
        bodyBulid.setType(MultipartBody.FORM);
        // 追加参数
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                Object object = params.get(key);
                if (!(object instanceof File)) {
                    bodyBulid.addFormDataPart(key, object.toString());
                }
            }
        }
        if (TextUtils.isEmpty(fileKey)) {
            fileKey = "file";
        }
        bodyBulid.addFormDataPart(fileKey, file.getName(), RequestBody.create(null, file));
        // 创建ExMultipartBody代理类，使其能够返回进度值
        ExMultipartBody exMultipartBody = new ExMultipartBody(bodyBulid.build(),
                new OnProgressListener() {
                    @Override
                    public void onProgress(long total, long current) {
                        if (progress != null && total != 0) {
                            progress.invokeAndKeepAlive((int) current*100 / total);
                        }
                    }
                });
        requestBuilder.post(exMultipartBody);
        return requestBuilder.build();
    }
}
