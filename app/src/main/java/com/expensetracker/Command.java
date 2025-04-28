package com.expensetracker;

public interface Command {
    void execute();
    void undo();
}
