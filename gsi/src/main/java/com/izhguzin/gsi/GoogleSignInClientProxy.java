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

    public interface OnSignInListener {
        void onSuccess(String value);
        void onFailure(int statusCode, String message);
    }

    public interface OnTaskCompleteListener {
        void onSuccess();
        void onFailure(int statusCode, String message);
    }

    private static final String TAG = "GIS PROXY";
    private final AppCompatActivity activity;
    private GoogleSignInClient client;
    private final ActivityResultLauncher<Intent> signInResultHandler;
    private boolean singleUse;

    private OnSignInListener onSignInListener;

    GoogleSignInClientProxy(AppCompatActivity appCompatActivity) {

        this.activity = appCompatActivity;
        signInResultHandler = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
        );
    }

    public void configureClient(String clientId, boolean singleUse) {

        this.singleUse = singleUse;

        GoogleSignInOptions.Builder bld =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN);

        if (singleUse) {
            bld.requestIdToken(clientId);
        }
        else {
            bld.requestServerAuthCode(clientId);
        }

        GoogleSignInOptions gso = bld.build();
        client = GoogleSignIn.getClient(activity, gso);

        Log.d(TAG, "Google Sign In client is ready to use.");
    }

    public void signIn(OnSignInListener listener) {

        onSignInListener = listener;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);

        if (account != null){
            invokeSignInSuccess(account);
            return;
        }

        Intent signInIntent = client.getSignInIntent();
        signInResultHandler.launch(signInIntent);
    }

    public void signOut(OnTaskCompleteListener listener) {

        client.signOut().addOnCompleteListener(activity, task -> handleTaskResult(listener, task));
    }

    private void handleTaskResult(OnTaskCompleteListener listener, Task<Void> task) {
        if (listener != null) {
            if (task.isSuccessful()) {
                listener.onSuccess();
            }
            else {
                ApiException apiException = (ApiException) task.getException();
                if (apiException != null) {
                    listener.onFailure(apiException.getStatusCode(), GoogleSignInStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                }
            }
        }
    }

    public void revokeAccess(OnTaskCompleteListener listener) {

        client.revokeAccess().addOnCompleteListener(activity, task -> handleTaskResult(listener, task));
    }

    private void invokeSignInSuccess(GoogleSignInAccount account) {

        if (onSignInListener != null) {
            if (singleUse) {
                onSignInListener.onSuccess(account.getIdToken());
            }
            else {
                onSignInListener.onSuccess(account.getServerAuthCode());
            }
        }

        onSignInListener = null;
    }

    private void invokeFailure(int errorCode, String message) {

        if (onSignInListener != null) {
            onSignInListener.onFailure(errorCode, message);
        }

        onSignInListener = null;
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            invokeSignInSuccess(account);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            invokeFailure(e.getStatusCode(), GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }
    }
}
