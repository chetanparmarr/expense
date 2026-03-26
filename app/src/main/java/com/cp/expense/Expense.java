package com.cp.expense;

public class Expense {
    private long id;
    private double amount;
    private String description;
    private String date; // stored as yyyy-MM-dd
    private long timestamp;

    public Expense() {}

    public Expense(long id, double amount, String description, String date, long timestamp) {
        this.id = id;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.timestamp = timestamp;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
