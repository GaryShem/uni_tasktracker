package com.warriorrat.roommateapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

@IgnoreExtraProperties
class Bill implements Comparable<Bill> {

    private String description;
    private boolean completed;
    private double amount;
    private String uuid;
    private Long time;

    public Bill(String description, double amount) {
        this.description = description;
        this.uuid = UUID.randomUUID().toString();
        this.amount = amount;
        time = System.currentTimeMillis();
    }

    public Bill() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void updateTime() {
        time = System.currentTimeMillis();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public int compareTo(Bill bill) {
        if (completed == bill.isCompleted()) {
            return -time.compareTo(bill.getTime());
        }
        if (completed) {
            return 1;
        } else {
            return -1;
        }
    }
}
