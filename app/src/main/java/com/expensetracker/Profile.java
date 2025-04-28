package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.expensetracker.databinding.ActivityProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.card.MaterialCardView;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Animate UI elements
        setupAnimations();

        // Set up BottomNavigationView
        setupBottomNavigation();

        // Display user info and fetch data
        displayUserInfo();
        fetchAndDisplayUserData();
    }

    private void setupAnimations() {
        // Find cards
        MaterialCardView profileCard = binding.profileCard;
        MaterialCardView statsCard = binding.statsCard;

        // Set initial alpha
        profileCard.setAlpha(0f);
        statsCard.setAlpha(0f);
        binding.bottomNav.setAlpha(0f);

        // Animate fade-in
        profileCard.animate().alpha(1f).setDuration(800).setStartDelay(200).start();
        statsCard.animate().alpha(1f).setDuration(800).setStartDelay(400).start();
        binding.bottomNav.animate().alpha(1f).setDuration(800).setStartDelay(600).start();
    }

    private void setupBottomNavigation() {
        // Highlight Profile item
        binding.bottomNav.setSelectedItemId(R.id.nav_profile);

        // Handle navigation
        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {

                Intent intent = new Intent(Profile.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {

                Intent intent = new Intent(Profile.this, income.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
    }

    private void displayUserInfo() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            binding.nameTextView.setText("Guest");
            binding.emailTextView.setText("No Email");
            return;
        }

        String email = currentUser.getEmail();
        String name = currentUser.getDisplayName();

        if (name == null || name.isEmpty()) {
            if (email != null && email.contains("@")) {
                name = email.substring(0, email.indexOf("@"));
            } else {
                name = "Unknown User";
            }
        }

        binding.nameTextView.setText(name);
        binding.emailTextView.setText(email != null ? email : "No Email");
    }

    private void fetchAndDisplayUserData() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Double income = documentSnapshot.getDouble("income");
                        Double savingsPercent = documentSnapshot.getDouble("savingsPercent");
                        Double spendableMoney = documentSnapshot.getDouble("spendableMoney");
                        Double spendable = documentSnapshot.getDouble("spendable");

                        binding.incomeTextView.setText(income != null ? String.format("$%.2f", income) : "N/A");
                        binding.savingsPercentTextView.setText(savingsPercent != null ? String.format("%.0f%%", savingsPercent) : "N/A");
                        binding.spendableMoneyTextView.setText(spendableMoney != null ? String.format("$%.2f", spendableMoney) : "N/A");
                        binding.spendableMoneyTextView.setText(spendable != null ? String.format("$%.2f", spendable) : "N/A");
                    } else {
                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show();
                        binding.incomeTextView.setText("N/A");
                        binding.savingsPercentTextView.setText("N/A");
                        binding.spendableMoneyTextView.setText("N/A");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to fetch user data: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    binding.incomeTextView.setText("N/A");
                    binding.savingsPercentTextView.setText("N/A");
                    binding.spendableMoneyTextView.setText("N/A");
                });
    }
}