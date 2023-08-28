package com.starrylit;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.android.Utils;
import java.util.List;
import org.opencv.core.Point;
import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfFloat;

public class OpticalFlow {
    private Bitmap bitmap_0;
    private Bitmap bitmap_1;
    int[] array = new int[2];

    public OpticalFlow() {
        this.bitmap_0 = null;
        this.bitmap_1 = null;
        array[0] = 0;
        array[1] = 0;
    }

    public void setBitmap_0(Bitmap bitmap) {
        this.bitmap_0 = bitmap;
    }

    public void setBitmap_1(Bitmap bitmap) {
        if (this.bitmap_1 != null) {
            this.bitmap_0 = this.bitmap_1;
        }
        this.bitmap_1 = bitmap;
    }

    public int[] opticalFlow() {
        Mat gray1 = new Mat();
        Mat gray2 = new Mat();

        // 将bitmap转换为Mat
        Utils.bitmapToMat(bitmap_0, gray1);
        Utils.bitmapToMat(bitmap_1, gray2);
        // 转换为灰度图
        Imgproc.cvtColor(gray1, gray1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.cvtColor(gray2, gray2, Imgproc.COLOR_BGR2GRAY);
        // 检测第一幅图像中的特征点
        MatOfPoint points1 = new MatOfPoint();
;
        Imgproc.goodFeaturesToTrack(gray1, points1, 100, 0.3, 7);

        // 计算光流
        MatOfPoint2f points2 = new MatOfPoint2f();
        MatOfByte status = new MatOfByte();
        MatOfFloat err = new MatOfFloat();
        MatOfPoint2f points1f = new MatOfPoint2f(points1.toArray());
        Video.calcOpticalFlowPyrLK(gray1, gray2, points1f, points2, status, err);

        // 计算目标物体在两幅图像中的位移量
        double xShift = 0;
        double yShift = 0;
        List<Point> pointsList1 = points1f.toList();
        List<Point> pointsList2 = points2.toList();
        for (int i = 0; i < pointsList1.size(); i++) {
            Point point1 = pointsList1.get(i);
            Point point2 = pointsList2.get(i);
            xShift += point2.x - point1.x;
            yShift += point2.y - point1.y;
        }
        xShift /= pointsList1.size();
        yShift /= pointsList1.size();

        array[0] = (int) xShift;
        array[1] = (int) yShift;
        // Log.d("OpticalFlow", "xShift: " + xShift + " yShift: " + yShift);
        return array;
    }
}