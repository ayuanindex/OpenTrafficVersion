package com.realmax.opentrafficversion.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.realmax.opentrafficversion.App;
import com.realmax.opentrafficversion.Values;
import com.realmax.opentrafficversion.tencentCloud.OkHttpUtil;
import com.realmax.opentrafficversion.tencentCloud.TencentCloudAPIDemo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static com.realmax.opentrafficversion.utils.EncodeAndDecode.bitmapToBase64;

/**
 * 封装了Retrofit的网络请求工具类
 */
public class Network {
    private static Retrofit retrofit;

    // 构建一个Retrofit
    private static Retrofit getRetrofit(String baseUrl) {
        if (retrofit != null)
            return retrofit;


        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(Values.REQUEST_TIME, TimeUnit.SECONDS)
                .connectTimeout(Values.REQUEST_TIME, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder().baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(App.getGson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit;

    }

    /**
     * 返回一个请求代理
     *
     * @return
     */
    public static <T> T remote(Class<T> clazz, String baseUrl) {
        return Network.getRetrofit(baseUrl).create(clazz);
    }

    public interface ResultData<T> {
        void result(T t);

        void error();
    }

    /**
     * 百度API识别后返回一个json对象
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static <T> void getOrcString(Bitmap bitmap, String URL, Class<T> tClass, ResultData<? super T> resultData) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    /*Bitmap bitmap = EncodeAndDecode.drawableToBitmap(drawable);*/
                    String bitmapToBase64 = bitmapToBase64(bitmap);
                    String imgParam = URLEncoder.encode(bitmapToBase64, "UTF-8");
                    String param = "image=" + imgParam;
                    // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
                    String result = HttpUtil.post(URL, Values.TOKEN, param);
                    Log.i(TAG, "拿到了识别文字：" + result);
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.has("error_code")) {
                        resultData.result(null);
                    } else {
                        resultData.result(new Gson().fromJson(result, tClass));
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    resultData.error();
                } catch (UnsupportedEncodingException | JSONException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    /**
     * 腾讯云车牌识别
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static <T> void getOrcString(Bitmap bitmap, Class<T> tClass, OkHttpUtil.Result<T> result) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    TreeMap<String, Object> params = TencentCloudAPIDemo.getParams(bitmap);
                    OkHttpUtil.doPost(params, tClass, result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
