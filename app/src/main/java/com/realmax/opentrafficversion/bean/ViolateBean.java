package com.realmax.opentrafficversion.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Objects;

@DatabaseTable(tableName = "violateTable")
public class ViolateBean {
    @DatabaseField(columnName = "id", generatedId = true)
    private int id;
    @DatabaseField(columnName = "camera")
    private String camera;
    @DatabaseField(columnName = "numberPlate")
    private String numberPlate;
    @DatabaseField(columnName = "violateCount")
    private int violateCount;
    /**
     * 是否记录违章
     */
    @DatabaseField(columnName = "isViolate")
    private boolean isViolate = false;
    @DatabaseField(columnName = "camera_two")
    private String camera_two;

    public ViolateBean() {
    }

    public ViolateBean(String camera, String numberPlate, int violateCount, boolean isViolate) {
        this.camera = camera;
        this.numberPlate = numberPlate;
        this.violateCount = violateCount;
        this.isViolate = isViolate;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = camera;
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

    public boolean isViolate() {
        return isViolate;
    }

    public void setViolate(boolean violate) {
        isViolate = violate;
    }

    /**
     * 违章判定，对指定摄像头进行违章判定，例如：A1抓拍第一次压线，第二次为A2拍到同样的车两压线判定为为违章
     *
     * @param camera      拍摄的摄像头
     * @param numberPlate 识别出的车牌号
     * @return 返回判断标示符：true表示违章，违章次数+1，false表示没有违章
     */
    public boolean ViolateCheck(String camera, String numberPlate) {
        if (/*this.isViolate && */camera.charAt(0) == this.camera.charAt(0) || camera.equals(this.camera_two) && numberPlate.equals(this.numberPlate)) {
            this.camera_two = camera;
            this.violateCount++;
            // 停止继续验证此车辆
            this.isViolate = false;
            return true;
        }
        return false;
    }

    /**
     * 更新当前车辆经过的摄像头，例如：第一次压线没闯红灯，当第二次压线时，将当前的摄像头编号更新为最新的摄像头编号，其他属性不变
     *
     * @param camera 摄像头编号
     */
    public void updateViolate(String camera) {
        this.camera = camera;
        // 开始验证此车辆
        this.isViolate = true;
    }

    @Override
    public String toString() {
        return "ViolateBean{" +
                "camera='" + camera + '\'' +
                ", numberPlate='" + numberPlate + '\'' +
                ", violateCount=" + violateCount +
                ", isViolate=" + isViolate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ViolateBean that = (ViolateBean) o;
        return Objects.equals(numberPlate, that.numberPlate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(numberPlate);
    }
}
