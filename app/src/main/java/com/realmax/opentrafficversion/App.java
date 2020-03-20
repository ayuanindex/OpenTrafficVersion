package com.realmax.opentrafficversion;

import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import com.google.gson.Gson;
import com.realmax.opentrafficversion.utils.Network;

public class App extends Application {

    private static Gson gson;
    private static Context context;
    private static ApiService remote;
    private static Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();
        gson = new Gson();
        context = this;
        toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
        remote = Network.remote(ApiService.class);
    }

    public static Context getContext() {
        return context;
    }

    public static Gson getGson() {
        return gson;
    }

    public static void showToast(String msg) {
        toast.setText(msg);
        toast.show();
    }

    public static ApiService getRemote() {
        return remote;
    }
}
