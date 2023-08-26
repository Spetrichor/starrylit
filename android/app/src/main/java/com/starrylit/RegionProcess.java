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
import android.content.Context;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.common.util.concurrent.ListenableFuture;
import org.tensorflow.lite.task.vision.segmenter.ImageSegmenter;
import org.tensorflow.lite.task.vision.segmenter.Segmentation;
import org.tensorflow.lite.task.vision.segmenter.OutputType;
import org.tensorflow.lite.task.vision.segmenter.ColoredLabel;
import org.tensorflow.lite.task.core.BaseOptions;
import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.Tensor;
import org.tensorflow.lite.gpu.CompatibilityList;
import java.util.concurrent.ExecutionException;
import java.util.*;
import android.media.Image;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Optional;
import android.graphics.Color;

//导入或定义WINDOW_SERVICE
public class RegionProcess {

    public static Bitmap getMask(List<Segmentation> results, int mScreenWidth, int mScreenHeight) {
        List<ColorLabel> colorLabels = new ArrayList<>();
        for (int i = 0; i < results.get(0).getColoredLabels().size(); i++) {
            ColoredLabel coloredLabel = results.get(0).getColoredLabels().get(i);
            int color;
            if (coloredLabel.getlabel().equals("sky")) {
                color = Color.WHITE;
            } else {
                color = Color.BLACK;
            }
            colorLabels.add(new ColorLabel(i, coloredLabel.getlabel(), color));
        }
        TensorImage maskTensor = results.get(0).getMasks().get(0);
        byte[] maskArray = maskTensor.getBuffer().array();
        int[] pixels = new int[maskArray.length];
        for (int i = 0; i < maskArray.length; i++) {
            // Set isExist flag to true if any pixel contains this color.
            ColorLabel colorLabel = colorLabels.get(maskArray[i] & 0xff);// 通过像素值判断种类
            colorLabel.setIsExist(true);// 像素值不为0则该种类存在
            int color = colorLabel.getColor();
            pixels[i] = color;
        }
        Bitmap image = Bitmap.createBitmap(
                pixels,
                maskTensor.getWidth(),
                maskTensor.getHeight(),
                Bitmap.Config.ARGB_8888);
        // 创建一个新的Bitmap并调整大小
        float scaleFactor = Math.max(mScreenWidth * 1f / image.getWidth(), mScreenHeight * 1f / image.getHeight());
        int scaleWidth = (int) (image.getWidth() * scaleFactor);
        int scaleHeight = (int) (image.getHeight() * scaleFactor);

        Bitmap scaleBitmap = Bitmap.createScaledBitmap(image, scaleWidth, scaleHeight, false);
        Bitmap croppedBitmap = Bitmap.createBitmap(scaleBitmap, (scaleWidth - mScreenWidth) / 2,
                (scaleHeight - mScreenHeight) / 2, mScreenWidth, mScreenHeight);
        return croppedBitmap;
    }
}
