package com.realmax.opentrafficversion.activity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import androidx.cardview.widget.CardView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.j256.ormlite.dao.Dao;
import com.realmax.opentrafficversion.R;
import com.realmax.opentrafficversion.Values;
import com.realmax.opentrafficversion.bean.ButtonBean;
import com.realmax.opentrafficversion.bean.CameraBodyBean;
import com.realmax.opentrafficversion.bean.CurrentCameraBean;
import com.realmax.opentrafficversion.bean.ORCBean;
import com.realmax.opentrafficversion.bean.ViolateBean;
import com.realmax.opentrafficversion.bean.ViolateCarBean;
import com.realmax.opentrafficversion.dao.OpenTrafficQueryDao;
import com.realmax.opentrafficversion.dao.OrmHelper;
import com.realmax.opentrafficversion.ftp.FTPUtil;
import com.realmax.opentrafficversion.impl.CameraHandler;
import com.realmax.opentrafficversion.impl.CustomerCallback;
import com.realmax.opentrafficversion.impl.RemoteHandler;
import com.realmax.opentrafficversion.utils.EncodeAndDecode;
import com.realmax.opentrafficversion.utils.L;
import com.realmax.opentrafficversion.utils.Network;
import com.realmax.opentrafficversion.utils.TCPLinks;
import com.realmax.opentrafficversion.utils.ValueUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
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
     * 当前识别到违章车辆到摄像头
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
                /*case 1:
                    // 进行车牌号识别
                    orcNumberPlate();
                    break;*/
            }
        }
    };
    private OrmHelper ormHelper;
    private Dao<ViolateBean, ?> violateDao;
    private ChannelHandlerContext handlerContext;
    private ImageView iv_violate_image;
    private CardView cd_view;
    private Button btn_violation_view;
    /**
     * 当前拍摄到违章的摄像头编号
     */
    private int violateCamera;
    private ArrayList<CurrentCameraBean> violateBitmap;
    private ArrayList<ViolateCarBean> violateCarBeans;

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
        iv_violate_image = (ImageView) findViewById(R.id.iv_violate_image);
        iv_violate_image.setOnClickListener(this);
        cd_view = (CardView) findViewById(R.id.cd_view);
        cd_view.setOnClickListener(this);
        btn_violation_view = (Button) findViewById(R.id.btn_violation_view);
        btn_violation_view.setOnClickListener(this);

        // 获取源图片的宽高算出宽高比
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.decodeResource(getResources(), R.drawable.weizhang, options);
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        float i = (float) outWidth / (float) outHeight;

        // 通过宽高比设置carView的宽高
        cd_view.setContentPadding(10, 10, 10, 10);
        ViewGroup.LayoutParams cardParams = cd_view.getLayoutParams();
        cardParams.height = 240;
        cardParams.width = (int) (200 * i);
        cd_view.setLayoutParams(cardParams);
    }

    @Override
    protected void initEvent() {
        cd_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_violate_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueUtil.setViolateCarBeans(violateCarBeans);
                jump(ViolateDetailActivity.class);
            }
        });

        btn_violation_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValueUtil.setViolateCarBeans(violateCarBeans);
                jump(ViolateDetailActivity.class);
            }
        });
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

        violateCarBeans = new ArrayList<>();
        // 初始化违章车辆集合
        violateBeans = new ArrayList<>();
        // 违章照片集合
        violateBitmap = new ArrayList<>();

        OpenTrafficQueryDao.queryForAll(violateCarBeans, new OpenTrafficQueryDao.Result() {
            @Override
            public void success(Object object) {
                handlerContext = CameraHandler.getHandlerContext();
                ValueUtil.setHandlerContext(handlerContext);
                monitor();
            }
        });

        handlerContext = CameraHandler.getHandlerContext();
        ValueUtil.setHandlerContext(handlerContext);
    }

    /**
     * 打开监听
     */
    private void monitor() {
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
                    checkedPosition = jsonObject.optInt("id");
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
            @RequiresApi(api = Build.VERSION_CODES.O)
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
        if (checkedPosition >= 1 && checkedPosition <= 8) {
            for (int i = 0; i < buttonNames.size(); i++) {
                int currentId = buttonNames.get(i).getId();
                if (currentId == checkedPosition) {
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
                    tv_measure.setText(buttonNames.get(checkedPosition).getName() + "抓拍，百度云测算中……");
                    // 设置可拍照
                    isBeat = true;
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

                    // 判断当前返回的数据是不是拍摄到违章画面的摄像头
                    if (imageBodyBean.getCameraNum() == currentCamera && isBeat) {
                        iv_violate_image.setImageBitmap(bitmap);
                        Log.i(TAG, "run: 开始执行拍照");
                        isBeat = false;
                        violateBitmap.add(new CurrentCameraBean(bitmap, checkedPosition, new Date()));

                        if (flag) {
                            flag = false;
                            orcNumberPlate();
                        }
                    }
                }
            });
        }
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
                Message message = Message.obtain();
                if (violateBitmap.size() <= 0) {
                    Log.d(TAG, "run: 识别完毕");
                    flag = true;
                    return;
                }

                CurrentCameraBean currentCameraBean = violateBitmap.get(0);
                Bitmap bitmap = currentCameraBean.getBitmap();
                Network.getORCString(bitmap, Values.LICENSE_PLATE_ORC_URL, ORCBean.class, new Network.ResultData<ORCBean>() {
                    @Override
                    public void result(ORCBean orcBean) {
                        String camera = buttonNames.get(currentCameraBean.getCameraPostion()).getName();
                        if (orcBean != null) {
                            Date createTime = currentCameraBean.getCreateTime();
                            // 进行上传图片的操作
                            uploadImg(createTime.getTime() + "", bitmap);

                            Log.i(TAG, "result: 识别成功");
                            // 创建当前监控车辆对象的容器
                            ViolateCarBean obj = null;
                            String numberPlate = orcBean.getWords_result().getNumber();

                            // 找出当前车辆之前是否有过违章记录
                            for (ViolateCarBean violateCarBean : violateCarBeans) {
                                if (violateCarBean.getNumberPlate().equals(numberPlate)) {
                                    obj = violateCarBean;
                                    obj.setCreateTime(createTime);
                                    obj.setViolateBitmap(bitmap);
                                    L.e("找到对应车辆");
                                    break;
                                }
                            }

                            // 判断是否为A1，B1，C1，D1进行拍摄
                            if (currentCameraBean.getCameraPostion() >= 0 && currentCameraBean.getCameraPostion() <= 3) {//0、1、2、3
                                L.e("判定为为A1B1C1D1压线");
                                // 判断是否在以前的违章记录中找到了次车辆
                                if (obj == null) {
                                    L.e("未找到之前的记录");
                                    ViolateCarBean e = new ViolateCarBean(camera, "", numberPlate, 0, true, "判定压线", bitmap, createTime);
                                    // 未找到将其添加进违章记录集合
                                    violateCarBeans.add(e);
                                } else {
                                    L.e("找到了之前的记录");
                                    // 找到此车辆之前的违章记录，更新当前车辆违章记录的次数
                                    // 设置拍照时间
                                    obj.updateViolate(camera);
                                }

                                runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        tv_measure.setText(camera + "抓拍，百度云测算中成功");
                                        tv_tips.setText("车牌号:" + numberPlate + "，违章：判定压线");
                                    }
                                });
                            } else if (currentCameraBean.getCameraPostion() >= 4 && currentCameraBean.getCameraPostion() <= 7) {//判断是否为A2，B2，C2，D2抓拍的
                                L.e("判定为闯红灯");
                                // 当找到此车辆之前的压线记录｜闯红灯记录
                                if (obj != null) {
                                    L.e("找到之前的记录:" + camera);
                                    // 验证当前车辆是否闯红灯（比如先被A1抓拍然后被A2抓拍，中间有压B1的线则重新开始判定）
                                    if (obj.ViolateCheck(camera, numberPlate)) {
                                        ViolateCarBean finalObj = obj;
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
                                    L.e("没有找到之前的记录");
                                    runOnUiThread(new Runnable() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void run() {
                                            tv_measure.setText(camera + "抓拍，百度云测算中成功");
                                            tv_tips.setText("车牌号:" + numberPlate + "，违章：闯红灯，第1次拍照");
                                        }
                                    });
                                    // 新的违章
                                    ViolateCarBean e = new ViolateCarBean("", camera, numberPlate, 1, false, "判定闯红灯", bitmap, createTime);
                                    // 添加进集合
                                    violateCarBeans.add(e);
                                }
                            }

                            // 从集合中删除图片
                            violateBitmap.remove(0);
                        } else {
                            // 识别失败，删除照片
                            violateBitmap.remove(0);
                            message.obj = camera + "抓拍，百度云测算失败！";
                            message.what = 0;
                            handler.sendMessage(message);
                        }


                        // 重新调用此方法继续识别图片集合中的违章车牌
                        orcNumberPlate();
                    }

                    @Override
                    public void error() {
                        message.obj = "抓拍，百度云测算失败！";
                        message.what = 0;
                        handler.sendMessage(message);

                        violateBitmap.remove(0);
                        // 重新调用此方法继续识别图片集合中的违章车牌
                        orcNumberPlate();
                    }
                });
            }
        }.start();
    }

    private void uploadImg(String createTime, Bitmap bitmap) {
        FTPUtil.compressImage(bitmap, createTime, new FTPUtil.Result() {
            @Override
            public void success(File file) throws IOException {
                FTPUtil ftpUtil = new FTPUtil();
                boolean b = ftpUtil.openConnect();
                boolean uploading = ftpUtil.uploading(file, createTime);
                if (uploading) {
                    if (file != null) {
                        file.delete();
                    }
                }
                L.e("上传状态：" + uploading);
            }
        });
    }

    /**
     * 对车牌号进行识别
     */
    /*@RequiresApi(api = Build.VERSION_CODES.O)
    private void orcNumberPlate() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Message message = Message.obtain();
                if (violateBitmap.size() <= 0) {
                    Log.d(TAG, "run: 识别完毕");
                    flag = true;
                    return;
                }

                CurrentCameraBean currentCameraBean = violateBitmap.get(0);
                Network.getORCString(currentCameraBean.getBitmap(), Values.LICENSE_PLATE_ORC_URL, ORCBean.class, new Network.ResultData<ORCBean>() {
                    @Override
                    public void result(ORCBean orcBean) {
                        String camera = buttonNames.get(currentCameraBean.getCameraPostion()).getName();
                        if (orcBean != null) {
                            Log.i(TAG, "result: 识别成功");
                            // 创建当前监控车辆对象的容器
                            ViolateBean obj = null;
                            String numberPlate = orcBean.getWords_result().getNumber();

                            // 找出当前车辆之前是否有过违章记录
                            for (ViolateBean violateBean : violateBeans) {
                                if (violateBean.getNumberPlate().equals(numberPlate)) {
                                    obj = violateBean;
                                    Log.d(TAG, "result: 找到对应车辆");
                                    break;
                                }
                            }

                            // 判断是否为A1，B1，C1，D1进行拍摄
                            if (currentCameraBean.getCameraPostion() >= 0 && currentCameraBean.getCameraPostion() <= 3) {//0、1、2、3
                                Log.d(TAG, "result: 判定为为A1B1C1D1压线");
                                // 判断是否在以前的违章记录中找到了次车辆
                                if (obj == null) {
                                    ViolateBean e = new ViolateBean(camera, numberPlate, 0, true);
                                    // 未找到将其添加进违章记录集合
                                    violateBeans.add(e);
                                    // 添加至数据库中
                                    ViolateDaoUtil.add(e);
                                    Log.d(TAG, "result: 找到了之前的记录");
                                } else {
                                    // 找到此车辆之前的违章记录，更新当前车辆违章记录的次数
                                    obj.updateViolate(camera);
                                    // 更新数据库中车辆违章的次数
                                    ViolateDaoUtil.updateToCamera(obj);
                                    Log.d(TAG, "result: 未找到之前的记录");
                                }

                                runOnUiThread(new Runnable() {
                                    @SuppressLint("SetTextI18n")
                                    @Override
                                    public void run() {
                                        tv_measure.setText(camera + "抓拍，百度云测算中成功");
                                        tv_tips.setText("车牌号:" + numberPlate + "，违章：判定压线");
                                    }
                                });
                            } else if (currentCameraBean.getCameraPostion() >= 4 && currentCameraBean.getCameraPostion() <= 7) {//判断是否为A2，B2，C2，D2抓拍的
                                Log.d(TAG, "result: 判定为闯红灯");
                                // 当找到此车辆之前的压线记录｜闯红灯记录
                                if (obj != null) {
                                    Log.d(TAG, "result: 找到之前的记录" + camera);
                                    // 验证当前车辆是否闯红灯（比如先被A1抓拍然后被A2抓拍，中间有压B1的线则重新开始判定）
                                    if (obj.ViolateCheck(camera, numberPlate)) {
                                        // 判定为闯红灯，更新至数据库中闯红灯次数+1
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
                                    Log.d(TAG, "result: 没有找到之前的记录");
                                    runOnUiThread(new Runnable() {
                                        @SuppressLint("SetTextI18n")
                                        @Override
                                        public void run() {
                                            tv_measure.setText(camera + "抓拍，百度云测算中成功");
                                            tv_tips.setText("车牌号:" + numberPlate + "，违章：闯红灯，第1次拍照");
                                        }
                                    });
                                    // 新的违章
                                    ViolateBean e = new ViolateBean("", numberPlate, 1, false);
                                    // 设置第二次抓拍到的摄像头
                                    e.setCamera_two(camera);
                                    // 添加进集合
                                    violateBeans.add(e);
                                    // 添加到数据库中
                                    ViolateDaoUtil.add(e);
                                }
                            }

                            // 从集合中删除图片
                            violateBitmap.remove(0);
                        } else {
                            // 识别失败，删除照片
                            violateBitmap.remove(0);
                            message.obj = camera + "抓拍，百度云测算失败！";
                            message.what = 0;
                            handler.sendMessage(message);
                        }


                        // 重新调用此方法继续识别图片集合中的违章车牌
                        orcNumberPlate();
                    }

                    @Override
                    public void error() {
                        message.obj = "抓拍，百度云测算失败！";
                        message.what = 0;
                        handler.sendMessage(message);

                        violateBitmap.remove(0);
                        // 重新调用此方法继续识别图片集合中的违章车牌
                        orcNumberPlate();
                    }
                });
            }
        }.start();
    }*/

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
            case R.id.btn_violation_view:
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
