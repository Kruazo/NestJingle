package com.intetics.kkilyachkov.nestjingleapp;

import android.hardware.Camera;
import android.util.Log;

import static android.hardware.Camera.Parameters.FLASH_MODE_OFF;
import static android.hardware.Camera.Parameters.FLASH_MODE_TORCH;

public class FlashlightThread extends Thread {

    private static final int BLINK_QUANTITY = 20;
    private static final int DELAY = 100; // in ms

    private Camera mCamera;
    private Camera.Parameters mParams;

    private boolean isFlashlightOn;

    @Override
    public void run() {
        try {
            mCamera = Camera.open();
            mCamera.setPreviewDisplay(null);
            mCamera.startPreview();
            for (int i = 0; i < BLINK_QUANTITY; i++) {
                toggleFlashLight();
                sleep(DELAY);
            }
            mCamera.stopPreview();
            mCamera.release();
        } catch (Exception e) {
            Log.e(FlashlightThread.class.getSimpleName(), e.getMessage());
        }
    }

    public void turnOn() {
        mParams.setFlashMode(FLASH_MODE_TORCH);
        mCamera.setParameters(mParams);
        isFlashlightOn = true;
    }

    public void turnOff() {
        if (mParams.getFlashMode().equals(FLASH_MODE_TORCH)) {
            mParams.setFlashMode(FLASH_MODE_OFF);
            mCamera.setParameters(mParams);
        }
        isFlashlightOn = false;
    }

    public void toggleFlashLight() {
        mParams = mCamera.getParameters();
        if (isFlashlightOn) {
            turnOff();
        } else {
            turnOn();
        }
    }
}
