package com.starrylit;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
public class OrientationActivity extends Activity implements SensorEventListener {

    //定义传感器管理器和传感器对象
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;

    //定义重力和地磁场数据数组
    private float[] gravity = new float[3];
    private float[] geomagnetic = new float[3];

    //定义旋转矩阵和输出结果数组
    private float[] R = new float[9];
    public static float[] values = new float[3];

    //定义显示方位、横滚和俯仰角的文本视图
    private TextView azimuthText;
    private TextView pitchText;
    private TextView rollText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_orientation);

        //获取传感器管理器和传感器对象
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        //获取文本视图对象
        // azimuthText = (TextView) findViewById(R.id.azimuthText);
        // pitchText = (TextView) findViewById(R.id.pitchText);
        // rollText = (TextView) findViewById(R.id.rollText);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //注册传感器监听器
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //注销传感器监听器
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //根据传感器类型获取重力和地磁场数据
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values.clone();
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values.clone();
        }
        //计算旋转矩阵
        SensorManager.getRotationMatrix(R, null, gravity, geomagnetic);
        //计算方位、横滚和俯仰角
        SensorManager.getOrientation(R, values);

        //将弧度转换为角度，并显示在文本视图上
        // azimuthText.setText("方位角：" + Math.toDegrees(values[0]) + "°");
        // pitchText.setText("俯仰角：" + Math.toDegrees(values[1]) + "°");
        // rollText.setText("横滚角：" + Math.toDegrees(values[2]) + "°");
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //无需处理精度变化事件
    }
}
