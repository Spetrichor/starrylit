package com.starrylit;

import android.graphics.Canvas;
import android.graphics.Bitmap;
import android.content.Context;
import android.view.*;
import android.util.Log;

public class OverlayView extends View {
    private Bitmap bitmap;
    private int[] offset = new int[2];
    private float x;
    private float y;

    public OverlayView(Context context) {
        super(context);
        offset[0] = 0;
        offset[1] = 0;
        x = 0f;
        y = 0f;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }

    public void setOffset(int[] offset) {
        this.offset[0] = offset[0];
        this.offset[1] = offset[1];
    }
    public void restart(){
        offset[0] = 0;
        offset[1] = 0;
        x = 0f;
        y = 0f;
        Log.d("OverlayView", "restart");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            x = x + offset[0];
            y = y + offset[1];
            canvas.drawBitmap(bitmap, x, y, null);
        }
    }
}
