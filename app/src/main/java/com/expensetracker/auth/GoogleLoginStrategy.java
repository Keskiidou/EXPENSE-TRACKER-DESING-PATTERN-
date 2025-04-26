package com.expensetracker.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class GoogleLoginStrategy implements LoginStrategy {

    private static final String TAG = "GoogleLoginStrategy";
    private static final int RC_SIGN_IN = 9001;

    private final Activity activity;
    private final GoogleSignInClient googleSignInClient;
    private final FirebaseAuth mAuth;

    private LoginCallback loginCallback;

    public GoogleLoginStrategy(Activity activity, GoogleSignInClient googleSignInClient, FirebaseAuth mAuth) {
        this.activity = activity;
        this.googleSignInClient = googleSignInClient;
        this.mAuth = mAuth;
    }

    @Override
    public void login(String email, String password, LoginCallback callback) {
        this.loginCallback = callback;
        googleSignInClient.signOut().addOnCompleteListener(task -> {
            Intent signInIntent = googleSignInClient.getSignInIntent();
            activity.startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }


    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null && account.getIdToken() != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Log.e(TAG, "GoogleSignInAccount is null or missing ID token.");
                    if (loginCallback != null) {
                        loginCallback.onFailure("Google sign-in failed: No ID token.");
                    }
                }
            } catch (ApiException e) {
                Log.e(TAG, "Google sign-in failed", e);
                if (loginCallback != null) {
                    loginCallback.onFailure("Google sign-in failed: " + e.getMessage());
                }
            }
        }
    }

    private void firebaseAuthWithGoogle(@NonNull GoogleSignInAccount acct) {
        String idToken = acct.getIdToken();
        if (idToken == null) {
            Log.e(TAG, "ID token is null");
            if (loginCallback != null) {
                loginCallback.onFailure("Google ID token is null");
            }
            return;
        }

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    if (loginCallback != null) loginCallback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Firebase authentication failed", e);
                    if (loginCallback != null) {
                        loginCallback.onFailure("Firebase auth failed: " + e.getMessage());
                    }
                });
    }
}
