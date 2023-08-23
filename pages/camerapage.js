// 在App.js文件中，导入React Native提供的NativeModules模块
import React from 'react';
import { View, Button, NativeModules } from 'react-native';
import { PermissionsAndroid } from 'react-native';

async function requestCameraPermission() {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.CAMERA,
      {
        title: '需要摄像头权限',
        message: '应用需要访问您的摄像头',
        buttonNeutral: '稍后询问',
        buttonNegative: '取消',
        buttonPositive: '确定',
      },
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('你现在可以使用摄像头了');
    } else {
      console.log('用户未授予摄像头权限');
    }
  } catch (err) {
    console.warn(err);
  }
}

// 创建一个按钮组件，给它添加一个onPress事件处理函数
const App = () => {
    // 这个函数的作用是调用NativeModules.CameraModule.startCameraPreview方法
    const onPress = () => {
        requestCameraPermission();
        NativeModules.CameraModule.startCamera();
    };

    return (
        <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
            <Button title="启动相机预览" onPress={onPress} />
        </View>
    );
};

export default App;
