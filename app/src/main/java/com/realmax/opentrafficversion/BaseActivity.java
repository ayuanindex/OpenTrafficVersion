package com.realmax.opentrafficversion;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.realmax.opentrafficversion.bean.TokenBean;

import java.net.Socket;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    public final String TAG = this.getClass().getSimpleName();
    public Socket remoteSocket = null;
    public Socket cameraSocket = null;

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

    /**
     * 初始化数据
     */
    protected abstract void initData();

    /**
     * 建立连接后返回处理过的流调用的方法
     *
     * @param type
     * @param msg
     */
    protected abstract void messageResult(String type, String msg);

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
