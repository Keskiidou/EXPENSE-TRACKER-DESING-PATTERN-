// GoogleAuthStrategy.java
package com.expensetracker.auth;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import com.expensetracker.Login;

import com.expensetracker.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

public class GoogleAuthStrategy implements AuthStrategy {

    private final GoogleSignInClient googleSignInClient;
    private final ActivityLoginBinding binding;
    private static final int RC_SIGN_IN = 9001;

    public GoogleAuthStrategy(GoogleSignInClient googleSignInClient, ActivityLoginBinding binding) {
        this.googleSignInClient = googleSignInClient;
        this.binding = binding;
    }

    @Override
    public void login(Activity activity, String email, String password) {
        // We ignore email/password in Google strategy
        binding.progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
