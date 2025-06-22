package com.s23010615.habitease.models;

import androidx.annotation.NonNull;

public class Habit {
    private int id;
    private final String name;
    private final String time;         // Scheduled time (e.g., "08:00 AM")
    private final boolean homeOnly;    // true if it should notify only at Home
    private boolean isCompleted;       // for today
    private int streakCount;           // Track habit streak

    // Primary constructor
    public Habit(int id, String name, String time, boolean homeOnly, boolean isCompleted, int streakCount) {
        this.id = id;
        this.name = name;
        this.time = time;
        this.homeOnly = homeOnly;
        this.isCompleted = isCompleted;
        this.streakCount = streakCount;
    }

    //  constructor for new habits
    public Habit(String name, String time, boolean homeOnly) {
        this(0, name, time, homeOnly, false,0);
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getTime() { return time; }
    public boolean isHomeOnly() { return homeOnly; }
    public boolean isCompleted() { return isCompleted; }
    public int getStreakCount() { return streakCount; }

    // Setters (only for mutable fields)
    public void setId(int id) { this.id = id; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
    public void setStreakCount(int streakCount) { this.streakCount = streakCount; }

    // Utility methods
    @NonNull
    @Override
    public String toString() {
        return "Habit{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", time='" + time + '\'' +
                ", homeOnly=" + homeOnly +
                ", isCompleted=" + isCompleted +
                ", streak=" + streakCount +
                '}';
    }

    // Builder pattern for initialization
    public static class Builder {
        private String name;
        private String time;
        private boolean homeOnly;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setTime(String time) {
            this.time = time;
            return this;
        }

        public Builder setHomeOnly(boolean homeOnly) {
            this.homeOnly = homeOnly;
            return this;
        }

        public Habit build() {
            return new Habit(name, time, homeOnly);
        }
    }
}