package com.expensetracker;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class expenses extends AppCompatActivity {

    private LinearLayout container;
    private FirebaseFirestore db;
    private String userId = "kZxoVEYwdMPbkk8coxc3p8FXAlo1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_expenses);

        // bind views
        MaterialToolbar bar = findViewById(R.id.topAppBar);
        bar.setNavigationOnClickListener(v -> finish());

        container = findViewById(R.id.container);

        db = FirebaseFirestore.getInstance();
        loadExpenses();
    }

    private void loadExpenses() {
        db.collection("expenses")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(qs -> {
                    container.removeAllViews();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

                    for (QueryDocumentSnapshot doc : qs) {
                        Expense e = doc.toObject(Expense.class);

                        // create card
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

                        // inner vertical layout
                        LinearLayout inner = new LinearLayout(this);
                        inner.setOrientation(LinearLayout.VERTICAL);
                        inner.setPadding(dp8, dp8, dp8, dp8);

                        // amount
                        TextView amountTv = new TextView(this);
                        amountTv.setTextSize(20);
                        amountTv.setText(String.format("$%.2f", e.getAmount()));
                        inner.addView(amountTv);

                        // category
                        TextView categoryTv = new TextView(this);
                        categoryTv.setTextSize(16);
                        categoryTv.setText("Category: " + e.getCategory());
                        categoryTv.setPadding(0, dp8/2, 0, 0);
                        inner.addView(categoryTv);

                        // timestamp
                        TextView timeTv = new TextView(this);
                        timeTv.setTextSize(12);
                        timeTv.setTextColor(0xFF888888);
                        timeTv.setPadding(0, dp8/2, 0, 0);
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
