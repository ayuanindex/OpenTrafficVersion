package com.realmax.opentrafficversion.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.bean.ViolateCarBean;

public class ViolateFragment extends Fragment {
    private static final String TAG = "ViolateFragment";
    private final ViolateCarBean violateCarBean;
    private Context context;

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
        return View.inflate(getActivity(), R.layout.fragment_violate_detail, null);
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {

    }
}
