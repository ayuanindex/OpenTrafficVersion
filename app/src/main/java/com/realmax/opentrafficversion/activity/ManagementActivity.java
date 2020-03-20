package com.realmax.opentrafficversion.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.realmax.opentrafficversion.App;
import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.utils.EncodeAndDecode;
import com.realmax.opentrafficversion.utils.TCPLinks;

import java.util.ArrayList;

public class ManagementActivity extends BaseActivity implements View.OnClickListener {
    private TextView tv_camera_state;
    private TextView tv_control_state;
    private ImageView iv_snap_shot;
    private TextView tv_measure;
    private TextView tv_tips;
    private Button btn_back;
    private TCPLinks cameraTCPLink;
    private TCPLinks remoteTCPLink;
    private boolean flag = false;
    private ArrayList<String> buttonNames;
    private CustomerAdapter customerAdapter;
    private GridView gv_btns;

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
        gv_btns = (GridView) findViewById(R.id.gv_btns);
    }

    @Override
    protected void initEvent() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    @Override
    protected void initData() {
        // 判断连接状态
        tv_camera_state.setText("摄像头：" + (cameraSocket != null ? "已连接" : "未连接"));
        tv_control_state.setText("控制器：" + (remoteSocket != null ? "已连接" : "未连接"));

        // 初始化按钮名称集合
        buttonNames = new ArrayList<>();
        buttonNames.add("A1");
        buttonNames.add("B1");
        buttonNames.add("C1");
        buttonNames.add("D1");
        buttonNames.add("A2");
        buttonNames.add("B2");
        buttonNames.add("C2");
        buttonNames.add("D2");

        customerAdapter = new CustomerAdapter();
        gv_btns.setAdapter(customerAdapter);

        cameraTCPLink = new TCPLinks(cameraSocket);
        remoteTCPLink = new TCPLinks(remoteSocket);

        cameraTCPLink.start_camera("小车", 1, 1);
        getImage();

        gv_btns.setSelection(2);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getImage() {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                super.run();
                if (cameraSocket != null) {
                    while (!flag) {
                        String s = cameraTCPLink.fetch_camera();
                        if (!TextUtils.isEmpty(s)) {
                            Log.i(TAG, "run: 哈哈和：" + s);
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
            }
        }.start();
    }

    class CustomerAdapter extends BaseAdapter {
        private Button btnCamera;

        @Override
        public int getCount() {
            return buttonNames.size();
        }

        @Override
        public String getItem(int position) {
            return buttonNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null) {
                view = View.inflate(ManagementActivity.this, R.layout.item_btn, null);
            } else {
                view = convertView;
            }
            initView(view);
            btnCamera.setText(getItem(position));

            btnCamera.setOnClickListener(null);
            btnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    App.showToast("点击了：" + getItem(position) + "按钮");
                }
            });
            return view;
        }

        private void initView(View view) {
            btnCamera = (Button) view.findViewById(R.id.btn_camera);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        cameraTCPLink.stop_camera();
    }
}
