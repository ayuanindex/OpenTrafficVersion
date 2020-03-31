package com.realmax.opentrafficversion.ftp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;

import com.realmax.opentrafficversion.utils.L;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * FTP封装类.
 */
public class FTPUtil {
    private int port = 21;
    /**
     * 服务器名.
     */
    private String hostName = "212.64.85.235";

    /**
     * 用户名.
     */
    private String userName = "driving";

    /**
     * 密码.
     */
    private String password = "123456";

    /**
     * FTP连接.
     */
    private FTPClient ftpClient;

    /**
     * FTP根目录.
     */
    private String urlPath = "/upload/";

    /**
     * FTP当前目录.
     */
    private String currentPath = "";

    /**
     * 统计流量.
     */
    private double response;

    /**
     * 构造函数.
     */
    public FTPUtil() {
        this.ftpClient = new FTPClient();
    }

    /**
     * 打开FTP服务.
     *
     * @throws IOException
     */
    public boolean openConnect() throws IOException {
        // 中文转码
        ftpClient.setControlEncoding("UTF-8");
        int reply; // 服务器响应值
        // 连接至服务器
        ftpClient.connect(hostName, port);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        }
        // 登录到服务器
        boolean login = ftpClient.login(userName, password);
        // 获取响应值
        reply = ftpClient.getReplyCode();
        if (!FTPReply.isPositiveCompletion(reply)) {
            // 断开连接
            ftpClient.disconnect();
            throw new IOException("connect fail: " + reply);
        } else {
            // 获取登录信息
            FTPClientConfig config = new FTPClientConfig(ftpClient.getSystemType().split(" ")[0]);
            config.setServerLanguageCode("zh");
            ftpClient.configure(config);
            // 使用被动模式设为默认
            ftpClient.enterLocalPassiveMode();
            // 二进制文件支持
            ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
            System.out.println("login");
        }

        return login;
    }

    /**
     * 关闭FTP服务.
     *
     * @throws IOException
     */
    public void closeConnect() throws IOException {
        if (ftpClient != null) {
            if (ftpClient.isConnected()) {
                // 登出FTP
                ftpClient.logout();
                // 断开连接
                ftpClient.disconnect();
                System.out.println("logout");
            }
        }
    }

    /**
     * 上传.
     *
     * @param localFile 本地文件
     * @param name
     * @return
     * @throws IOException
     */
    public boolean uploading(File localFile, String name) throws IOException {
        // 初始化FTP当前目录
        currentPath = urlPath;
        // 初始化当前流量
        response = 0;
        // 二进制文件支持
        ftpClient.setFileType(org.apache.commons.net.ftp.FTP.BINARY_FILE_TYPE);
        // 使用被动模式设为默认
        ftpClient.enterLocalPassiveMode();
        // 设置模式
        ftpClient.setFileTransferMode(org.apache.commons.net.ftp.FTP.STREAM_TRANSFER_MODE);
        // 改变FTP目录
        ftpClient.changeWorkingDirectory(urlPath);
        // 上传单个文件
        return uploadingSingle(localFile, name);
    }

    /**
     * 上传单个文件.
     *
     * @param localFile 本地文件
     * @param fileName
     * @return true上传成功, false上传失败
     * @throws IOException
     */
    private boolean uploadingSingle(File localFile, String fileName) throws IOException {
        boolean flag = true;
        // 创建输入流
        InputStream inputStream = new FileInputStream(localFile);
        // 上传单个文件
        flag = ftpClient.storeFile(fileName + ".png", inputStream);
        L.e("上传状态" + flag);
        // 关闭文件流
        inputStream.close();
        return flag;
    }

    /**
     * bitmap转File
     *
     * @param bitmap 需要转换的bitmap
     */
    @SuppressLint("SimpleDateFormat")
    public static File compressImage(Bitmap bitmap, String filename, Result result) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            int options = 100;
            while (baos.toByteArray().length / 1024 > 500) {  //循环判断如果压缩后图片是否大于500kb,大于继续压缩
                baos.reset();//重置baos即清空baos
                options -= 10;//每次都减少10
                bitmap.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
                long length = baos.toByteArray().length;
            }
            File file = new File(Environment.getExternalStorageDirectory(), "/" + filename + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
            result.success(file);
            return file;
        } catch (FileNotFoundException e) {
            L.e(e.getMessage());
            compressImage(bitmap, filename, result);
            e.printStackTrace();
        } catch (Exception e) {
            L.e("出现错误" + e.getMessage());
            compressImage(bitmap, filename, result);
        }
        return null;
    }

    public interface Result {
        void success(File file) throws IOException;
    }
}
