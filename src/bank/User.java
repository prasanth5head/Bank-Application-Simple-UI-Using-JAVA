package bank;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String username;
    private String pin;
    private double balance;
    private List<Transaction> transactionHistory;

    public User(String username, String pin, double initialBalance) {
        this.username = username;
        this.pin = pin;
        this.balance = initialBalance;
        this.transactionHistory = new ArrayList<>();
        
        if (initialBalance > 0) {
            addTransaction(new Transaction("Initial Deposit", initialBalance));
        }
    }

    public String getUsername() { return username; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            addTransaction(new Transaction("Deposit", amount));
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            addTransaction(new Transaction("Withdrawal", -amount));
            return true;
        }
        return false;
    }
    
    public boolean transferTo(User recipient, double amount) {
        if (amount > 0 && this.balance >= amount) {
            this.balance -= amount;
            addTransaction(new Transaction("Transfer to " + recipient.getUsername(), -amount));
            recipient.receiveTransfer(this.username, amount);
            return true;
        }
        return false;
    }
    
    private void receiveTransfer(String sender, double amount) {
        this.balance += amount;
        addTransaction(new Transaction("Transfer from " + sender, amount));
    }

    private void addTransaction(Transaction t) {
        this.transactionHistory.add(t);
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }
}
