package com.starrylit;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.content.Context;
import android.view.*;
import android.util.Log;

public class OverlayView extends View {
    private Bitmap bitmap;

    public OverlayView(Context context) {
        super(context);
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0f, 0f, null);
            Log.d("FrameProcess","绘制完毕");
        }
    }
}
