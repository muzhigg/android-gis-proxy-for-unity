package com.izhguzin.gsi;

import android.os.Bundle;

import com.izhguzin.unityappcompatactivity.UnityPlayerAppCompatActivity;

public class GsiAppCompatActivity extends UnityPlayerAppCompatActivity {

    public static GoogleSignInClientProxy clientProxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        clientProxy = new GoogleSignInClientProxy(this);
    }
}
