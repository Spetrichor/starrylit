package com.starrylit;

import java.lang.Runnable;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.ImageAnalysis;
import android.graphics.Bitmap;
import java.lang.InterruptedException;
import java.lang.Thread;
import java.util.ArrayList;
import java.util.List;
import org.tensorflow.lite.task.vision.segmenter.ImageSegmenter;
import org.tensorflow.lite.task.vision.segmenter.Segmentation;
import org.tensorflow.lite.task.vision.segmenter.OutputType;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.gpu.CompatibilityList;
import java.util.concurrent.ExecutorService;
import com.starrylit.OverlayView;
import com.starrylit.RegionProcess;
import com.starrylit.DrawStar;
import android.util.Log;

class FrameProcessor implements Runnable {
    private boolean isfirst;
    private ImageSegmenter imageSegmenter;
    private ImageProcessor imageProcessor;
    private ImageAnalysis imageAnalysis;
    private ExecutorService executorService;
    private List<Segmentation> results = new ArrayList<>();
    private OverlayView overlayView;
    private int mScreenWidth;
    private int mScreenHeight;

    public FrameProcessor(ImageSegmenter imageSegmenter, ImageProcessor imageProcessor, ImageAnalysis imageAnalysis,
            ExecutorService executorService, OverlayView overlayView, int mScreenWidth, int mScreenHeight) {
        this.imageSegmenter = imageSegmenter;
        this.imageProcessor = imageProcessor;
        this.imageAnalysis = imageAnalysis;
        this.executorService = executorService;
        this.overlayView = overlayView;
        this.mScreenWidth = mScreenWidth;
        this.mScreenHeight = mScreenHeight;
        isfirst = true;
    }

    @Override
    public void run() {
        imageAnalysis.setAnalyzer(executorService, new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(ImageProxy imageProxy) {
                // 第一次执行帧，进行图像分割
                if (isfirst) {
                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    Bitmap bitmap = imageProxy.toBitmap();
                    tensorImage.load(bitmap);
                    TensorImage tensorImage_ = imageProcessor.process(tensorImage);
                    Log.d("Button", "开始预测...");
                    results = imageSegmenter.segment(tensorImage_);
                    Log.d("Button", "预测完毕");
                    // results即为预测结果
                    // 设置标志位
                    isfirst = false;
                }
                // 图像分割完毕后，获取图像分割结果并开始绘制
                else {
                    Log.d("Button", "开始绘制...");
                    overlayView.setBitmap(DrawStar.drawStar(RegionProcess.getMask(results, mScreenWidth,
                            mScreenHeight), mScreenWidth, mScreenHeight));
                    Log.d("Button", "绘制完毕...");
                }
                imageProxy.close();
            }
        });

        // while (!Thread.currentThread().isInterrupted()) {
        // ImageProxy imageProxy = imageAnalysis.acquireLatestImage();
        // if (imageProxy != null) {
        // if (isfirst) {
        // TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
        // Bitmap bitmap = imageProxy.toBitmap();
        // tensorImage.load(bitmap);
        // TensorImage tensorImage_ = imageProcessor.process(tensorImage);
        // Log.d("FrameProcess", "开始预测...");
        // results = imageSegmenter.segment(tensorImage_);
        // Log.d("FrameProcess", "预测完毕");
        // isfirst = false;
        // } else {
        // Log.d("FrameProcess", "开始绘制...");
        // overlayView.setBitmap(RegionProcess.getMask(results, mScreenWidth,
        // mScreenHeight));
        // Log.d("FrameProcess", "绘制完毕...");
        // }
        // imageProxy.close();
        // } else {
        // try {
        // Thread.sleep(1000 / 30); // 根据帧率调整睡眠时间
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // }
        // }
    }
}