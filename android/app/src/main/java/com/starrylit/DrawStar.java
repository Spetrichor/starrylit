package com.starrylit;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.content.Context;
import android.view.*;
import android.util.Log;
import android.graphics.Paint;
import android.graphics.Point;

import java.util.List;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.Random;
//导入OpenCV库
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Size;
import org.opencv.core.CvType;
import org.opencv.core.Core;
import com.starrylit.OrientationActivity;
import com.starrylit.SetImageUrlModule;
import java.nio.IntBuffer;

public class DrawStar {
    private static boolean isdrawn = false;
    private static Bitmap transBitmap = null;

    public static Bitmap drawStar(Bitmap maskBitmap, int mScreenWidth, int mScreenHeight) {
        // 获取OrientationActivity.values数组，它包含了设备的方位、横滚和俯仰角
        float[] values = OrientationActivity.values;
        // 根据这些角度来调整你绘制星星的图形的方向或位置
        // 这里只是一个简单的示例，你可以根据你自己的需求来修改
        float azimuth = values[0]; // 方位角
        float pitch = values[1]; // 俯仰角
        float roll = values[2]; // 横滚角
        Log.d("Button", "方位角：" + azimuth + " 俯仰角：" + pitch + " 横滚角：" + roll);
        Bitmap transparentBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        if (!isdrawn) {
            transBitmap = DrawStar.Sketch(mScreenWidth, mScreenHeight);
            isdrawn = true;
        }
        Canvas canvas = new Canvas(transparentBitmap);
        List<Point> circlePositions = new ArrayList<>();
        // Random random = new Random();
        // int maxRadius = 3; // 圆的最大半径
        // int maxOpacity = 255; // 圆的最大透明度
        // for (int y = 0; y < mScreenHeight; y++) {
        // for (int x = 0; x < mScreenWidth; x++) {
        // if (originalbitmap.getPixel(x, y) == -2147483648) {
        // if (random.nextInt(10000) == 0) { // 每100个像素中有一个圆
        // int radius = random.nextInt(maxRadius); // 随机半径
        // int opacity = random.nextInt(maxOpacity); // 随机透明度
        // Point circlePosition = new Point(x, y);
        // circlePositions.add(circlePosition); // 存储圆的位置
        // // 创建圆的画笔
        // Paint circlePaint = new Paint();
        // circlePaint.setColor(Color.WHITE);
        // circlePaint.setAlpha(opacity);
        // circlePaint.setStrokeWidth(0); // 设置半径

        // // 在这里绘制一个圆
        // canvas.drawCircle(x, y, radius, circlePaint);
        // }
        // }
        // }
        // }
        // return transparentBitmap;
        return transBitmap;
    }

    public static Bitmap transImage(int mScreenWidth, int mScreenHeight) {
        try {
            Bitmap bitmap = SetImageUrlModule.getBitmap();
            Mat mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);
            Mat gray = new Mat();
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
            // Imgproc.blur(gray, gray, new Size(3, 3));
            Imgproc.GaussianBlur(gray, gray, new Size(3, 3), 5, 5);
            // Mat thresh = new Mat();
            // Imgproc.adaptiveThreshold(gray, thresh, 255,
            // Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
            // Mat edges = new Mat();
            // Imgproc.Canny(thresh, edges, 100, 200);
            // Utils.matToBitmap(edges, bitmap);
            Mat thes = new Mat();
            double maxValue = 255;
            Imgproc.adaptiveThreshold(gray, thes, maxValue, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 13,
                    5);
            // Mat res = new Mat();
            // Imgproc.Canny(thes, res, 100, 200);
            Utils.matToBitmap(thes, bitmap);
            gray.release();
            // res.release();
            thes.release();
            // edges.release();
            // 创建一个新的Bitmap并调整大小
            float scaleFactor = Math.min(mScreenWidth * 1f / bitmap.getWidth(),
                    mScreenHeight * 1f / bitmap.getHeight());
            int scaleWidth = (int) (bitmap.getWidth() * scaleFactor / 2);
            int scaleHeight = (int) (bitmap.getHeight() * scaleFactor / 2);

            Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth,
                    scaleHeight, false);
            return scaleBitmap;
        } catch (Exception e) {
            Log.d("Button", e.toString());
            return null;
        }
    }

    public static Bitmap Sketch(int mScreenWidth, int mScreenHeight) {
        Bitmap bitmap = SetImageUrlModule.getBitmap();
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Mat src = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Mat dest = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Mat grey = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Mat invert = new Mat(bitmap.getHeight(), bitmap.getWidth(), CvType.CV_8UC4);
        Bitmap inv, gray;
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, grey, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(grey, grey, Imgproc.COLOR_GRAY2RGBA, 4);
        Core.bitwise_not(grey, invert);
        Imgproc.GaussianBlur(invert, invert, new Size(11, 11), 0);
        inv = Bitmap.createBitmap(invert.cols(), invert.rows(), Bitmap.Config.ARGB_8888);
        gray = Bitmap.createBitmap(invert.cols(), invert.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(invert, inv);
        Utils.matToBitmap(grey, gray);
        Bitmap b = ColorDodgeBlend(inv, gray);
        // Bitmap processImg = Bitmap.createBitmap(dest.cols(), dest.rows(),
        // Bitmap.Config.ARGB_8888);
        // Utils.matToBitmap(dest, processImg);
        float scaleFactor = Math.min(mScreenWidth * 1f / b.getWidth(),
                mScreenHeight * 1f / b.getHeight());
        int scaleWidth = (int) (b.getWidth() * scaleFactor );
        int scaleHeight = (int) (b.getHeight() * scaleFactor );

        Bitmap scaleBitmap = Bitmap.createScaledBitmap(b, scaleWidth,
                scaleHeight, false);
        return scaleBitmap;
    }

    private static int colordodge(int in1, int in2) {
        float image = (float) in2;
        float mask = (float) in1;
        return ((int) ((image == 255) ? image : Math.min(255, (((long) mask << 8) / (255 - image))))) * 6;
    }

    public static Bitmap ColorDodgeBlend(Bitmap source, Bitmap layer) {
        Bitmap base = source.copy(Bitmap.Config.ARGB_8888, true);
        Bitmap blend = layer.copy(Bitmap.Config.ARGB_8888, false);
        IntBuffer buffBase = IntBuffer.allocate(base.getWidth() * base.getHeight());
        base.copyPixelsToBuffer(buffBase);
        buffBase.rewind();
        IntBuffer buffBlend = IntBuffer.allocate(blend.getWidth() * blend.getHeight());
        blend.copyPixelsToBuffer(buffBlend);
        buffBlend.rewind();
        IntBuffer buffOut = IntBuffer.allocate(base.getWidth() * base.getHeight());
        buffOut.rewind();
        while (buffOut.position() < buffOut.limit()) {
            int filterInt = buffBlend.get();
            int srcInt = buffBase.get();
            int redValueFilter = Color.red(filterInt);
            int greenValueFilter = Color.green(filterInt);
            int blueValueFilter = Color.blue(filterInt);
            int redValueSrc = Color.red(srcInt);
            int greenValueSrc = Color.green(srcInt);
            int blueValueSrc = Color.blue(srcInt);
            int redValueFinal = colordodge(redValueFilter, redValueSrc);
            int greenValueFinal = colordodge(greenValueFilter, greenValueSrc);
            int blueValueFinal = colordodge(blueValueFilter, blueValueSrc);
            int pixel = Color.argb(255, redValueFinal, greenValueFinal, blueValueFinal);
            buffOut.put(pixel);
        }
        buffOut.rewind();
        base.copyPixelsFromBuffer(buffOut);
        blend.recycle();
        return base;
    }
}