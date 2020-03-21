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
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.realmax.opentrafficversion.App;
import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.Values;
import com.realmax.opentrafficversion.bean.ORCBean;
import com.realmax.opentrafficversion.utils.EncodeAndDecode;
import com.realmax.opentrafficversion.utils.Network;
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
        Network.getORCString(iv_snap_shot.getDrawable(), Values.LICENSE_PLATE_ORC_URL, ORCBean.class, new Network.ResultData<ORCBean>() {
            @Override
            public void result(ORCBean orcBean) {
                Log.i(TAG, "result: " + orcBean.toString());
            }
        });

        // 获取摄像头拍摄数据
        getImage();
    }

    /**
     * 获取摄像头图片
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getImage() {
        new Thread() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                super.run();
                if (cameraSocket != null) {
                    while (!flag) {
                        String imageData = cameraTCPLink.getImageData(cameraTCPLink.getJsonString());
                        if (!TextUtils.isEmpty(imageData)) {
                            Log.i(TAG, "run: 哈哈和：" + imageData);
                            Bitmap bitmap = EncodeAndDecode.decodeBase64ToImage(imageData);
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
        private CheckBox cbCamera;
        private int checkedPosition = 0;

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
            // 设置按钮显示的文字
            cbCamera.setText(getItem(position));
            // 设置按钮的默认选中状态
            cbCamera.setChecked(false);

            // 设置当前选中的按钮
            if (checkedPosition == position) {
                // 将选中的按钮的状态更改为true
                cbCamera.setChecked(true);
                // 开启监控
                cameraTCPLink.start_camera("小车", position + 1, position);
            }

            cbCamera.setOnClickListener(null);
            cbCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 选中item的position
                    checkedPosition = position;
                    // 刷新列表更新当前按钮状态
                    notifyDataSetChanged();
                    App.showToast("点击了：" + getItem(position) + "按钮");
                }
            });
            return view;
        }

        private void initView(View view) {
            cbCamera = (CheckBox) view.findViewById(R.id.cb_camera);
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
