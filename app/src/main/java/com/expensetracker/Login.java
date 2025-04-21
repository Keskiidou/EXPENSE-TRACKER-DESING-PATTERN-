package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expensetracker.databinding.ActivityLoginBinding;

import com.google.firebase.auth.FirebaseAuth;


public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();

        // If user is already logged in, skip to income activity
//        if (mAuth.getCurrentUser() != null) {
//            startActivity(new Intent(Login.this, income.class));
//            finish();
//            return;
//        }


        // Apply gradient background based on theme
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            binding.main.setBackgroundResource(R.drawable.gradient_background_dark);
        } else {
            binding.main.setBackgroundResource(R.drawable.gradient_background);
        }

        // Animate elements
        animateViews();
        email = binding.email;
        password = binding.password;
        loginButton = binding.loginButton;
        progressBar = binding.progressBar;
        // Login button click

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString().trim();
                String password = binding.password.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {
                    binding.loginButton.setEnabled(false); // prevent double taps
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener(authResult -> {
                                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Login.this, income.class);
                                startActivity(intent);
                                finish(); // closes login screen
                            })
                            .addOnFailureListener(e -> {
                                binding.loginButton.setEnabled(true);
                                Toast.makeText(Login.this, "Login failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                } else {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }

            }
            auth.signInWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(this, task ->  {

                            if (task.isSuccessful()) {

                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = auth.getCurrentUser();
                                if (user != null) {
                                    String email = user.getEmail();
                                    String uid = user.getUid();
                                    Log.d(TAG, "User logged in: Email = " + email + ", UID = " + uid);
                                }

                            } else {

                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();

                            }

                    });



        });

        // Sign up prompt click (navigate to Register page)
        binding.signupPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);

            }
        });
    }

    private void animateViews() {
        // Initial states
        binding.loginCard.setTranslationY(100f);
        binding.loginCard.setAlpha(0f);
        binding.titleText.setAlpha(0f);
        binding.subtitleText.setAlpha(0f);

        // Animate card
        binding.loginCard.animate()
                .translationY(0f)
                .alpha(1f)
                .setDuration(600)
                .setStartDelay(200)
                .start();

        // Animate title
        binding.titleText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(400)
                .start();

        // Animate subtitle
        binding.subtitleText.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(500)
                .start();
    }
}
