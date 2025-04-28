package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        bottomNav = findViewById(R.id.bottom_nav);

        // Fetch current user details and recent expenses
        fetchUserData();
        fetchRecentExpenses();

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                Intent intent = new Intent(Home.this, income.class);
                startActivity(intent);
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(Home.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void fetchUserData() {
        if (mAuth.getCurrentUser() == null) {
            Log.e("HomeActivity", "User is not logged in");
            return;
        }

        // Get the current user
        String userId = mAuth.getCurrentUser().getUid();

        // Fetch data from the "users" collection
        db.collection("users").document(userId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            double income = document.getDouble("income");
                            // Update the UI with the balance
                            TextView balanceValue = findViewById(R.id.balance_value);
                            balanceValue.setText("$" + income);
                        }
                    } else {
                        Log.e("HomeActivity", "Failed to fetch user data", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeActivity", "Error fetching user data", e);
                });
    }

    private void fetchRecentExpenses() {
        if (mAuth.getCurrentUser() == null) {
            Log.e("HomeActivity", "User is not logged in");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        db.collection("expenses")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().getDocuments().isEmpty()) {
                            // Handle case with no recent expenses
                            Log.d("HomeActivity", "No recent expenses found for user: " + userId);
                            TextView expenseDescription = findViewById(R.id.expense_category);
                            TextView expenseAmount = findViewById(R.id.expense_amount);
                            expenseDescription.setText("No recent expenses");
                            expenseAmount.setText("$0.00");
                        } else {
                            DocumentSnapshot document = task.getResult().getDocuments().get(0);
                            double amount = document.getDouble("amount");
                            String category = document.getString("category");

                            Log.d("HomeActivity", "Recent expense: " + amount + " | Category: " + category);

                            TextView expenseDescription = findViewById(R.id.expense_category);
                            TextView expenseAmount = findViewById(R.id.expense_amount);

                            // Make sure the UI is updated properly
                            expenseDescription.setText(category);
                            expenseAmount.setText("$" + amount);
                        }
                    } else {
                        Log.e("HomeActivity", "Error fetching expenses", task.getException());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("HomeActivity", "Error fetching recent expenses", e);
                });
    }
}
