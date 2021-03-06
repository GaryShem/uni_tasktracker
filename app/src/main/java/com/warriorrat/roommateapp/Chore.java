package com.warriorrat.roommateapp;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.UUID;

@IgnoreExtraProperties
class Chore implements Comparable<Chore> {

    private String description;
    private boolean completed;
    private String uuid;
    private Long time;

    public Chore(String description) {
        this.description = description;
        this.uuid = UUID.randomUUID().toString();
        time = System.currentTimeMillis();
    }

    public Chore() {
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

    @Override
    public int compareTo(Chore chore) {
        if (completed == chore.isCompleted()) {
            return -time.compareTo(chore.getTime());
        }
        if (completed) {
            return 1;
        } else {
            return -1;
        }
    }
}
