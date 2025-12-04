package org.example.mentalwellnessfocusapp;

public class UserProfile {
    private String name;
    private boolean notificationsEnabled;

    public UserProfile() {
        // sensible defaults
        this.name = "Friend";
        this.notificationsEnabled = true;
    }

    public UserProfile(String name, boolean notificationsEnabled) {
        this.name = name;
        this.notificationsEnabled = notificationsEnabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
    }

    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }

    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}
