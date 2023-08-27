package com.starrylit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Size;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.appcompat.app.ActionBar;
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
import android.content.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.common.util.concurrent.ListenableFuture;
import org.tensorflow.lite.task.vision.segmenter.ImageSegmenter;
import org.tensorflow.lite.task.vision.segmenter.Segmentation;
import org.tensorflow.lite.task.vision.segmenter.OutputType;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.gpu.CompatibilityList;
import java.util.concurrent.ExecutionException;
import java.util.*;
import com.starrylit.RegionProcess;
import com.starrylit.ColorLabel;
import com.starrylit.OverlayView;
import com.starrylit.DrawStar;
import com.starrylit.FrameProcessor;
import android.media.Image;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.opencv.android.OpenCVLoader;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.view.KeyEvent;
import android.content.Intent;
import androidx.camera.core.CameraX;

public class CameraModule extends ReactContextBaseJavaModule {
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[] { Manifest.permission.CAMERA };
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private Button button;
    private boolean isfirst = true;
    // private List<Segmentation> results = new ArrayList<>();

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
        try {
            // 初始化OpenCV
            OpenCVLoader.initDebug();
            // Create an ImageSegmenterOptions object.
            Context context = activity.getApplicationContext();
            CompatibilityList compatibilityList = new CompatibilityList();
            Log.d("GPU Available", compatibilityList.isDelegateSupportedOnThisDevice() ? "true" : "false");
            // 获取屏幕宽高
            WindowManager wm = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
            DisplayMetrics metrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(metrics);
            int mScreenHeight = metrics.heightPixels;
            int mScreenWidth = metrics.widthPixels;
            // 获取完毕
            // 创建新视图
            OverlayView overlayView = new OverlayView(context);

            // 模型预测代码
            ImageSegmenter.ImageSegmenterOptions options = ImageSegmenter.ImageSegmenterOptions.builder()
                    .setBaseOptions(BaseOptions.builder().setNumThreads(4).build())
                    .setOutputType(OutputType.CATEGORY_MASK)
                    .build();
            ImageSegmenter imageSegmenter = ImageSegmenter.createFromFileAndOptions(context,
                    "lite-model_deeplabv3-mobilenetv2-ade20k_1_default_2.tflite", options);
            ImageProcessor imageProcessor = new ImageProcessor.Builder()
                    .add(new ResizeOp(513, 513, ResizeOp.ResizeMethod.BILINEAR))
                    .build();

            // 创建视图
            // 创建一个相对布局，用于放置相机预览和其他视图
            RelativeLayout relativeLayout = new RelativeLayout(activity);
            relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
            // 创建视图
            Preview preview = new Preview.Builder()
                    .build();
            // 创建相机选择器
            CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();
            // 获取容器的视图
            ViewGroup container = activity.findViewById(android.R.id.content);
            container.removeAllViews();
            // 创建一个帧布局，用于放置预览视图和叠加视图
            FrameLayout frameLayout = new FrameLayout(activity);
            frameLayout.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT));
            container.addView(frameLayout);
            container.addView(relativeLayout);
            // 创建一个预览视图
            PreviewView previewView = new PreviewView(activity);
            previewView.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            frameLayout.addView(previewView);
            // 绘制OverlayView视图
            overlayView.setLayoutParams(
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
            frameLayout.addView(overlayView);
            // 帧处理
            // 帧处理过程
            ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                    .setTargetResolution(new Size(1280, 720))
                    .setOutputImageRotationEnabled(true)
                    .setTargetRotation(Surface.ROTATION_0)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();
            ExecutorService executorService = Executors.newFixedThreadPool(4);
            Thread thread = new Thread(
                    new FrameProcessor(imageSegmenter, imageProcessor, imageAnalysis, executorService,
                            overlayView, mScreenWidth, mScreenHeight));
            // 设置按钮相关
            Button button = new Button(activity);
            button.setText("还没写样式 ;)");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isfirst = true;
                    // 点击按钮后的处理
                    Log.d("Button", "点击了按钮");
                    imageAnalysis.clearAnalyzer();
                    thread.interrupt();
                    Thread thread = new Thread(
                            new FrameProcessor(imageSegmenter, imageProcessor, imageAnalysis, executorService,
                                    overlayView, mScreenWidth, mScreenHeight));
                    thread.start();
                }
            });
            RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            buttonParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            buttonParams.setMargins(0, 0, 0, 0); // 如果需要，可以设置按钮的边距
            relativeLayout.addView(button, buttonParams);
            // overlayView.setAlpha(0.5f);
            preview.setSurfaceProvider(previewView.getSurfaceProvider());

            Camera camera = cameraProvider.bindToLifecycle(activity, cameraSelector, imageAnalysis, preview);
        } catch (Exception e) {
            // 打印错误信息
            Log.e("CameraModule", e.toString());
        }
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
