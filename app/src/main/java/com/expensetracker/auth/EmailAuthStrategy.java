package com.expensetracker.auth;


import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.expensetracker.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EmailAuthStrategy implements AuthStrategy {

    private final FirebaseAuth auth;
    private final ActivityLoginBinding binding;

    public EmailAuthStrategy(FirebaseAuth auth, ActivityLoginBinding binding) {
        this.auth = auth;
        this.binding = binding;
    }

    @Override
    public void login(Activity activity, String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(activity, "Please enter all the fields", Toast.LENGTH_SHORT).show();
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d("EmailAuth", "signInWithEmail:success - " + user.getEmail());
                        // Navigate to Main screen
                    } else {
                        Toast.makeText(activity, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
