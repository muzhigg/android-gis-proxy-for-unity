package com.izhguzin.gsi;

import android.content.Intent;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInClientProxy {

    public interface OnTaskCompleteListener {

        void onComplete(String code, int statusCode, String errorMessage);
    }

    private static volatile GoogleSignInClientProxy instance;

    private static final String TAG = "GSI PROXY";
    private final AppCompatActivity activity;
    private GoogleSignInClient client;
    private final ActivityResultLauncher<Intent> signInResultHandler;
    private OnTaskCompleteListener onSignInListener;

    public static GoogleSignInClientProxy getInstance() {
        return instance;
    }

    public static void init(AppCompatActivity appCompatActivity) {

        GoogleSignInClientProxy localInstance = instance;

        if (localInstance == null) {
            synchronized (GoogleSignInClientProxy.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new GoogleSignInClientProxy(appCompatActivity);
                }
            }
        }
        else {
            Log.e(TAG, "Google Sign In Client Proxy already initialized!");
        }
    }

    GoogleSignInClientProxy(AppCompatActivity appCompatActivity) {

        this.activity = appCompatActivity;
        signInResultHandler = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
        );

        Log.d(TAG, "GoogleSignInClientProxy is initialized.");
    }

    public void initOptions(GoogleSignInOptions options) {

        client = GoogleSignIn.getClient(activity, options);
        Log.d(TAG, "Google Sign In client is ready to use.");
    }

    public void signIn(OnTaskCompleteListener listener) {

        onSignInListener = listener;

        Intent signInIntent = client.getSignInIntent();
        signInResultHandler.launch(signInIntent);
    }

    public void signOut() {

        client.signOut();
    }

    private void invokeSignInSuccess(GoogleSignInAccount account) {

        if (onSignInListener != null) {

            onSignInListener.onComplete(account.getServerAuthCode(), 1, "");
        }

        onSignInListener = null;
    }

    private void invokeFailure(int errorCode, String message) {

        if (onSignInListener != null) {
            onSignInListener.onComplete("", errorCode, message);
        }

        onSignInListener = null;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            invokeSignInSuccess(account);
        } catch (ApiException e) {
            invokeFailure(e.getStatusCode(), GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }
    }
}
