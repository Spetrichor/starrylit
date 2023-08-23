import { Text, StyleSheet, View } from 'react-native'
import React, { Component } from 'react'
import Camerapage from './pages/camerapage'
export default class App extends Component {
  render() {
    return (
      <View style={{ flex: 1 }}>
        <Camerapage></Camerapage>
      </View>
    )
  }
}

const styles = StyleSheet.create({})