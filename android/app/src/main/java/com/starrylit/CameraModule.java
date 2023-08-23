package com.starrylit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Size;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.camera.view.PreviewView;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import android.view.View;
import android.view.Surface;
import android.util.Log;
import android.graphics.Bitmap;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import com.starrylit.ImageUtils;

public class CameraModule extends ReactContextBaseJavaModule {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.CAMERA };
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    public CameraModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "CameraModule";
    }

    @ReactMethod
    public void startCamera() {
        AppCompatActivity currentActivity = (AppCompatActivity) getCurrentActivity();
        if (currentActivity != null) {
            if (allPermissionsGranted(currentActivity)) {
                startCamera(currentActivity);
            } else {
                ActivityCompat.requestPermissions(
                        currentActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
            }
        }
    }

    private void startCamera(AppCompatActivity activity) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(activity);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider, activity);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(activity));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider, AppCompatActivity activity) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        ViewGroup container = activity.findViewById(android.R.id.content);
        container.removeAllViews();

        FrameLayout frameLayout = new FrameLayout(activity);
        frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT));
        container.addView(frameLayout);

        PreviewView previewView = new PreviewView(activity);
        previewView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        frameLayout.addView(previewView);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        //帧处理过程
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .setTargetResolution(new Size(1280, 720))
                // .setOutputImageRotationEnabled(true)
                // .setTargetRotation(Surface.ROTATION_0)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        //在本例中采用的主线程池，后续有待优化
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(activity), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(ImageProxy imageProxy) {
                //完成图片分析函数
                ImageUtils.imageProxyToTensor(imageProxy);
                imageProxy.close();
            }
        });
        Camera camera = cameraProvider.bindToLifecycle(activity, cameraSelector,imageAnalysis, preview);
    }

    private boolean allPermissionsGranted(AppCompatActivity activity) {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
