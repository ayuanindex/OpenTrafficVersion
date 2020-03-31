package com.realmax.opentrafficversion.bean;

import android.graphics.Bitmap;

import java.util.Date;

public class CurrentCameraBean {
    private Bitmap bitmap;
    private int cameraPostion;
    private Date createTime;

    public CurrentCameraBean(Bitmap bitmap, int cameraPostion, Date createTime) {
        this.bitmap = bitmap;
        this.cameraPostion = cameraPostion;
        this.createTime = createTime;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getCameraPostion() {
        return cameraPostion;
    }

    public void setCameraPostion(int cameraPostion) {
        this.cameraPostion = cameraPostion;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
