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
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.tasks.Task;

public class GoogleSignInClientProxy {

    public interface OnTaskCompleteListener {

        void onComplete(String value, int statusCode, String errorMessage);
    }

    private static final String TAG = "GIS PROXY";
    private final AppCompatActivity activity;
    private GoogleSignInClient client;
    private final ActivityResultLauncher<Intent> signInResultHandler;
    private boolean singleUse;

    private OnTaskCompleteListener onSignInListener;

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

    public void signIn(OnTaskCompleteListener listener) {

        onSignInListener = listener;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(activity);

        if (account != null){
            invokeSignInSuccess(account, true);
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
                listener.onComplete("", CommonStatusCodes.SUCCESS, "");
            }
            else {
                ApiException apiException = (ApiException) task.getException();
                if (apiException != null) {
                    listener.onComplete("", apiException.getStatusCode(), GoogleSignInStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                }
            }
        }
    }

    public void revokeAccess(OnTaskCompleteListener listener) {

        client.revokeAccess().addOnCompleteListener(activity, task -> handleTaskResult(listener, task));
    }

    private void invokeSignInSuccess(GoogleSignInAccount account, boolean fromCache) {

        if (onSignInListener != null) {

            int status = fromCache ? -1 : 0;

            if (singleUse) {
                onSignInListener.onComplete(account.getIdToken(), status, "");
            }
            else {
                onSignInListener.onComplete("", status, "");
            }
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

            // Signed in successfully, show authenticated UI.
            invokeSignInSuccess(account, false);

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            invokeFailure(e.getStatusCode(), GoogleSignInStatusCodes.getStatusCodeString(e.getStatusCode()));
        }
    }
}
