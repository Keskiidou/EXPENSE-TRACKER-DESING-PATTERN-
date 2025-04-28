package com.expensetracker;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Expenses extends AppCompatActivity {

    private LinearLayout container;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String userId;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expenses);

        // Bind views
        MaterialToolbar bar = findViewById(R.id.topAppBar);
        bar.setNavigationOnClickListener(v -> finish());

        container = findViewById(R.id.container);
        bottomNav = findViewById(R.id.bottom_nav); // <<< BOTTOM NAVIGATION INIT

        // Highlight expense icon
        bottomNav.setSelectedItemId(R.id.nav_expense);

        // Bottom navigation listener
        bottomNav.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(Expenses.this, Home.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                startActivity(new Intent(Expenses.this, Income.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(Expenses.this, Profile.class));
                overridePendingTransition(0, 0);
                return true;
            } else if (item.getItemId() == R.id.nav_expense) {
                // Already here
                return true;
            }
            return false;
        });

        // Firebase init
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadExpenses();
        } else {
            // User not logged in
            // TODO: show login screen
        }
    }

    private void loadExpenses() {
        db.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(qs -> {
                    container.removeAllViews();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    if (qs.isEmpty()) {
                        // If no expenses
                        TextView emptyText = new TextView(this);
                        emptyText.setText("No expenses found.");
                        emptyText.setTextSize(18);
                        emptyText.setPadding(0, 50, 0, 0);
                        emptyText.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
                        container.addView(emptyText);
                        return;
                    }

                    for (QueryDocumentSnapshot doc : qs) {
                        Expense e = doc.toObject(Expense.class);

                        // Create card
                        MaterialCardView card = new MaterialCardView(this);
                        int dp8 = (int)(8 * getResources().getDisplayMetrics().density);
                        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        cardParams.setMargins(0, dp8, 0, dp8);
                        card.setLayoutParams(cardParams);
                        card.setRadius(dp8);
                        card.setCardElevation(dp8);
                        card.setUseCompatPadding(true);

                        // Inner vertical layout
                        LinearLayout inner = new LinearLayout(this);
                        inner.setOrientation(LinearLayout.VERTICAL);
                        inner.setPadding(dp8, dp8, dp8, dp8);

                        // Amount
                        TextView amountTv = new TextView(this);
                        amountTv.setTextSize(20);
                        amountTv.setText(String.format("$%.2f", e.getAmount()));
                        inner.addView(amountTv);

                        // Category
                        TextView categoryTv = new TextView(this);
                        categoryTv.setTextSize(16);
                        categoryTv.setText("Category: " + e.getCategory());
                        categoryTv.setPadding(0, dp8 / 2, 0, 0);
                        inner.addView(categoryTv);

                        // Timestamp
                        TextView timeTv = new TextView(this);
                        timeTv.setTextSize(12);
                        timeTv.setTextColor(0xFF888888);
                        timeTv.setPadding(0, dp8 / 2, 0, 0);
                        timeTv.setText(sdf.format(new Date(e.getTimestamp())));
                        inner.addView(timeTv);

                        card.addView(inner);
                        container.addView(card);
                    }
                })
                .addOnFailureListener(e -> {
                    // TODO: show error
                });
    }

    // Firestore model
    public static class Expense {
        private double amount;
        private String category;
        private long timestamp;
        public Expense() {}
        public double getAmount()   { return amount; }
        public String getCategory() { return category; }
        public long getTimestamp()  { return timestamp; }
    }
}
