package com.izhguzin.testactivity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.izhguzin.gsi.GoogleSignInClientProxy;
import com.izhguzin.gsi.GsiAppCompatActivity;

public class TestActivivty extends GsiAppCompatActivity implements View.OnClickListener {


    private static final int SIGN_IN = 1;
    private static final int SIGN_OUT = 2;
    private static final int REVOKE = 3;
    private static final int REFRESH = 4;

    private static final int REQUEST_CODE_GOOGLE_SIGN_IN = 6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Integer a = new Integer(1);
        Integer b = new Integer(1);

        Log.d("TAG", String.valueOf(a == b));
        Log.d("TAG", String.valueOf(a.equals(b)));

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

        //clientProxy.configureClient("143544088912-i9u0d456571g2vms63r5nho59qm6cvo5.apps.googleusercontent.com", true);

    }

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case SIGN_IN:
                    signIn();
                    break;
                case SIGN_OUT:

                    break;
                case REVOKE:

                    break;
            }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GOOGLE_SIGN_IN) {
                try {
                    SignInCredential credential = Identity.getSignInClient(this).getSignInCredentialFromIntent(data);
                    // Signed in successfully - show authenticated UI
                    //updateUI(credential);
                } catch (ApiException e) {
                    // The ApiException status code indicates the detailed failure reason.
                }
            }
        }
    }

    private void signIn() {
        GetSignInIntentRequest request =
                GetSignInIntentRequest.builder()
                        .setServerClientId("143544088912-i9u0d456571g2vms63r5nho59qm6cvo5.apps.googleusercontent.com")
                        .build();

        Identity.getSignInClient(this)
                .getSignInIntent(request)
                .addOnSuccessListener(
                        result -> {
                            try {
                                startIntentSenderForResult(
                                        result.getIntentSender(),
                                        REQUEST_CODE_GOOGLE_SIGN_IN,
                                        /* fillInIntent= */ null,
                                        /* flagsMask= */ 0,
                                        /* flagsValue= */ 0,
                                        /* extraFlags= */ 0,
                                        /* options= */ null);
                            } catch (IntentSender.SendIntentException e) {
                                Log.e("TAG", "Google Sign-in failed");
                            }
                        })
                .addOnFailureListener(
                        e -> {
                            Log.e("TAG", "Google Sign-in failed", e);
                        });
    }
}
