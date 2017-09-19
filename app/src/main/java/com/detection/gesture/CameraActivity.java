package com.detection.gesture;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.List;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "CameraActivity";

    private static final int CAMERA_REQUEST_PERMISSION_CODE = 1;

    public static Intent getIntent(@NonNull final Context context) {
        return new Intent(context, CameraActivity.class);
    }

    private Camera mCamera;
    private CameraPreview mPreview;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        mEditText = findViewById(R.id.activity_camera_text);
        mEditText.setRawInputType(InputType.TYPE_CLASS_TEXT);
        mEditText.setTextIsSelectable(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (hasCameraPermission()) {
            initCamera();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onClick(final View view) {
        switch (view.getId()) {
            case R.id.camera_preview:
                doAutofocus();
                break;
            default:
                // Do nothing
        }
    }

    private void initCamera() {
        // Create an instance of Camera
        mCamera = getCameraInstance();
        if (mCamera == null) {
            finish();
            return;
        }

        setCameraDisplayOrientation();

        Camera.Parameters params = mCamera.getParameters();
        List<String> focusModes = params.getSupportedFocusModes();
        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
            // get Camera parameters
            // set the focus mode
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            // set Camera parameters
            mCamera.setParameters(params);
        }

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = findViewById(R.id.camera_preview);
        preview.setOnClickListener(this);
        preview.addView(mPreview);
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void setCameraDisplayOrientation() {
        Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(0, info);

        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int cameraOrientation;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            cameraOrientation = (info.orientation + degrees) % 360;
            cameraOrientation = (360 - cameraOrientation) % 360;  // compensate the mirror
        } else {  // back-facing
            cameraOrientation = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(cameraOrientation);
    }

    private void doAutofocus() {
        if (mCamera != null) {
            try {
                mCamera.autoFocus(null);
            } catch (Throwable throwable) {
                Log.e(TAG, "focus error", throwable);
            }
        }
    }

    private Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.e(TAG, "Camera open error", e);
        }
        return camera;
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(new String[] {Manifest.permission.CAMERA}, CAMERA_REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(
            final int requestCode,
            @NonNull final String[] permissions,
            @NonNull final int[] grantResults) {
        switch (requestCode) {
            case CAMERA_REQUEST_PERMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initCamera();
                } else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
