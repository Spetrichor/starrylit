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
                        int centerAlpha = opacity;
                        int outerAlpha = (maxOpacity - centerAlpha) / (radius + 1);
                        int innerAlpha = outerAlpha * radius + centerAlpha;
                        circlePaint.setAlpha(innerAlpha);
                        circlePaint.setStrokeWidth(0); // 设置半径

                        // 在这里绘制一个圆
                        canvas.drawCircle(x, y, radius, circlePaint);
                    }
                }
            }
        }
        return transparentBitmap;
    }
}