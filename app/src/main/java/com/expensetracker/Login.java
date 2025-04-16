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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private static final String TAG = "LoginActivity";
    TextInputEditText email, password;
    Button loginButton;
    ProgressBar progressBar;
    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        auth = FirebaseAuth.getInstance();
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
        binding.loginButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();

            if (emailInput.isEmpty() || passwordInput.isEmpty()  ) {
                Toast.makeText(Login.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                return;
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

        // Sign up prompt click
        binding.signupPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, Register.class));
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