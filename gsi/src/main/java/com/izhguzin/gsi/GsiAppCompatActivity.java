package com.izhguzin.gsi;

import android.os.Bundle;

import com.izhguzin.unityappcompatactivity.UnityPlayerAppCompatActivity;

public class GsiAppCompatActivity extends UnityPlayerAppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInClientProxy.init(this);
    }
}
