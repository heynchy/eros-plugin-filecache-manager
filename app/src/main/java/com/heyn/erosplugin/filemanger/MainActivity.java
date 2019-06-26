package com.heyn.erosplugin.filemanger;

import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.heyn.erosplugin.wx_filemanger.activity.PermissionActionActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/1.apk";
//        Log.i("chy1234", "path===" + apkPath);
        String apkPath = this.getPackageCodePath(); // 获取Apk包存储路径
        try {
            MessageDigest dexDigest = MessageDigest.getInstance("MD5");
            byte[] bytes = new byte[1024];
            int byteCount;
            FileInputStream fis = new FileInputStream(new File(apkPath)); // 读取apk文件
            while ((byteCount = fis.read(bytes)) != -1) {
                dexDigest.update(bytes, 0, byteCount);
            }
            BigInteger bigInteger = new BigInteger(1, dexDigest.digest()); // 计算apk文件的哈希值
            Log.i("chy1234", "sha1====" + bigInteger);
            String sha = bigInteger.toString(16);
            Log.i("chy1234", "sha=2===" + sha);
            Log.i("chy1234", "sha=3===" + sha.length());
            Log.i("chy1234", "sha=4===" + getFileMD5(new File(apkPath)));
            fis.close();
//            success.invoke(sha);
        } catch (NoSuchAlgorithmException e) {
            Log.i("chy1234", "sha=3===" + e);
//            failure.invoke(e.getMessage());
        } catch (FileNotFoundException e) {
            Log.i("chy1234", "sha=3===" + e);
//            failure.invoke(e.getMessage());
        } catch (IOException e) {
            Log.i("chy1234", "sha=3===" + e);
        }
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return bytesToHexString(digest.digest());
    }

    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

}
