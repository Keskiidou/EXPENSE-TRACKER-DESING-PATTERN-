package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expensetracker.auth.EmailLoginStrategy;
import com.expensetracker.auth.GoogleLoginStrategy;
import com.expensetracker.auth.LoginCallback;
import com.expensetracker.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;
    private GoogleLoginStrategy googleLoginStrategy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        GoogleSignInClient googleSignInClient = setupGoogleSignInClient();

        googleLoginStrategy = new GoogleLoginStrategy(this, googleSignInClient, mAuth);

        applyThemeBackground();
        animateViews();

        binding.loginButton.setOnClickListener(v -> attemptLogin());
        binding.googleLoginButton.setOnClickListener(v -> attemptGoogleLogin());
        binding.signupPrompt.setOnClickListener(v -> navigateToRegisterActivity());
    }

    private GoogleSignInClient setupGoogleSignInClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return GoogleSignIn.getClient(this, gso);
    }

    private void attemptLogin() {
        String email = binding.email.getText().toString().trim();
        String password = binding.password.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.loginButton.setEnabled(false);

        EmailLoginStrategy loginStrategy = new EmailLoginStrategy(mAuth);
        loginStrategy.login(email, password, new LoginCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();
                navigateToIncomeActivity();
            }

            @Override
            public void onFailure(String errorMessage) {
                binding.loginButton.setEnabled(true);
                Toast.makeText(Login.this, "Login failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void attemptGoogleLogin() {
        if (googleLoginStrategy == null) {
            Toast.makeText(this, "Google login is not set up", Toast.LENGTH_SHORT).show();
            return;
        }

        googleLoginStrategy.login("", "", new LoginCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(Login.this, "Google login successful", Toast.LENGTH_SHORT).show();
                navigateToIncomeActivity();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(Login.this, "Google login failed: " + errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (googleLoginStrategy != null) {
            googleLoginStrategy.handleActivityResult(requestCode, resultCode, data);
        } else {
            Toast.makeText(this, "Google login strategy unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToIncomeActivity() {
        startActivity(new Intent(this, Home.class));
        finish();
    }

    private void navigateToRegisterActivity() {
        startActivity(new Intent(this, Register.class));
    }

    private void applyThemeBackground() {
        int nightModeFlags = getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            binding.main.setBackgroundResource(R.drawable.gradient_background_dark);
        } else {
            binding.main.setBackgroundResource(R.drawable.gradient_background);
        }
    }

    private void animateViews() {
        binding.loginCard.setTranslationY(100f);
        binding.loginCard.setAlpha(0f);
        binding.titleText.setAlpha(0f);
        binding.subtitleText.setAlpha(0f);

        binding.loginCard.animate().translationY(0f).alpha(1f).setDuration(600).setStartDelay(200).start();
        binding.titleText.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(400).start();
        binding.subtitleText.animate().alpha(1f).translationY(0f).setDuration(400).setStartDelay(500).start();
    }
}
