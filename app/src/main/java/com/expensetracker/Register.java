package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.expensetracker.databinding.ActivityRegisterBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    TextInputEditText email, password, repeat_password;
    Button registerButton;
    ProgressBar progressBar;
    FirebaseAuth auth;

    private static final String TAG = "RegisterActivity"; // Add TAG for logging

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();

        // Initialize the binding object before setContentView
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
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

        // Apply window insets for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI elements
        email = binding.email;
        password = binding.password;
        repeat_password = binding.repeatPassword;
        registerButton = binding.registerButton;
        progressBar = binding.progressBar;

        // Register button click listener
        registerButton.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String emailInput = email.getText().toString();
            String passwordInput = password.getText().toString();
            String repeatPasswordInput = repeat_password.getText().toString();

            if (emailInput.isEmpty() || passwordInput.isEmpty() || repeatPasswordInput.isEmpty()) {
                Toast.makeText(Register.this, "Please enter all the fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!passwordInput.equals(repeatPasswordInput)) {
                Toast.makeText(Register.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // Register the user
            auth.createUserWithEmailAndPassword(emailInput, passwordInput)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {

                            Log.d(TAG, "createUserWithEmail:success");
                            startActivity(new Intent(Register.this, Login.class));

                        } else {
                            Exception exception = task.getException();
                            Log.e(TAG, "Authentication failed: ", exception);
                            Toast.makeText(Register.this, "Authentication failed: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }



    private void animateViews() {
        // Initial states
        binding.registerCard.setTranslationY(100f);
        binding.registerCard.setAlpha(0f);
        binding.titleText.setAlpha(0f);
        binding.subtitleText.setAlpha(0f);

        // Animate card
        binding.registerCard.animate()
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
