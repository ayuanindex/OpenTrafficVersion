package com.realmax.opentrafficversion.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.realmax.opentrafficversion.R;

public class ControlSettingActivity extends BaseActivity implements View.OnClickListener {
    private ImageView iv_logo;
    private EditText et_ip;
    private EditText et_port;
    private Button btn_connected;
    private Button btn_back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        iv_logo = (ImageView) findViewById(R.id.iv_logo);
        iv_logo.setOnClickListener(this);
        et_ip = (EditText) findViewById(R.id.et_ip);
        et_ip.setOnClickListener(this);
        et_port = (EditText) findViewById(R.id.et_port);
        et_port.setOnClickListener(this);
        btn_connected = (Button) findViewById(R.id.btn_connected);
        btn_connected.setOnClickListener(this);
        btn_back = (Button) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(this);


        iv_logo.setImageResource(R.drawable.control);
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
            case R.id.btn_connected:

                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    private void submit() {
        // validate
        String ip = et_ip.getText().toString().trim();
        if (TextUtils.isEmpty(ip)) {
            Toast.makeText(this, "请输入IP地址", Toast.LENGTH_SHORT).show();
            return;
        }

        String port = et_port.getText().toString().trim();
        if (TextUtils.isEmpty(port)) {
            Toast.makeText(this, "请输入端口号", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO validate success, do something


    }
}
