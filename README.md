# android-gis-proxy-for-unity

This repository contains two libraries for working with the Google Identity package on the Android platform.

1. com.izhguzin.unityappcompatactivity
It contains the UnityPlayerAppCompatActivity class, which inherits from AppCompatActivity. Otherwise, it is a complete copy of UnityPlayerActivity. It also contains the UnityAppCompatThemeSelector theme, which is an analogue of UnityThemeSelector.
2. com.izhguzin.gsi
It contains two classes: GsiAppCompatActivity and GoogleSignInClientProxy.
GsiAppCompatActivity inherits from UnityPlayerAppCompatActivity and, most importantly, initializes the GoogleSignInClientProxy. You can inherit your activity from this class so that the GoogleIdentity package can work on Android devices.
If you don't want to inherit from GsiAppCompatActivity, you can initialize the GoogleSignInClientProxy yourself using the GoogleSignInClientProxy.init(AppCompatActivity) method.
