//导入必要的包
package com.starrylit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Context;

import java.io.IOException;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import android.util.Log;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class SetImageUrlModule extends ReactContextBaseJavaModule {
    public SetImageUrlModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "SetImageUrlModule";
    }

    @ReactMethod
    public void setImageURL(String url) {
        Log.d("setImageURL", "setImageURL: " + url);
        // 根据url读取本地图片文件为bitmap
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(url);
            Log.d("setImageURL", "success");

        } catch (Exception e) {
            Log.d("setImageURL", "fail");
            throw new RuntimeException(e);
        }
    }
}
