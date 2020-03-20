package com.realmax.opentrafficversion.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.utils.EncodeAndDecode;
import com.realmax.opentrafficversion.utils.TCPLinks;

public class ManagementActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_camera_state;
    private TextView tv_control_state;
    private ImageView iv_snap_shot;
    private TextView tv_measure;
    private TextView tv_tips;
    private GridView gv_btns;
    private Button btn_back;
    private TCPLinks cameraTCPLink;
    private TCPLinks remoteTCPLink;

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

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        // 判断连接状态
        tv_camera_state.setText("摄像头：" + (cameraSocket != null ? "已连接" : "未连接"));
        tv_control_state.setText("控制器：" + (remoteSocket != null ? "已连接" : "未连接"));

        cameraTCPLink = new TCPLinks(cameraSocket);
        remoteTCPLink = new TCPLinks(remoteSocket);

        getImage();
        String s = cameraTCPLink.fetch_camera();
    }

    private void getImage() {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                super.run();
                if (cameraSocket != null) {
                    while (true) {
                        String s = cameraTCPLink.fetch_camera();
                        Bitmap bitmap = EncodeAndDecode.decodeBase64ToImage(s);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                iv_snap_shot.setImageBitmap(bitmap);
                            }
                        });
                    }
                }
            }
        }.start();
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
