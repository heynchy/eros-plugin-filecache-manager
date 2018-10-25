package com.heyn.erosplugin.wx_filemanger.util;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;


import com.heyn.erosplugin.wx_filemanger.Application;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: 崔海营
 * Date:   2018/9/19
 * <p>
 * Introduce: 文件操作的工具类
 */
public class FileUtil {
    static InputStream in;
    static BufferedReader br;
    public static final String PACKAGE_NAME =
        Application.getAppContext().getPackageName() + ".fileProvider";

    public static Intent openFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return null;
        /* 取得扩展名 */
        String end = file.getName().substring(file.getName().lastIndexOf(".") + 1, file.getName().length()).toLowerCase();
        /* 依扩展名的类型决定MimeType */
        if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
            end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
            return getAudioFileIntent(filePath);
        } else if (end.equals("3gp") || end.equals("mp4")) {
            return getVideoFileIntent(filePath);
        } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
            end.equals("jpeg") || end.equals("bmp")) {
            return getImageFileIntent(filePath);
        } else if (end.equals("apk")) {
            return getApkFileIntent(filePath);
        } else if (end.equals("ppt")) {
            return getPptFileIntent(filePath);
        } else if (end.equals("xls")) {
            return getExcelFileIntent(filePath);
        } else if (end.equals("doc")) {
            return getWordFileIntent(filePath);
        } else if (end.equals("pdf")) {
            return getPdfFileIntent(filePath);
        } else if (end.equals("chm")) {
            return getChmFileIntent(filePath);
        } else if (end.equals("txt")) {
            return getTextFileIntent(filePath, false);
        } else {
            return getAllIntent(filePath);
        }
    }

    //Android获取一个用于打开APK文件的intent
    public static Intent getAllIntent(String param) {

        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = getAndroidUri(param);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "*/*");
        return intent;
    }

    //Android获取一个用于打开APK文件的intent
    public static Intent getApkFileIntent(String param) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        File file = new File(param);
        if (file.exists()) {
            Uri uri = getAndroidUri(param);
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        }
        return intent;
    }

    //Android获取一个用于打开VIDEO文件的intent
    public static Intent getVideoFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //Android获取一个用于打开AUDIO文件的intent
    public static Intent getAudioFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }

    //Android获取一个用于打开Html文件的intent
    public static Intent getHtmlFileIntent(String param) {

        Uri uri = Uri.parse(param).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(param).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }

    //Android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(String param) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }

    //Android获取一个用于打开PPT文件的intent
    public static Intent getPptFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //Android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //Android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //Android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //Android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(String param, boolean paramBoolean) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getAndroidUri(param, paramBoolean);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    //Android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(String param) {

        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = getAndroidUri(param);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }

    /**
     * 获取版本对应的uri
     *
     * @param param
     * @return
     */
    public static Uri getAndroidUri(String param) {
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            Log.i("heyn","PACKAGE_NAME:" + PACKAGE_NAME);
            uri = FileProvider.getUriForFile(
                Application.getAppContext(),
                PACKAGE_NAME,
                new File(param));
        } else {
            uri = Uri.fromFile(new File(param));
        }
        return uri;
    }

    /**
     * text 对应的Uri获取
     *
     * @param param
     * @param paramBoolean
     * @return
     */
    public static Uri getAndroidUri(String param, boolean paramBoolean) {
        Uri uri;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            uri = FileProvider.getUriForFile(
                    Application.getAppContext(),
                PACKAGE_NAME,
                new File(param));
        } else {
            if (paramBoolean) {
                uri = Uri.parse(param);
            } else {
                uri = Uri.fromFile(new File(param));
            }
        }
        return uri;
    }

    /**
     * 保存到SD卡
     *
     * @param filename
     * @param content
     * @throws Exception
     */
    public static String saveToSDCard(String filename, byte[] content) throws Exception {
        String filePath = Environment.getExternalStorageDirectory() + "/chem";
        File dirFirstFolder = new File(filePath);
        if (!dirFirstFolder.exists()) {
            //创建文件夹
            dirFirstFolder.mkdirs();
        }
        File file = new File(filePath, filename);
        FileOutputStream outStream = new FileOutputStream(file);
        outStream.write(content);
        outStream.close();
        return Environment.getExternalStorageDirectory() + "/chem/" + filename;
    }

    public static String saveToSDCard(String filename, InputStream in) throws IOException {
        String filePath = Environment.getExternalStorageDirectory() + "/YueYeYa";
        File dirFirstFolder = new File(filePath);
        if (!dirFirstFolder.exists()) {
            //创建文件夹
            dirFirstFolder.mkdirs();
        }
        File file = new File(filePath, filename);
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] data = new byte[500];
        int count = -1;
        while ((count = in.read(data, 0, 500)) != -1) {
            outStream.write(data, 0, count);
        }
        outStream.close();
        in.close();
        return Environment.getExternalStorageDirectory() + "/YueYeYa/" + filename;
    }

    public static String saveToSDCard(String filePath, String filename, InputStream in) throws IOException {
        File dirFirstFolder = new File(filePath);
        if (!dirFirstFolder.exists()) {
            //创建文件夹
            dirFirstFolder.mkdirs();
        }
        File file = new File(filePath, filename);
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] data = new byte[500];
        int count = -1;
        while ((count = in.read(data, 0, 500)) != -1) {
            outStream.write(data, 0, count);
        }
        outStream.close();
        in.close();
        return file.getAbsolutePath();
    }

    public static byte[] InputStreamTOByte(InputStream in) throws IOException {

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int count = -1;
        while ((count = in.read(data, 0, in.available())) != -1)
            outStream.write(data, 0, count);

        outStream.close();
        return outStream.toByteArray();
    }

    /**
     * 读取表情配置文件
     *
     * @param context
     * @return
     */
    public static List<String> getEmojiFile(Context context) {
        try {
            List<String> list = new ArrayList<String>();
            in = context.getResources().getAssets().open("emoji");
            br = new BufferedReader(new InputStreamReader(in,
                "UTF-8"));
            String str = null;
            while ((str = br.readLine()) != null) {
                list.add(str);
            }

            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭IO流
     */
    public static void close() {
        if (br != null) {
            try {
                br.close();
                br = null;
                Log.d("close", "br");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (in != null) {
            try {
                in.close();
                in = null;
                Log.d("close", "in");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据资源名获取ID
     *
     * @param fileName 资源文件名称
     * @param context
     * @return
     */
    public static int getResourceId(String fileName, Context context) {
        String imageName = fileName
            .substring(0, fileName.lastIndexOf("."));
        int resId = context.getResources().getIdentifier(imageName, "drawable", context.getPackageName());
        //如果没有在"drawable"下找到imageName,将会返回0
        Log.d("id", String.valueOf(resId));
        return resId;
    }

    /**
     * SDk 版本小于于4.4时，Uri 转换成真实路径的方法
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getPathByUriOld(Context context, Uri uri) {
        String filename = null;
        if (uri.getScheme().toString().compareTo("content") == 0) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{"_data"}, null, null, null);
            if (cursor.moveToFirst()) {
                filename = cursor.getString(0);
            }
        } else if (uri.getScheme().toString().compareTo("file") == 0) {// file:///开头的uri
            filename = uri.toString();
            filename = uri.toString().replace("file://", "");// 替换file://
            if (!filename.startsWith("/mnt")) {// 加上"/mnt"头
                filename += "/mnt";
            }
        }
        return filename;
    }

    /**
     * SDk 版本大于4.4时，Uri 转换成真实路径的方法
     *
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("NewApi") //作用仅仅是屏蔽android lint错误，所以在方法中还要判断版本做不同的操作.
    public static String getPathByUrikitkat(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {// ExternalStorageProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {// DownloadsProvider
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),
                    Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(uri)) {// MediaProvider
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && isAndroidNUri(uri)) {
                final String[] split = uri.getPath().split("/");
                StringBuilder path = new StringBuilder("");
                boolean begin = false;
                for (int i = 0; i < split.length; i++) {
                    if (!begin && split[i].equals("storage")) {
                        begin = true;
                    }
                    if (begin) {
                        path.append("/");
                        path.append(split[i]);
                    }
                }
                return path.toString();
            }
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    private static boolean isAndroidNUri(Uri uri) {
        return "com.fihtdc.filemanager.provider".equals(uri.getAuthority());
    }

    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * 保存JSON文件
     *
     * @return
     */
    public static String saveJson(Context context, String json) {
        String path;
        if (hasSdCard()) {
            path = context.getExternalFilesDir("").toString();
        } else {
            path = context.getFilesDir().toString();
        }
        File file = new File(path, "user_group.json");
        try {
            writeFile(file.getPath(), json);
            Log.d("jsonStatus", "保存本地成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getPath();
    }

    public static void writeFile(String filePath, String sets) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        PrintWriter out = new PrintWriter(fw);
        out.write(sets);
        out.println();
        fw.close();
        out.close();
    }

    public static boolean hasSdCard() {
        String sdStatus = Environment.getExternalStorageState();
        if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) {
            return false;
        }
        return true;
    }

    /**
     * 获取文件的类型
     *
     * @param filePath
     * @return
     */
    public static int mediaType(String filePath) {
        if (filePath != null) {
            String end = filePath.substring(filePath.lastIndexOf(".") + 1, filePath.length()).toLowerCase();
            /* 依扩展名的类型决定MimeType */
            if (end.equals("m4a") || end.equals("mp3") || end.equals("mid") ||
                end.equals("xmf") || end.equals("ogg") || end.equals("wav")) {
                return 1;
            } else if (end.equals("3gp") || end.equals("mp4")) {
                return 2;
            } else if (end.equals("jpg") || end.equals("gif") || end.equals("png") ||
                end.equals("jpeg") || end.equals("bmp")) {
                return 3;
            } else if (end.equals("apk")) {
                return 4;
            } else if (end.equals("ppt")) {
                return 5;
            } else if (end.equals("xls")) {
                return 6;
            } else if (end.equals("doc")) {
                return 7;
            } else if (end.equals("pdf")) {
                return 8;
            } else if (end.equals("chm")) {
                return 9;
            } else if (end.equals("txt")) {
                return 10;
            } else {
                return 11;
            }
        } else {
            return 0;
        }
    }

    /**
     * 获取文件名的后缀
     *
     * @param fileName
     * @return
     */
    public static String getEndName(String fileName) {
        if (fileName.lastIndexOf(".") == -1) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
    }

    /**
     * 判断文件是否存在
     *
     * @return
     */
    public static boolean isFileExist(String filePath) {
        File downloadFile = new File(filePath);
        return downloadFile.exists();
    }

    /**
     * 计算某一文件的大小
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static long getFolderSize(File file) throws Exception {
        long size = 0;
        try {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                // 如果下面还有文件
                if (fileList[i].isDirectory()) {
                    size = size + getFolderSize(fileList[i]);
                } else {
                    size = size + fileList[i].length();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return size;
    }


    /**
     * 递归清空某一文件夹
     *
     * @param file
     */
    public static void deleteFiles(File file) {
        if (file.exists()) {
            // 如果file是一个文件，直接删除
            if (file.isFile()) {
                file.delete();
                return;
            }
            // 如果file是一个文件夹，遍历内部的文件和文件夹，递归删除
            if (file.isDirectory()) {
                File[] chileFiles = file.listFiles();
                if (chileFiles == null || chileFiles.length == 0) {
                    file.delete();
                    return;
                }
                for (File file1 : chileFiles) {
                    deleteFiles(file1);
                }
                file.delete();
            }
        }
    }

    /**
     * 格式化单位
     *
     * @param size
     * @return
     */
    public static String getFormatSize(double size) {
        double kiloByte = size / 1024;
        if (kiloByte < 1) {
            return "0.0MB";
        }

        double megaByte = kiloByte / 1024;
        if (megaByte < 1) {
            BigDecimal result1 = new BigDecimal(Double.toString(kiloByte));
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "KB";
        }

        double gigaByte = megaByte / 1024;
        if (gigaByte < 1) {
            BigDecimal result2 = new BigDecimal(Double.toString(megaByte));
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB";
        }

        double teraBytes = gigaByte / 1024;
        if (teraBytes < 1) {
            BigDecimal result3 = new BigDecimal(Double.toString(gigaByte));
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB";
        }
        BigDecimal result4 = new BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString()
            + "TB";
    }

}
