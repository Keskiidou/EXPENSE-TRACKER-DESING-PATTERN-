package com.expensetracker.auth;

import android.app.Activity;

public interface AuthStrategy {
    void login(Activity activity, String email, String password);
}