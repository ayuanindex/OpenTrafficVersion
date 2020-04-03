package com.realmax.opentrafficversion.activity;

import android.Manifest;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.dao.OrmHelper;
import com.realmax.opentrafficversion.utils.ValueUtil;

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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void initData() {
        // 请求必要权限
        requestPermissions(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS
        }, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_jump_management:
                // 交通管理界面
                jump(ManagementActivity.class);
                break;
            case R.id.btn_jump_camera_setting:
                // 虚拟摄像头连接界面
                jump(CameraSettingActivity.class);
                break;
            case R.id.btn_jump_control_setting:
                // 控制器连接界面
                jump(ControlSettingActivity.class);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    ValueUtil.getCameraEventExecutors().shutdownGracefully().sync();
                    ValueUtil.getRemoteEventExecutors().shutdownGracefully().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        // 关闭数据库
        OrmHelper.getInstance().close();
    }
}
