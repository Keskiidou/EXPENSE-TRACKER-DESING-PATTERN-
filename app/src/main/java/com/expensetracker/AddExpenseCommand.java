package com.expensetracker.commands;

import com.expensetracker.Command;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class AddExpenseCommand implements Command {

    private final FirebaseFirestore db;
    private final Map<String, Object> expenseData;
    private DocumentReference addedDocRef;

    public AddExpenseCommand(FirebaseFirestore db, Map<String, Object> expenseData) {
        this.db = db;
        this.expenseData = expenseData;
    }

    @Override
    public void execute() {
        // add to Firestore
        db.collection("expenses")
                .add(expenseData)
                .addOnSuccessListener(docRef -> {
                    // keep the reference so we can delete it on undo()
                    this.addedDocRef = docRef;
                })
                .addOnFailureListener(e -> {
                    // TODO: show error to user
                });
    }

    @Override
    public void undo() {
        if (addedDocRef != null) {
            addedDocRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        // optionally notify user
                    })
                    .addOnFailureListener(e -> {
                        // TODO: show error
                    });
        }
    }
}
