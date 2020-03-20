package com.realmax.opentrafficversion.activity;

import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.realmax.opentrafficversion.R;

public class ManagementActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_camera_state;
    private TextView tv_control_state;
    private ImageView iv_snap_shot;
    private TextView tv_measure;
    private TextView tv_tips;
    private GridView gv_btns;
    private Button btn_back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_manager;
    }

    @Override
    protected void initView() {
        tv_camera_state = (TextView) findViewById(R.id.tv_camera_state);
        tv_camera_state.setOnClickListener(this);
        tv_control_state = (TextView) findViewById(R.id.tv_control_state);
        tv_control_state.setOnClickListener(this);
        iv_snap_shot = (ImageView) findViewById(R.id.iv_snap_shot);
        iv_snap_shot.setOnClickListener(this);
        tv_measure = (TextView) findViewById(R.id.tv_measure);
        tv_measure.setOnClickListener(this);
        tv_tips = (TextView) findViewById(R.id.tv_tips);
        tv_tips.setOnClickListener(this);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void messageResult(String type, String msg) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
