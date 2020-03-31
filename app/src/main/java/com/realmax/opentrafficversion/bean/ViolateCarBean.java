package com.realmax.opentrafficversion.bean;

import android.text.TextUtils;

import com.realmax.opentrafficversion.dao.OpenTrafficQueryDao;
import com.realmax.opentrafficversion.utils.L;

import java.util.Date;
import java.util.Objects;

public class ViolateCarBean {
    private String rootPath = "http://driving.zuto360.com/upload/";
    private String camera_one;
    private String camera_two;
    private String numberPlate;
    private int violateCount;
    private String lastImagePath;
    private Date createTime;
    private boolean isViolate;
    private String des;

    public ViolateCarBean() {
    }

    public ViolateCarBean(String camera_one, String camera_two, String numberPlate, int violateCount, boolean isViolate) {
        this.camera_one = camera_one;
        this.camera_two = camera_two;
        this.numberPlate = numberPlate;
        this.violateCount = violateCount;
        this.isViolate = isViolate;
    }

    public ViolateCarBean(String camera_one, String camera_two, String numberPlate, int violateCount, boolean isViolate, String des, Date createTime) {
        this.camera_one = camera_one;
        this.camera_two = camera_two;
        this.numberPlate = numberPlate;
        this.violateCount = violateCount;
        this.isViolate = isViolate;
        this.des = des;
        this.createTime = createTime;

        // 添加至数据库中
        OpenTrafficQueryDao.addToVio(this, new OpenTrafficQueryDao.Result() {
            @Override
            public void success(Object object) {
                L.e((int) object + "");
            }
        });
    }

    public ViolateCarBean(String camera_one, String camera_two, String numberPlate, int violateCount, String lastImagePath, Date createTime) {
        this.camera_one = camera_one;
        this.camera_two = camera_two;
        this.numberPlate = numberPlate;
        this.violateCount = violateCount;
        this.lastImagePath = lastImagePath;
        this.createTime = createTime;
    }

    public String getCamera_one() {
        return camera_one;
    }

    public void setCamera_one(String camera_one) {
        this.camera_one = camera_one;
    }

    public String getCamera_two() {
        return camera_two;
    }

    public void setCamera_two(String camera_two) {
        this.camera_two = camera_two;
    }

    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }

    public int getViolateCount() {
        return violateCount;
    }

    public void setViolateCount(int violateCount) {
        this.violateCount = violateCount;
    }

    public String getLastImagePath() {
        return rootPath + createTime.getTime();
    }

    public void setLastImagePath(String lastImagePath) {
        this.lastImagePath = lastImagePath;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public boolean isViolate() {
        return isViolate;
    }

    public void setViolate(boolean violate) {
        isViolate = violate;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    /**
     * 违章判定，对指定摄像头进行违章判定，例如：A1抓拍第一次压线，第二次为A2拍到同样的车两压线判定为为违章
     *
     * @param camera      拍摄的摄像头
     * @param numberPlate 识别出的车牌号
     * @return 返回判断标示符：true表示违章，违章次数+1，false表示没有违章
     */
    public boolean ViolateCheck(String camera, String numberPlate) {
        if (!TextUtils.isEmpty(this.camera_one)) {
            if (this.camera_one.charAt(0) == camera.charAt(0)) {
                updateVio(camera);
                return true;
            }
        } else if (camera.equals(this.camera_two) && numberPlate.equals(this.numberPlate)) {
            updateVio(camera);
            return true;
        }
        return false;
    }

    private void updateVio(String camera) {
        this.camera_two = camera;
        this.violateCount++;
        // 停止继续验证此车辆
        this.isViolate = false;
        this.des = "判定闯红灯";
        this.createTime = new Date();
        // 判定为闯红灯，更新至数据库中闯红灯次数+1
        OpenTrafficQueryDao.updateViolateCountByNumberPlate(this, camera, new OpenTrafficQueryDao.Result() {
            @Override
            public void success(Object object) {
                L.e("" + (int) object);
            }
        });
    }

    /**
     * 更新当前车辆经过的摄像头，例如：第一次压线没闯红灯，当第二次压线时，将当前的摄像头编号更新为最新的摄像头编号，其他属性不变
     *
     * @param camera 摄像头编号
     */
    public void updateViolate(String camera) {
        this.camera_one = camera;
        // 开始验证此车辆
        this.isViolate = true;
        this.createTime = new Date();
        this.des = "判定压线";
        // 更新数据库中车辆违章的次数
        OpenTrafficQueryDao.updateCameraOneByNumberPlate(this, camera, new OpenTrafficQueryDao.Result() {
            @Override
            public void success(Object object) {
                L.e("更新了" + (int) object);
            }
        });
    }

    @Override
    public String toString() {
        return "ViolateCarBean{" +
                "camera_one='" + camera_one + '\'' +
                ", camera_two='" + camera_two + '\'' +
                ", numberPlate='" + numberPlate + '\'' +
                ", violateCount=" + violateCount +
                ", lastImagePath='" + lastImagePath + '\'' +
                ", createTime='" + createTime.toGMTString() + '\'' +
                ", isViolate=" + isViolate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViolateCarBean that = (ViolateCarBean) o;
        return Objects.equals(numberPlate, that.numberPlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberPlate);
    }
}
