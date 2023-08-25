package com.starrylit;
import android.graphics.Color;

public class ColorLabel {
    private int id;
    private String label;
    private int rgbColor;
    private boolean isExist;

    public ColorLabel(int id, String label, int rgbColor) {
        this(id, label, rgbColor, false);
    }

    public ColorLabel(int id, String label, int rgbColor, boolean isExist) {
        this.id = id;
        this.label = label;
        this.rgbColor = rgbColor;
        this.isExist = isExist;
    }

    public int getColor() {
        // Use completely transparent for the background color.
        return Color.argb(
                128,
                Color.red(rgbColor),
                Color.green(rgbColor),
                Color.blue(rgbColor));
    }

    public void setIsExist(boolean isExist) {
        this.isExist = isExist;
    }
}