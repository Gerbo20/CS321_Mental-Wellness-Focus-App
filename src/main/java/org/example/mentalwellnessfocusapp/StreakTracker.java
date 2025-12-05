package org.example.mentalwellnessfocusapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;

public class StreakTracker {

    private int streak = 0;
    private LocalDate lastCompleted = null;

    private Connection conn;
    private final UserProfile userProfile;

    public StreakTracker(UserProfile userProfile) {
        this.userProfile = userProfile;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            loadFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            conn = null;
        }
    }

    private String getCurrentProfileName() {
        String name = userProfile.getName();
        if (name == null || name.isBlank()) {
            return null;
        }
        return name.trim();
    }

    // Load streak state for current profile
    private void loadFromDatabase() throws SQLException {
        if (conn == null) return;

        String profile = getCurrentProfileName();
        if (profile == null) {
            // No user → no streak state to load
            return;
        }

        String sql = "SELECT streak, last_completed FROM streak_state WHERE profile_name = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    streak = rs.getInt("streak");
                    String last = rs.getString("last_completed");
                    if (last != null) {
                        lastCompleted = LocalDate.parse(last);
                    }
                }
            }
        }
    }

    // Called after profile change if you want to reload that user's streak
    public void reloadForCurrentProfile() {
        streak = 0;
        lastCompleted = null;
        try {
            loadFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Save streak state for current profile
    private void saveToDatabase() {
        if (conn == null) return;

        String profile = getCurrentProfileName();
        if (profile == null) {
            // No user → don't persist streak
            return;
        }

        String sql = "INSERT OR REPLACE INTO streak_state " +
                     "(profile_name, streak, last_completed) VALUES (?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, profile);
            ps.setInt(2, streak);
            if (lastCompleted != null) {
                ps.setString(3, lastCompleted.toString());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Call this when the user completes their wellness task for the day
    public void completeToday() {

        String profile = getCurrentProfileName();
        if (profile == null) {
            // No profile → do not track streaks
            return;
        }
        
        LocalDate today = LocalDate.now();

        if (lastCompleted == null) {
            streak = 1;
        } else if (lastCompleted.plusDays(1).equals(today)) {
            streak++;
        } else if (lastCompleted.equals(today)) {
            // already completed today; do nothing
        } else {
            streak = 1;
        }

        lastCompleted = today;
        saveToDatabase();
    }

    public boolean hasMissedDay() {
        if (lastCompleted == null) return false;
        LocalDate today = LocalDate.now();
        return lastCompleted.plusDays(1).isBefore(today);
    }

    public int getStreak() {
        return streak;
    }
}