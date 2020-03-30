package com.realmax.opentrafficversion.bean;

import android.graphics.Bitmap;

public class CurrentCameraBean {
    private Bitmap bitmap;
    private int cameraPostion;

    public CurrentCameraBean(Bitmap bitmap, int cameraPostion) {
        this.bitmap = bitmap;
        this.cameraPostion = cameraPostion;
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
}
