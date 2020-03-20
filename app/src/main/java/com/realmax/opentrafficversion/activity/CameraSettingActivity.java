package com.realmax.opentrafficversion.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.realmax.opentrafficversion.App;
import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.utils.TCPLinks;
import com.realmax.opentrafficversion.utils.SpUtil;

import java.net.Socket;

@SuppressLint("HandlerLeak")
public class CameraSettingActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_logo;
    private EditText et_ip;
    private EditText et_port;
    private Button btn_connected;
    private Button btn_back;
    private String camera_ip;
    private int camera_port;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case 0:
                    cameraSocket = (Socket) msg.obj;
                    App.showToast("连接成功");
                    break;
                case 1:
                    App.showToast("连接失败");
                    break;
            }
        }
    };
    private TCPLinks cameraTcp;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_port = (EditText) findViewById(R.id.et_port);
        btn_connected = (Button) findViewById(R.id.btn_connected);
        btn_back = (Button) findViewById(R.id.btn_back);

        iv_logo.setImageResource(R.drawable.pic_camera);
    }

    @Override
    protected void initEvent() {
        iv_logo.setOnClickListener(this);
        et_ip.setOnClickListener(this);
        et_port.setOnClickListener(this);
        btn_connected.setOnClickListener(this);
        btn_back.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        // 拿到最后一次连接成功的IP和端口号
        camera_ip = SpUtil.getString("camera_ip", "192.168.1.1");
        camera_port = SpUtil.getInt("camera_port", 8527);

        // 回显最后一次连接的ip和port
        et_ip.setText(camera_ip);
        et_port.setText(camera_port + "");
    }

    @Override
    protected void messageResult(String type, String msg) {

    }

    /**
     * 提交连接
     */
    private void submit() {
        String ip = et_ip.getText().toString().trim();
        if (TextUtils.isEmpty(ip)) {
            Toast.makeText(this, "请输入IP地址", Toast.LENGTH_SHORT).show();
            return;
        }

        String portStr = et_port.getText().toString().trim();
        if (TextUtils.isEmpty(portStr)) {
            Toast.makeText(this, "请输入端口号", Toast.LENGTH_SHORT).show();
            return;
        }
        int portInt = Integer.parseInt(portStr);

        App.showToast("正在连接");

        // 判断进入当前界面后最新输入的ip或端口号是否和之前的一样，如果不一样则断开之前的连接
        // 对摄像头连接的socket进行判空
        if (!(ip.equals(camera_ip) && portInt == camera_port) || cameraSocket == null) {
            cameraSocket = null;
            cameraTcp.stop();
            cameraTcp.start(ip, portInt, new TCPLinks.ResultData() {
                @Override
                public void isConnected(Socket socket, Message message) {
                    if (socket.isConnected()) {
                        camera_ip = ip;
                        camera_port = portInt;

                        SpUtil.putString("camera_ip", camera_ip);
                        SpUtil.putInt("camera_port", camera_port);

                        cameraSocket = socket;

                        message.what = 0;
                        message.obj = socket;
                        handler.sendMessage(message);
                    }
                }

                @Override
                public void error(Message message) {
                    message.what = 1;
                    handler.sendMessage(message);
                }
            });
        } else {
            App.showToast("已连接");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_connected:
                if (cameraTcp == null) {
                    cameraTcp = new TCPLinks();
                    cameraTcp.setSocket(cameraSocket);
                }
                submit();
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }
}
