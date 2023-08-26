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
import com.starrylit.LoadImage;

public class DrawStar {
    public static Bitmap drawStar(Bitmap originalbitmap, int mScreenWidth, int mScreenHeight) {
        Bitmap transparentBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(transparentBitmap);
        List<Point> circlePositions = new ArrayList<>();
        Random random = new Random();
        int maxRadius = 2; // 圆的最大半径
        int maxOpacity = 255; // 圆的最大透明度
        for (int y = 0; y < mScreenHeight; y++) {
            for (int x = 0; x < mScreenWidth; x++) {
                if (originalbitmap.getPixel(x, y) == -2147483648) {
                    if (random.nextInt(10000) == 0) { // 每100个像素中有一个圆
                        int radius = random.nextInt(maxRadius); // 随机半径
                        int opacity = random.nextInt(maxOpacity); // 随机透明度
                        Point circlePosition = new Point(x, y);
                        circlePositions.add(circlePosition); // 存储圆的位置
                        // 创建圆的画笔
                        Paint circlePaint = new Paint();
                        circlePaint.setColor(Color.WHITE);
                        circlePaint.setAlpha(opacity);
                        circlePaint.setStrokeWidth(0); // 设置半径

                        // 在这里绘制一个圆
                        canvas.drawCircle(x, y, radius, circlePaint);
                    }
                }
            }
        }
        return transparentBitmap;
    }

    // public static Bitmap transImage(int mScreenWidth, int mScreenHeight) {
    //     Bitmap bitmap = LoadImage.loadImage(filePath);
    //     Mat mat = new Mat();
    //     Utils.bitmapToMat(bitmap, mat);
    //     Mat gray = new Mat();
    //     Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
    //     Imgproc.blur(mat, gray, new Size(3, 3));
    //     Imgproc.GaussianBlur(mat, gray, new Size(3, 3), 5, 5);
    //     Mat thresh = new Mat();
    //     Imgproc.adaptiveThreshold(gray, thresh, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 11, 2);
    //     Mat edges = new Mat();
    //     Imgproc.Canny(thresh, edges, 100, 200);
    //     Utils.matToBitmap(edges, bitmap);
    //     gray.release();
    //     thresh.release();
    //     edges.release();
    //     // 创建一个新的Bitmap并调整大小
    //     float scaleFactor = Math.min(mScreenWidth * 1f / bitmap.getWidth(), mScreenHeight * 1f / bitmap.getHeight());
    //     int scaleWidth = (int) (bitmap.getWidth() * scaleFactor);
    //     int scaleHeight = (int) (bitmap.getHeight() * scaleFactor);

    //     Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, false);
    //     Log.d("DrawStar","成功创建了一个Bitmap");
    //     return scaleBitmap;
    // }
}