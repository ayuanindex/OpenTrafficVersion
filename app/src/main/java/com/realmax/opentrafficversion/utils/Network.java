package com.realmax.opentrafficversion.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.realmax.baiduapitest.App;
import com.realmax.baiduapitest.Values;
import com.realmax.baiduapitest.bean.ORCBean;
import com.realmax.baiduapitest.bean.TokenBean;

import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.ContentValues.TAG;
import static com.realmax.baiduapitest.util.EncodeAndDecode.bitmapToBase64;

/**
 * 封装了Retrofit的网络请求工具类
 */
public class Network {
    private static Retrofit retrofit;
    //网络请求读写时长
    private static final int REQUEST_TIME = 30;
    public final static String BASE_URL = "https://aip.baidubce.com/";
    /**
     * 通用文字识别
     */
    public final static String strORCUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic";
    /**
     * 车牌识别
     */
    public final static String licensePlateORCUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/license_plate";
    /**
     * 手写文字识别
     */
    public final static String handwritingORCUrl = "https://aip.baidubce.com/rest/2.0/ocr/v1/handwriting";

    // 构建一个Retrofit
    private static Retrofit getRetrofit() {
        if (retrofit != null)
            return retrofit;


        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(REQUEST_TIME, TimeUnit.SECONDS)
                .connectTimeout(REQUEST_TIME, TimeUnit.SECONDS)
                .build();

        retrofit = new Retrofit.Builder().baseUrl(BASE_URL)
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
    public static <T> T remote(Class<T> clazz) {
        return Network.getRetrofit().create(clazz);
    }

    public interface ResultData<T> {
        void result(T t);
    }

    /**
     * 百度API识别后返回一个json对象
     *
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static <T> void getORCString(Drawable drawable, String URL, Class<T> tClass, ResultData<? super T> resultData) {
        try {
            Bitmap bitmap = EncodeAndDecode.drawableToBitmap(drawable);
            String bitmapToBase64 = bitmapToBase64(bitmap);
            String imgParam = URLEncoder.encode(bitmapToBase64, "UTF-8");
            String param = "image=" + imgParam;
            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String result = HttpUtil.post(URL, Values.getToken(), param);
            Log.i(TAG, "拿到了识别文字：" + result);
            resultData.result(new Gson().fromJson(result, tClass));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Token
     */
    @SuppressLint("CheckResult")
    public static void getTokenString() {
        App.getRemote().getToken(Values.getGrant_type(), Values.getClientId(), Values.getClientSecret())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TokenBean>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void accept(TokenBean tokenBean) throws Exception {
                        Values.setToken(tokenBean.getAccess_token());
                        Log.i(TAG, "拿到了token：" + tokenBean.getAccess_token());
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.i(TAG, "出现错误：" + throwable.getMessage());

                    }
                });
    }

}
