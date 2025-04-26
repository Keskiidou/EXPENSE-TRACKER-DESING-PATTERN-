package com.expensetracker.auth;

import com.google.firebase.auth.FirebaseAuth;

public class EmailLoginStrategy implements LoginStrategy {
    private FirebaseAuth mAuth;

    public EmailLoginStrategy(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    @Override
    public void login(String email, String password, LoginCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    callback.onSuccess();
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }
}
