// 在App.js文件中，导入React Native提供的NativeModules模块
import React from 'react';
import { View, Button, NativeModules } from 'react-native';
import { PermissionsAndroid } from 'react-native';
import { launchImageLibrary } from 'react-native-image-picker';
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
  return Promise.resolve('Camera permission granted');
}
async function requestExternalStoragePermission() {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.READ_EXTERNAL_STORAGE,
      {
        title: '需要读写外部存储权限',
        message: '应用需要读写您的外部存储',
        buttonNeutral: '稍后询问',
        buttonNegative: '取消',
        buttonPositive: '确定',
      },
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('你现在可以读写外部存储了');
    } else {
      console.log('用户未授予读写外部存储权限');
    }
  } catch (err) {
    console.warn(err);
  }
  return Promise.resolve('READ_EXTERNAL_STORAGE permission granted');
}
async function requestExternalStoragePermission_() {
  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
      {
        title: '需要读写外部存储权限',
        message: '应用需要读写您的外部存储',
        buttonNeutral: '稍后询问',
        buttonNegative: '取消',
        buttonPositive: '确定',
      },
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('你现在可以读写外部存储了');
    } else {
      console.log('用户未授予读写外部存储权限');
    }
  } catch (err) {
    console.warn(err);
  }
  return Promise.resolve('WRITE_EXTERNAL_STORAGE permission granted');
}
const openPhotoPicker = async () => {
  const options = {
    title: 'Select Photo',
    mediaType: 'photo',
    quality: 0.8,
    includeExtra: false,
    selectionLimit: 1,
  };

  const result = await launchImageLibrary(options);

  if (result.error) {
    console.log(result.error);
  } else {
    console.log(result.assets[0].uri);
  }
}
// 创建一个按钮组件，给它添加一个onPress事件处理函数
const App = () => {
  // 这个函数的作用是调用NativeModules.CameraModule.startCameraPreview方法
  const onPress = async () => {
    await requestCameraPermission();
    NativeModules.CameraModule.startCamera();
  };
  const onPress_c = async () => {
    await requestExternalStoragePermission();
    await requestExternalStoragePermission_();
    openPhotoPicker();
  }
  return (
    <View style={{ flex: 1, justifyContent: 'center', alignItems: 'center' }}>
      <Button title="启动相机预览" onPress={onPress} />
      <Button title="获取存储权限" onPress={onPress_c} />
    </View>
  );
};

export default App;
