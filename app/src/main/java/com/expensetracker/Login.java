package com.expensetracker;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.expensetracker.databinding.ActivityLoginBinding;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Apply gradient background based on theme
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            binding.main.setBackgroundResource(R.drawable.gradient_background_dark);
        } else {
            binding.main.setBackgroundResource(R.drawable.gradient_background);
        }
        // Animate elements
        animateViews();

        // Login button click
        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.email.getText().toString();
                String password = binding.password.getText().toString();

                if (!email.isEmpty() && !password.isEmpty()) {
                    // Simulate login (replace with actual authentication logic)
                    Toast.makeText(Login.this, "Logging in...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Sign up prompt click
        binding.signupPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to sign-up activity (implement as needed)
                Toast.makeText(Login.this, "Navigate to Sign Up", Toast.LENGTH_SHORT).show();
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