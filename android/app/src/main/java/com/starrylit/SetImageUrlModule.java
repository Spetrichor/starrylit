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
import android.util.Base64;
public class SetImageUrlModule extends ReactContextBaseJavaModule {
    public SetImageUrlModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "SetImageUrlModule";
    }

    @ReactMethod
    public void setImageURL(String base64Image) {
        Log.d("setImageURL", "setImageURL: " + base64Image);
        // 根据url读取本地图片文件为bitmap
        try {
            // 解码 Base64 字符串
            byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
            // 将字节数组转换为 Bitmap 对象
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            if (bitmap == null)
                Log.d("setImageURL", "bitmap is null");
            else
                Log.d("setImageURL", "bitmap is not null");

        } catch (Exception e) {
            Log.d("setImageURL", "fail");
            throw new RuntimeException(e);
        }
    }
}
