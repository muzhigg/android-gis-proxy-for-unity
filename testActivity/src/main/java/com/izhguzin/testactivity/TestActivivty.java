package com.izhguzin.testactivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.izhguzin.gsi.GoogleSignInClientProxy;
import com.izhguzin.gsi.GsiAppCompatActivity;

public class TestActivivty extends GsiAppCompatActivity implements View.OnClickListener {


    private static final int SIGN_IN = 1;
    private static final int SIGN_OUT = 2;
    private static final int REVOKE = 3;
    private static final int REFRESH = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        LinearLayoutCompat layoutCompat = new LinearLayoutCompat(this);

        AppCompatButton btn = new AppCompatButton(this);
        btn.setId(SIGN_IN);
        btn.setText("Sign In");
        btn.setOnClickListener(this);
        layoutCompat.addView(btn);

        AppCompatButton outBtn = new AppCompatButton(this);
        outBtn.setId(SIGN_OUT);
        outBtn.setText("Sign Out");
        outBtn.setOnClickListener(this);
        layoutCompat.addView(outBtn);

        AppCompatButton revokeBtn = new AppCompatButton(this);
        revokeBtn.setId(REVOKE);
        revokeBtn.setText("Revoke");
        revokeBtn.setOnClickListener(this);
        layoutCompat.addView(revokeBtn);

        AppCompatButton refreshBtn = new AppCompatButton(this);
        refreshBtn.setId(REFRESH);
        refreshBtn.setText("Refresh");
        refreshBtn.setOnClickListener(this);
        layoutCompat.addView(refreshBtn);

        this.setContentView(layoutCompat);

        clientProxy.configureClient("143544088912-i9u0d456571g2vms63r5nho59qm6cvo5.apps.googleusercontent.com", true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case SIGN_IN:
                clientProxy.signIn(new GoogleSignInClientProxy.OnSignInListener() {
                    @Override
                    public void onSuccess(String value) {
                        Log.d("TAG", value);
                    }

                    @Override
                    public void onFailure(int statusCode, String message) {
                        Log.e("TAG", message);
                    }
                });

                break;
            case SIGN_OUT:
clientProxy.signOut(new GoogleSignInClientProxy.OnTaskCompleteListener() {
    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailure(int statusCode, String message) {

    }
});
                break;
            case REVOKE:

                break;
        }
    }
}