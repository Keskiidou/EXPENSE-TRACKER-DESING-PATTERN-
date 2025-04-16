// AuthContext.java
package com.expensetracker.auth;

import android.app.Activity;

public class AuthContext {
    private AuthStrategy strategy;

    public void setStrategy(AuthStrategy strategy) {
        this.strategy = strategy;
    }

    public void authenticate(Activity activity, String email, String password) {
        if (strategy != null) {
            strategy.login(activity, email, password);
        }
    }
}
