package com.realmax.opentrafficversion.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.j256.ormlite.dao.Dao;
import com.realmax.opentrafficversion.App;
import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.Values;
import com.realmax.opentrafficversion.bean.ButtonBean;
import com.realmax.opentrafficversion.bean.ORCBean;
import com.realmax.opentrafficversion.bean.ViolateBean;
import com.realmax.opentrafficversion.dao.OrmHelper;
import com.realmax.opentrafficversion.utils.EncodeAndDecode;
import com.realmax.opentrafficversion.utils.Network;
import com.realmax.opentrafficversion.utils.TCPLinks;

import java.util.ArrayList;

public class ManagementActivity extends BaseActivity implements View.OnClickListener {
    public static final String Car = "小车";
    private TextView tv_camera_state;
    private TextView tv_control_state;
    private ImageView iv_snap_shot;
    private TextView tv_measure;
    private TextView tv_tips;
    private Button btn_back;

    /**
     * 是否开始拍照
     */
    private boolean isBeat = false;

    /**
     * 抓拍次数
     */
    private int count = 0;

    /**
     * 当前识别到的车牌号
     */
    private String currentNumberPlate;

    /**
     * 摄像头切换按钮
     */
    private GridView gv_btns;

    /**
     * 小车摄像头的链接
     */
    private TCPLinks cameraTCPLink;
    /**
     * 红绿灯摄像头的链接
     */
    private TCPLinks remoteTCPLink;
    /**
     * 是否获取数据
     */
    private boolean flag = true;
    /**
     * 按钮名称的集合
     */
    private ArrayList<ButtonBean> buttonNames;
    /**
     * 继承了BaseAdapter的数据适配器
     */
    private CustomerAdapter customerAdapter;
    private int checkedPosition = 0;
    private ArrayList<ViolateBean> violateBeans;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String message = (String) msg.obj;
                    App.showToast(message);
                    break;
            }
        }
    };
    private OrmHelper ormHelper;
    private Dao<ViolateBean, ?> violateDao;

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
        buttonNames.add(new ButtonBean("A1", 1));
        buttonNames.add(new ButtonBean("B1", 3));
        buttonNames.add(new ButtonBean("C1", 5));
        buttonNames.add(new ButtonBean("D1", 7));
        buttonNames.add(new ButtonBean("A2", 2));
        buttonNames.add(new ButtonBean("B2", 4));
        buttonNames.add(new ButtonBean("C2", 6));
        buttonNames.add(new ButtonBean("D2", 8));

        customerAdapter = new CustomerAdapter();
        gv_btns.setAdapter(customerAdapter);

        // 初始化违章车辆集合
        violateBeans = new ArrayList<>();

        cameraTCPLink = new TCPLinks(cameraSocket);
        remoteTCPLink = new TCPLinks(remoteSocket);

        // 获取摄像头拍摄数据
        getImageData();
        // 获取违章数据
        violate();
    }

    /**
     * 获取违章拍摄的摄像头
     */
    private void violate() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    if (remoteSocket != null) {
                        while (flag) {
                            checkedPosition = remoteTCPLink.getCameraNumber(remoteTCPLink.getJson());
                            if (checkedPosition >= 0) {
                                for (int i = 0; i < buttonNames.size(); i++) {
                                    int id = buttonNames.get(i).getId();
                                    if (id == checkedPosition + 1) {
                                        checkedPosition = i;
                                        break;
                                    }
                                }
                                /*// 切换摄像头
                                cameraTCPLink.start_camera(Car, 1, checkedPosition + 1);*/
                                // 刷新按钮位置
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 刷新按钮位置
                                        customerAdapter.notifyDataSetChanged();
                                        App.showToast("检测到车辆压线，正在监控当前车辆");
                                    }
                                });
                                /*sleep(500);
                                // 开始拍照，并用百度云分析
                                isBeat = true;*/
                            } else {
                                checkedPosition = 0;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 获取摄像头图片
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getImageData() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (cameraSocket != null) {
                    while (flag) {
                        String imageData = cameraTCPLink.getImageData(cameraTCPLink.getJson());
                        if (!TextUtils.isEmpty(imageData)) {
                            Bitmap bitmap = EncodeAndDecode.decodeBase64ToImage(imageData);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_snap_shot.setImageBitmap(bitmap);
                                }
                            });

                            if (isBeat) {
                                Log.i(TAG, "run: 哈哈");
                                Drawable drawable = iv_snap_shot.getDrawable();

                                isBeat = false;
                                if (drawable == null) {
                                    isBeat = true;
                                }

                                Network.getORCString(drawable, Values.LICENSE_PLATE_ORC_URL, ORCBean.class, new Network.ResultData<ORCBean>() {
                                    @Override
                                    public void result(ORCBean orcBean) {
                                        Message message = Message.obtain();
                                        if (orcBean != null) {
                                            message.obj = "车牌识别成功";
                                            message.what = 0;
                                            handler.sendMessage(message);
                                            // 创建当前监控车辆对象的容器
                                            ViolateBean obj = null;
                                            String numberPlate = orcBean.getWords_result().getNumber();
                                            String camera = buttonNames.get(checkedPosition).getName();
                                            // 当前拍摄的车辆
                                            for (ViolateBean violateBean : violateBeans) {
                                                if (violateBean.getNumberPlate().equals(numberPlate)) {
                                                    obj = violateBean;
                                                    break;
                                                }
                                            }

                                            if (checkedPosition >= 0 && checkedPosition <= 3) {//0、1、2、3
                                                if (obj == null) {
                                                    violateBeans.add(new ViolateBean(camera, numberPlate, 0, true));
                                                } else {
                                                    obj.updateViolate(camera);
                                                }
                                            } else if (checkedPosition >= 4 && checkedPosition <= 7) {//4、5、6、7
                                                if (obj != null) {
                                                    // 验证当前车辆是否闯红灯
                                                    if (obj.ViolateCheck(camera, numberPlate)) {
                                                        ViolateBean finalObj = obj;
                                                        runOnUiThread(new Runnable() {
                                                            @SuppressLint("SetTextI18n")
                                                            @Override
                                                            public void run() {
                                                                tv_measure.setText(finalObj.getCamera_two() + "抓拍，百度云测算中；");
                                                                tv_tips.setText("车牌号:" + finalObj.getNumberPlate() + "，违章：闯红灯，第" + finalObj.getViolateCount() + "次拍照");
                                                            }
                                                        });
                                                    }
                                                }
                                            }

                                            Log.i(TAG, "run: " + violateBeans.toString());
                                        } else {
                                            message.obj = "车牌识别失败";
                                            message.what = 0;
                                            handler.sendMessage(message);
                                        }
                                    }

                                    @Override
                                    public void error() {
                                        isBeat = true;
                                    }
                                });
                            }

                            isBeat = false;
                        }
                    }
                }
            }
        }.start();
    }

    class CustomerAdapter extends BaseAdapter {
        private CheckBox cbCamera;

        @Override
        public int getCount() {
            return buttonNames.size();
        }

        @Override
        public ButtonBean getItem(int position) {
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
            cbCamera.setText(getItem(position).getName());
            // 设置按钮的默认选中状态
            cbCamera.setChecked(false);

            // 设置当前选中的按钮
            if (checkedPosition == position) {
                // 将选中的按钮的状态更改为true
                cbCamera.setChecked(true);
                // 打开指定位置的摄像头
                cameraTCPLink.start_camera(Car, 1, getItem(position).getId());
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(400);
                            isBeat = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }

            cbCamera.setOnClickListener(null);
            cbCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 选中item的position
                    checkedPosition = position;
                    // 刷新列表更新当前按钮状态
                    customerAdapter.notifyDataSetChanged();
                    /*App.showToast("点击了：" + getItem(position) + "按钮");*/
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
        flag = false;
        cameraTCPLink.stop_camera();
    }
}
