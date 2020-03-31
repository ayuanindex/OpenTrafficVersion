package com.realmax.opentrafficversion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;
import com.realmax.opentrafficversion.utils.Network;

import static com.realmax.opentrafficversion.Values.BASE_URL;

public class App extends Application {
    private static ProgressDialog progressDialog;
    private static Gson gson;
    private static Context context;
    private static ApiService remote;
    private static Toast toast;

    @SuppressLint("ShowToast")
    @Override
    public void onCreate() {
        super.onCreate();
        gson = new Gson();
        context = this;
        toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
        remote = Network.remote(ApiService.class, BASE_URL);
    }

    public static Context getContext() {
        return context;
    }

    public static Gson getGson() {
        return gson;
    }

    public static void showToast(String msg) {
        toast.cancel();
        toast = null;
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static ApiService getRemote() {
        return remote;
    }

    public static void showDialog(Activity activity, String msg) {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                return;
            }
        }

        progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("提示");
        progressDialog.setMessage(msg);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

    public static void disDialog(FragmentActivity activity) {
        if (progressDialog != null) {
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    showToast("连接失败");
                }
            }.start();
        }
    }
}
