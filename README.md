# Expense Tracker App Design Pattern (DP)

## Overview
This **Expense Tracker App** leverages design patterns such as **Strategy** and **Chain of Responsibility** to improve functionality, maintainability, and flexibility.

## Features

### Authentication: Firebase
- The app supports **multiple login types** including:
  - Email
  - Google

The **Strategy** pattern is used to handle different login types, providing flexibility to add more login options in the future.

### Track Budget & Spending
Using the **Specification Pattern**, the app allows users to:
- **Check if a spending category is within budget**.
- **Evaluate if a transaction should trigger a warning** (e.g., "approaching limit").
- **Apply multiple rules together** (e.g., "Groceries under $200 AND Total Monthly Spending under $1000").
- **Allow users to define custom budget rules** (e.g., “Alert me if dining out exceeds $100 in a week”).

### Transaction Processing: Chain of Responsibility
The **Chain of Responsibility** pattern is used for processing transactions in a flexible and decoupled manner. The transaction processing follows this workflow:

1. **ValidationHandler**:
   - Checks if the transaction is valid (non-zero amount, valid date, etc.).
   - If invalid → stops the chain and shows an error.

2. **DuplicateCheckHandler** *(optional)*:
   - Checks if this transaction already exists (prevents duplicates).

3. **CategoryAutoAssignHandler**:
   - If the user didn’t choose a category, it tries to auto-assign based on keywords (e.g., "Uber" → Travel).

4. **BudgetLimitCheckHandler**:
   - Checks if the transaction will push the category over its budget.
   - If it does, flags it or notifies the user.

5. **AlertHandler / LoggerHandler**:
   - Logs the transaction or triggers alerts as necessary.

6. **FinalSaveHandler**:
   - Saves the processed transaction to the database.

## Future Improvements
- Add more **authentication strategies** for other login methods.
- Improve the **transaction processing workflow** by adding new handlers for specific use cases.

## Tech Stack
- Android Studio(JAVA)
- Firebase Authentication
- Specification Pattern
- Chain of Responsibility Pattern

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
