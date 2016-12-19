package com.warriorrat.roommateapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

@IgnoreExtraProperties
public class Chore {

    private String description;
    private boolean completed;
    private String uuid;

    public Chore(String description) {
        this.description = description;
        this.uuid = UUID.randomUUID().toString();
    }

    public Chore(){}

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
}
