package com.expensetracker;
import com.expensetracker.commands.AddExpenseCommand;
import com.expensetracker.CommandManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Income extends AppCompatActivity {

    private TextView incomeValue, dailyMoneyText, spendableMoneyText;
    private EditText incomeInput, savingsInput, expenseAmountInput;
    private Button updateBtn, addExpenseBtn;
    private Spinner expenseCategorySpinner;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private DocumentReference userIncomeRef;
    private CommandManager commandManager = new CommandManager();

    private String userId;

    private double totalIncome = 0.0;
    private double savingsPercent = 0.0;
    private double totalExpenses = 0.0;
    private double spendableMoney = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income);

        incomeValue = findViewById(R.id.incomeValue);
        dailyMoneyText = findViewById(R.id.dailyMoneyText);
        spendableMoneyText = findViewById(R.id.spendableMoneyText);
        incomeInput = findViewById(R.id.incomeInput);
        savingsInput = findViewById(R.id.savingsInput);
        expenseAmountInput = findViewById(R.id.expenseAmountInput);
        updateBtn = findViewById(R.id.updateIncomeBtn);
        addExpenseBtn = findViewById(R.id.addExpenseBtn);
        expenseCategorySpinner = findViewById(R.id.expenseCategorySpinner);
        BottomNavigationView bottomNav;
        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Button undoBtn = findViewById(R.id.undoBtn);
        undoBtn.setOnClickListener(v -> {
            commandManager.undoLastCommand();
            // you may want to reload the whole screen or otherwise refresh UI
        });



        bottomNav = findViewById(R.id.bottom_nav);


        bottomNav.setSelectedItemId(R.id.nav_settings);

        bottomNav.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_home) {

                Intent intent = new Intent(Income.this, Home.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            } else if (item.getItemId() == R.id.nav_settings) {
                return true;
            } else if (item.getItemId() == R.id.nav_profile) {

                Intent intent = new Intent(Income.this, Profile.class);
                startActivity(intent);
                overridePendingTransition(0,0);
                return true;
            }
            return false;
        });
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        userId = currentUser.getUid();
        userIncomeRef = db.collection("users").document(userId);

        // Setup Spinner for expense categories
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.expense_categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        expenseCategorySpinner.setAdapter(adapter);

        loadIncomeAndSavings();

        updateBtn.setOnClickListener(view -> {
            String incomeStr = incomeInput.getText().toString().trim();
            String savingsStr = savingsInput.getText().toString().trim();

            if (!incomeStr.isEmpty() && !savingsStr.isEmpty()) {
                try {
                    totalIncome = Double.parseDouble(incomeStr);
                    savingsPercent = Double.parseDouble(savingsStr);

                    double savingsAmount = totalIncome * (savingsPercent / 100.0);
                    spendableMoney = totalIncome - savingsAmount;
                    double dailyMoney = spendableMoney / 30.0;

                    incomeValue.setText("$" + String.format("%.2f", totalIncome));
                    dailyMoneyText.setText("Daily Money: $" + String.format("%.2f", dailyMoney));
                    spendableMoneyText.setText("Spendable Money: $" + String.format("%.2f", spendableMoney));

                    Map<String, Object> incomeData = new HashMap<>();
                    incomeData.put("income", totalIncome);
                    incomeData.put("savingsPercent", savingsPercent);
                    incomeData.put("userId", userId);

                    userIncomeRef.set(incomeData)
                            .addOnSuccessListener(aVoid ->
                                    Toast.makeText(Income.this, "Data saved", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e ->
                                    Toast.makeText(Income.this, "Failed to save data", Toast.LENGTH_SHORT).show());

                } catch (NumberFormatException e) {
                    Toast.makeText(Income.this, "Invalid input", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Income.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
            }
        });

        addExpenseBtn.setOnClickListener(view -> {
            String amountStr = expenseAmountInput.getText().toString().trim();
            String category  = expenseCategorySpinner.getSelectedItem().toString();

            if (amountStr.isEmpty()) {
                Toast.makeText(this, "Enter all expense fields", Toast.LENGTH_SHORT).show();
                return;
            }

            double expenseAmount;
            try {
                expenseAmount = Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build the Firestore data
            Map<String, Object> expenseData = new HashMap<>();
            expenseData.put("userId", userId);
            expenseData.put("amount", expenseAmount);
            expenseData.put("category", category);
            expenseData.put("timestamp", System.currentTimeMillis());

            // Create & execute the AddExpenseCommand
            AddExpenseCommand cmd = new AddExpenseCommand(db, expenseData);
            commandManager.executeCommand(cmd);

            // Update spendable money locally
            totalExpenses     += expenseAmount;
            spendableMoney    -= expenseAmount;
            spendableMoneyText.setText("Spendable Money: $" + String.format("%.2f", spendableMoney));

            // Persist spendable money
            userIncomeRef.update("spendableMoney", spendableMoney);

            // Show a Snackbar with “Undo”
            Snackbar.make(spendableMoneyText, "Expense added", Snackbar.LENGTH_LONG)
                    .setAction("UNDO", v -> {
                        commandManager.undoLastCommand();
                        // revert spendable money UI + Firestore
                        totalExpenses  -= expenseAmount;
                        spendableMoney += expenseAmount;
                        spendableMoneyText.setText("Spendable Money: $" + String.format("%.2f", spendableMoney));
                        userIncomeRef.update("spendableMoney", spendableMoney);
                    })
                    .show();

            // clear input
            expenseAmountInput.setText("");
        });


    }

    private void loadIncomeAndSavings() {
        userIncomeRef.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        totalIncome = documentSnapshot.contains("income") ? documentSnapshot.getDouble("income") : 0.0;
                        savingsPercent = documentSnapshot.contains("savingsPercent") ? documentSnapshot.getDouble("savingsPercent") : 0.0;
                        spendableMoney = documentSnapshot.contains("spendableMoney") ? documentSnapshot.getDouble("spendableMoney") : 0.0;

                        incomeValue.setText("$" + String.format("%.2f", totalIncome));
                        incomeInput.setText(String.format("%.2f", totalIncome));
                        savingsInput.setText(String.format("%.0f", savingsPercent));

                        double savingsAmount = totalIncome * (savingsPercent / 100.0);


                        // Ensure spendableMoney is calculated if not fetched
                        double dailyMoney = spendableMoney / 30.0;
                        dailyMoneyText.setText("Daily Money: $" + String.format("%.2f", dailyMoney));
                        spendableMoneyText.setText("Spendable Money: $" + String.format("%.2f", spendableMoney));
                    } else {
                        incomeValue.setText("$0.00");
                        dailyMoneyText.setText("Daily Money: $0.00");
                        spendableMoneyText.setText("Spendable Money: $0.00");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(Income.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                });
    }

}
