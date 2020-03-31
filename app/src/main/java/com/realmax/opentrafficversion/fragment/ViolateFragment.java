package com.realmax.opentrafficversion.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.realmax.opentrafficversion.ApiService;
import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.Values;
import com.realmax.opentrafficversion.bean.ViolateCarBean;
import com.realmax.opentrafficversion.utils.L;
import com.realmax.opentrafficversion.utils.Network;

import java.io.InputStream;
import java.text.SimpleDateFormat;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ViolateFragment extends Fragment {
    private static final String TAG = "ViolateFragment";
    private final ViolateCarBean violateCarBean;
    private Context context;
    private ImageView ivSnapShot;
    private TextView tvMeasure;
    private TextView tvTips;
    private Disposable subscribe;
    @SuppressLint("SimpleDateFormat")
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");


    public ViolateFragment(ViolateCarBean violateCarBean) {
        this.violateCarBean = violateCarBean;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: 1");
        View view = View.inflate(getActivity(), R.layout.fragment_violate_detail, null);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private void initView(View view) {
        ivSnapShot = (ImageView) view.findViewById(R.id.iv_snap_shot);
        tvMeasure = (TextView) view.findViewById(R.id.tv_measure);
        tvTips = (TextView) view.findViewById(R.id.tv_tips);
    }

    @SuppressLint("SetTextI18n")
    private void initData() {
        ApiService remote = Network.remote(ApiService.class, Values.BASE_URL_IMG);
        subscribe = remote.getImg(violateCarBean.getImgPath())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ResponseBody>() {
                    @Override
                    public void accept(ResponseBody responseBody) throws Exception {
                        InputStream inputStream = responseBody.byteStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        ivSnapShot.setImageBitmap(bitmap);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        L.e(throwable.getMessage());
                    }
                });

        tvMeasure.setText("车牌号：" + violateCarBean.getNumberPlate());
        tvTips.setText("违章次数：" + violateCarBean.getViolateCount() + ";最后一次违章时间：" + simpleDateFormat.format(violateCarBean.getCreateTime()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (subscribe != null) {
            if (subscribe.isDisposed()) {
                subscribe.dispose();
            }
        }
    }
}
