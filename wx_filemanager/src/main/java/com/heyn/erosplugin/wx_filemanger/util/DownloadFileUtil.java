package com.heyn.erosplugin.wx_filemanger.util;


import com.heyn.erosplugin.wx_filemanger.customInterface.onDownloadListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
     * @param url      下载路径
     * @param saveDir 本地保存文件的绝对路径
     * @param fileName 文件名称
     * @param listener 下载过程监听器
     */
    public void downloadFile(final String url, final String saveDir, final String fileName, final onDownloadListener listener) {
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
                        listener.onFailure("下载失败！");
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
}
