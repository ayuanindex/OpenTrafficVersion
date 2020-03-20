package com.realmax.opentrafficversion.activity;

import android.view.View;
import android.widget.Button;

import com.realmax.opentrafficversion.R;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_jump_management;
    private Button btn_jump_camera_setting;
    private Button btn_jump_control_setting;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        btn_jump_management = (Button) findViewById(R.id.btn_jump_management);
        btn_jump_management.setOnClickListener(this);
        btn_jump_camera_setting = (Button) findViewById(R.id.btn_jump_camera_setting);
        btn_jump_camera_setting.setOnClickListener(this);
        btn_jump_control_setting = (Button) findViewById(R.id.btn_jump_control_setting);
        btn_jump_control_setting.setOnClickListener(this);
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
            case R.id.btn_jump_management:
                jump(ManagementActivity.class);
                break;
            case R.id.btn_jump_camera_setting:
                jump(CameraSettingActivity.class);
                break;
            case R.id.btn_jump_control_setting:
                jump(ControlSettingActivity.class);
                break;
        }
    }
}
