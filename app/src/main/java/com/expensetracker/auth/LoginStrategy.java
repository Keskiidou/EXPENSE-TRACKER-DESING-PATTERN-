package com.expensetracker.auth;

public interface LoginStrategy {
    void login(String username, String password, LoginCallback callback);
}