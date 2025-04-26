package com.expensetracker.auth;

public interface LoginCallback {
    void onSuccess();
    void onFailure(String errorMessage);
}