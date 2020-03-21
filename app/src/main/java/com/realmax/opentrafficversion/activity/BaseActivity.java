package com.realmax.opentrafficversion.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.realmax.opentrafficversion.App;
import com.realmax.opentrafficversion.Values;
import com.realmax.opentrafficversion.bean.TokenBean;
import com.realmax.opentrafficversion.utils.EncodeAndDecode;
import com.realmax.opentrafficversion.utils.TCPLinks;

import java.net.Socket;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    public static Socket remoteSocket = null;
    public static Socket cameraSocket = null;
    private boolean cameraFlag = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        requiredParameter();
        initView();
        initEvent();
        initData();
    }

    /**
     * 获取布局文件ID
     *
     * @return 返回布局文件ID
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * 初始化控件
     */
    protected abstract void initView();

    /**
     * 初始化监听
     */
    protected abstract void initEvent();

    /**
     * 获取token
     */
    private void requiredParameter() {
        if ("".equals(Values.TOKEN)) {
            getTokenString();
        }
    }

    public String getImageData(String imageData) {
        return imageData;
    }

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 界面跳转
     *
     * @param jump 需要跳转的界面
     */
    public <T> void jump(Class<T> jump) {
        startActivity(new Intent(this, jump));
    }

    /**
     * 获取Token
     */
    @SuppressLint("CheckResult")
    public void getTokenString() {
        App.getRemote().getToken(Values.GRANT_TYPE, Values.CLIENT_ID, Values.CLIENT_SECRET)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<TokenBean>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void accept(TokenBean tokenBean) throws Exception {
                        Values.TOKEN = tokenBean.getAccess_token();
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
