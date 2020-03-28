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

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.Values;
import com.realmax.opentrafficversion.bean.ButtonBean;
import com.realmax.opentrafficversion.bean.CameraBodyBean;
import com.realmax.opentrafficversion.bean.ORCBean;
import com.realmax.opentrafficversion.bean.ViolateBean;
import com.realmax.opentrafficversion.dao.OrmHelper;
import com.realmax.opentrafficversion.dao.ViolateDaoUtil;
import com.realmax.opentrafficversion.impl.CameraHandler;
import com.realmax.opentrafficversion.impl.CustomerCallback;
import com.realmax.opentrafficversion.impl.RemoteHandler;
import com.realmax.opentrafficversion.utils.EncodeAndDecode;
import com.realmax.opentrafficversion.utils.Network;
import com.realmax.opentrafficversion.utils.TCPLinks;
import com.realmax.opentrafficversion.utils.ValueUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.netty.channel.ChannelHandlerContext;

@SuppressLint("SetTextI18n")
public class ManagementActivity extends BaseActivity implements View.OnClickListener {
    public static final String Car = "十字交叉路口";
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
    private int currentCamera = -1;

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
    private List<ViolateBean> violateBeans;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    String message = (String) msg.obj;
                    tv_measure.setText(message);
                    tv_tips.setText("");
                    break;
                case 1:
                    // 进行车牌号识别
                    orcNumberPlate();
                    break;
            }
        }
    };
    private OrmHelper ormHelper;
    private Dao<ViolateBean, ?> violateDao;
    private ChannelHandlerContext handlerContext;

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
        tv_camera_state.setText("摄像头：" + (ValueUtil.isCameraConnected() ? "已连接" : "未连接"));
        tv_control_state.setText("控制器：" + (ValueUtil.isRemoteConnected() ? "已连接" : "未连接"));

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

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    // 获取数据库对象
                    ormHelper = OrmHelper.getInstance();
                    // 获取Dao
                    violateDao = ormHelper.getDao(ViolateBean.class);
                    // 获取所有的数据
                    violateBeans = violateDao.queryForAll();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        /*cameraTCPLink = new TCPLinks(cameraSocket);
        remoteTCPLink = new TCPLinks(remoteSocket);*/

        handlerContext = CameraHandler.getHandlerContext();
        ValueUtil.setHandlerContext(handlerContext);

        // 控制器数据返回监听以及连接断开监听
        RemoteHandler.setCustomerCallback(new CustomerCallback() {
            /**
             * 连接断开时执行
             */
            @Override
            public void disConnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_control_state.setText("控制器：连接断开");
                    }
                });
            }

            /**
             * 返回数据时执行
             * @param msg 返回的json数据
             */
            @Override
            public void getResultData(String msg) {
                try {
                    JSONObject jsonObject = new JSONObject(msg);
                    checkedPosition = jsonObject.optInt("id") - 1;
                    Log.i(TAG, "getResultData: " + checkedPosition);
                    violate();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        // 摄像头返回数据监听以及摄像头连接状态监听
        CameraHandler.setCustomerCallback(new CustomerCallback() {
            /**
             * 连接断开时执行
             */
            @Override
            public void disConnected() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_camera_state.setText("摄像头：连接断开");
                    }
                });
            }

            /**
             * 收到返回数据时执行
             *
             * @param msg 返回的json数据
             */
            @Override
            public void getResultData(String msg) {
                getImageData(msg);
            }
        });
    }

    /**
     * 获取违章拍摄的摄像头
     */
    private void violate() {
        if (checkedPosition >= 0 && checkedPosition <= 7) {
            for (int i = 0; i < buttonNames.size(); i++) {
                int currentId = buttonNames.get(i).getId();
                if (currentId == checkedPosition + 1) {
                    checkedPosition = i;
                    currentCamera = buttonNames.get(i).getId();
                    break;
                }
            }

            // 刷新按钮位置
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // 刷新按钮位置
                    customerAdapter.notifyDataSetChanged();
                    tv_measure.setVisibility(View.VISIBLE);
                    tv_measure.setText(buttonNames.get(checkedPosition).getName() + "抓拍，百度云测算中……");
                }
            });
        } else {
            checkedPosition = 0;
        }
    }

    /**
     * 获取摄像头图片并设置到控件中
     *
     * @param msg 摄像头传回的数据
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getImageData(String msg) {
        CameraBodyBean imageBodyBean = getImageBodyBean(msg);
        if (imageBodyBean != null) {
            Bitmap bitmap = EncodeAndDecode.base64ToImage(imageBodyBean.getCameraImg());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    iv_snap_shot.setImageBitmap(bitmap);
                }
            });
        }
        /*if (!isBeat) {
            CameraBodyBean imageData = cameraTCPLink.getImageData(cameraTCPLink.getJson());
            if (imageData != null) {
                Bitmap bitmap = EncodeAndDecode.base64ToImage(imageData.getCameraImg());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iv_snap_shot.setImageBitmap(bitmap);
                        if (imageData.getCameraNum() == currentCamera) {
                            Log.i(TAG, "run: 发送拍照指令");
                            currentCamera = -1;
                            isBeat = true;
                            // 发送消息，通知其准备好进行识别
                            Message obtain = Message.obtain();
                            obtain.what = 1;
                            handler.sendMessage(obtain);
                        }
                    }
                });
            }
        }*/
    }

    /**
     * 解析获取到的json字符串，并将图片的数据提取出来
     *
     * @param imageData 服务端返回的json数据
     * @return 图片的base64编码
     */
    public CameraBodyBean getImageBodyBean(String imageData) {
        // 对数据进行判空处理
        if (TextUtils.isEmpty(imageData)) {
            return null;
        }

        try {
            return new Gson().fromJson(imageData, CameraBodyBean.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            // 出现json解析异常，处理特定异常{����{"cmd":"play","deviceType":"\u5c0f\u8f66","deviceId":1,"cameraNum":1}
            String substring = imageData.substring(1);
            Log.i(TAG, "getImageData: 出现异常：" + substring);
            getImageBodyBean(substring);
        }
        return null;
    }

    /**
     * 对车牌号进行识别
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void orcNumberPlate() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                // 获取当前的违章图片
                Drawable drawable = iv_snap_shot.getDrawable();
                // 通过百度云提供的工具类来联网识别当前照片中的车牌号
                Network.getORCString(drawable, Values.LICENSE_PLATE_ORC_URL, ORCBean.class, new Network.ResultData<ORCBean>() {
                    @Override
                    public void result(ORCBean orcBean) {
                        // 继续识别
                        isBeat = false;
                        String camera = buttonNames.get(checkedPosition).getName();
                        Message message = Message.obtain();

                        if (orcBean != null) {
                            // 创建当前监控车辆对象的容器
                            ViolateBean obj = null;
                            String numberPlate = orcBean.getWords_result().getNumber();

                            // 当前拍摄的车辆
                            for (ViolateBean violateBean : violateBeans) {
                                if (violateBean.getNumberPlate().equals(numberPlate)) {
                                    obj = violateBean;
                                    break;
                                }
                            }

                            if (checkedPosition >= 0 && checkedPosition <= 3) {//0、1、2、3
                                if (obj == null) {
                                    ViolateBean e = new ViolateBean(camera, numberPlate, 0, true);
                                    violateBeans.add(e);
                                    ViolateDaoUtil.add(e);
                                } else {
                                    obj.updateViolate(camera);
                                    ViolateDaoUtil.updateToCamera(obj);
                                }

                                runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        tv_measure.setText(camera + "抓拍，百度云测算中成功");
                                        tv_tips.setText("车牌号:" + numberPlate + "，违章：判定压线");
                                    }
                                });
                            } else if (checkedPosition >= 4 && checkedPosition <= 7) {//4、5、6、7
                                if (obj != null) {
                                    // 验证当前车辆是否闯红灯
                                    if (obj.ViolateCheck(camera, numberPlate)) {
                                        ViolateDaoUtil.updateToCount(obj);
                                        ViolateBean finalObj = obj;
                                        runOnUiThread(new Runnable() {
                                            @SuppressLint("SetTextI18n")
                                            @Override
                                            public void run() {
                                                tv_measure.setText(camera + "抓拍，百度云测算中成功");
                                                tv_tips.setText("车牌号:" + numberPlate + "，违章：闯红灯，第" + finalObj.getViolateCount() + "次拍照");
                                            }
                                        });
                                    }
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void run() {
                                            tv_measure.setText(camera + "抓拍，百度云测算中成功");
                                            tv_tips.setText("车牌号:" + numberPlate + "，违章：闯红灯，第1次拍照");
                                        }
                                    });
                                    ViolateBean e = new ViolateBean("", numberPlate, 1, false);
                                    e.setCamera_two(camera);
                                    violateBeans.add(e);
                                    ViolateDaoUtil.add(e);
                                }
                            }
                        } else {
                            message.obj = camera + "抓拍，百度云测算失败！";
                            message.what = 0;
                            handler.sendMessage(message);
                        }
                    }

                    @Override
                    public void error() {

                    }
                });
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
                ValueUtil.sendCameraCmd(Car, getItem(position).getId());
                /*cameraTCPLink.start_camera(Car, 1, getItem(position).getId());*/
            }

            cbCamera.setOnClickListener(null);
            cbCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 更新提示文字
                    tv_measure.setVisibility(View.VISIBLE);

                    tv_tips.setText("");
                    tv_measure.setText(getItem(position).getName() + "监控中");

                    // 选中item的position
                    checkedPosition = position;

                    // 刷新列表更新当前按钮状态
                    customerAdapter.notifyDataSetChanged();
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
        ValueUtil.sendStopCmd();
        /*flag = false;*/
        /*cameraTCPLink.stop_camera();*/
    }
}
