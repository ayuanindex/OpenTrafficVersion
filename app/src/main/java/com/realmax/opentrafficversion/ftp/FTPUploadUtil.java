package com.realmax.opentrafficversion.ftp;

import android.graphics.Bitmap;

import com.realmax.opentrafficversion.utils.L;

public class FTPUploadUtil {
    private static String host = "212.64.85.235";
    private static int port = 21;
    private static String user = "driving";
    private static String password = "123456";
    private static String path = "http://driving.zuto360.com/upload/";

    /**
     * 上传图片
     *
     * @param fileName 上传图片的名称
     * @param bitmap   需要上传的图片
     */
    public static void uploadImg(String fileName, Bitmap bitmap) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                L.e("上传中");
            }
        }.start();
    }
}
