package org.example.mentalwellnessfocusapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserProfile {
    private String name;
    private boolean notificationsEnabled;

    // Default profile
    public UserProfile() {
        this("", true);
    }  
     
    // Parameterized constructor
    public UserProfile(String name, boolean notificationsEnabled) {
        this.name = name;
        this.notificationsEnabled = notificationsEnabled;
    }

    // --- DB helpers ---
    // Load profile from DB, or return default if none exists.
    public static UserProfile loadOrDefault() {
        try {
            Connection conn = DatabaseManager.getInstance().getConnection();
            String sql = "SELECT name, notifications_enabled FROM user_profile WHERE id = 1";

            try (Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(sql)) {

                if (rs.next()) {
                    // We only carry over the notifications setting.
                    boolean enabled = rs.getInt("notifications_enabled") != 0;

                    // IMPORTANT:
                    // Start every run with an EMPTY name, so the app never auto-signs in.
                    return new UserProfile("", enabled);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Default: no name, notifications on
        return new UserProfile();
    }

    // Save current profile state to DB.
    public void save() {
        try {
            Connection conn = DatabaseManager.getInstance().getConnection();
            String sql = "INSERT OR REPLACE INTO user_profile " +
                         "(id, name, notifications_enabled) VALUES (1, ?, ?)";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setInt(2, notificationsEnabled ? 1 : 0);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
        e.printStackTrace();
        }
    }

    // --- existing getters/setters ---
    // Get name
    public String getName() {
        return name;
    }

    // Set name (non-blank)
    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name.trim();
        }
    }

    // Get notifications enabled
    public boolean isNotificationsEnabled() {
        return notificationsEnabled;
    }
    // Set notifications enabled
    public void setNotificationsEnabled(boolean notificationsEnabled) {
        this.notificationsEnabled = notificationsEnabled;
    }
}