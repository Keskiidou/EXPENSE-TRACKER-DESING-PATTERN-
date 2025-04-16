package com.expensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.expensetracker.auth.AuthContext;
import com.expensetracker.auth.EmailAuthStrategy;
import com.expensetracker.auth.GoogleAuthStrategy;
import com.expensetracker.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private AuthContext authContext;
    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // Theme background
        int nightModeFlags = getResources().getConfiguration().uiMode & android.content.res.Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES) {
            binding.main.setBackgroundResource(R.drawable.gradient_background_dark);
        } else {
            binding.main.setBackgroundResource(R.drawable.gradient_background);
        }

        animateViews();

        // AuthContext setup
        authContext = new AuthContext();

        // Email login button
        binding.loginButton.setOnClickListener(view -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            authContext.setStrategy(new EmailAuthStrategy(auth, binding));
            authContext.authenticate(this, email, password);
        });

        // Sign-up prompt
        binding.signupPrompt.setOnClickListener(v -> startActivity(new Intent(Login.this, Register.class)));

        // Google Sign-In setup
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Google Sign-In button
        binding.googleSignInButton.setOnClickListener(view -> {
            authContext.setStrategy(new GoogleAuthStrategy(googleSignInClient, binding));
            authContext.authenticate(this, "", ""); // email/password not needed
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                binding.progressBar.setVisibility(View.GONE);
                Log.w(TAG, "Google sign-in failed: " + e.getStatusCode() + ", " + e.getMessage());
                Toast.makeText(this, "Google sign-in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        Log.d(TAG, "signInWithGoogle:success - " + user.getEmail());
                        // Navigate to Main screen
                    } else {
                        Toast.makeText(Login.this, "Google authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
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
