package org.example.mentalwellnessfocusapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActivityLog {

    // Keep days in order (oldest → newest) for the CURRENT profile
    private final Map<LocalDate, DayActivity> days = new LinkedHashMap<>();

    // DB connection
    private Connection conn;

    // Current profile object (for name)
    private final UserProfile userProfile;

    public ActivityLog(UserProfile userProfile) {
        this.userProfile = userProfile;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            loadFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            conn = null;
        }
    }

    // ---- Profile helpers ----------------------------------------------------

    /** True only if this session has a non-blank profile name. */
    private boolean hasProfileName() {
        String name = userProfile.getName();
        return name != null && !name.isBlank();
    }

    /** Assumes hasProfileName() is true. */
    private String getCurrentProfileName() {
        return userProfile.getName().trim();
    }

    // ---- Load from DB -------------------------------------------------------

    // Load all day activities for the CURRENT profile into memory.
    private void loadFromDatabase() throws SQLException {
        // If no DB or no profile name yet → nothing to load
        if (conn == null || !hasProfileName()) {
            days.clear();
            return;
        }

        days.clear();

        String sql =
            "SELECT date, did_focus, did_breathing, did_journal " +
            "FROM day_activity " +
            "WHERE profile_name = ? " +
            "ORDER BY date ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, getCurrentProfileName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String dateStr = rs.getString("date");
                    LocalDate date = LocalDate.parse(dateStr);

                    DayActivity day = new DayActivity(date);
                    day.setDidFocus(rs.getInt("did_focus") != 0);
                    day.setDidBreathing(rs.getInt("did_breathing") != 0);
                    day.setDidJournal(rs.getInt("did_journal") != 0);

                    days.put(date, day);
                }
            }
        }
    }

    // Called after profile change to show that user's history
    public void reloadForCurrentProfile() {
        try {
            loadFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---- Save to DB ---------------------------------------------------------

    // Get or create a DayActivity in memory
    private DayActivity getOrCreate(LocalDate date) {
        DayActivity day = days.computeIfAbsent(date, DayActivity::new);
        saveDay(day); // ensure row exists in DB if we have a profile
        return day;
    }

    // Save or update a DayActivity in the database for the CURRENT profile.
    private void saveDay(DayActivity day) {
        // If no DB or no profile name → do not write anything
        if (conn == null || !hasProfileName()) {
            return;
        }

        String sql =
            "INSERT OR REPLACE INTO day_activity " +
            "(profile_name, date, did_focus, did_breathing, did_journal) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, getCurrentProfileName());
            ps.setString(2, day.getDate().toString());
            ps.setInt(3, day.isDidFocus() ? 1 : 0);
            ps.setInt(4, day.isDidBreathing() ? 1 : 0);
            ps.setInt(5, day.isDidJournal() ? 1 : 0);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---- Public API ---------------------------------------------------------

    // Record that the current user did this activity today.
    public void recordToday(ActivityType type) {
        // Respect your rule: do NOT track anything if there is no profile name
        if (!hasProfileName()) {
            return;
        }

        LocalDate today = LocalDate.now();
        DayActivity day = getOrCreate(today);

        switch (type) {
            case FOCUS -> day.setDidFocus(true);
            case BREATHING -> day.setDidBreathing(true);
            case JOURNAL -> day.setDidJournal(true);
        }

        saveDay(day);
    }

    /** Last N days (or fewer if not enough data). */
    public List<DayActivity> getLastNDays(int n) {
        List<DayActivity> all = new ArrayList<>(days.values());
        if (all.size() <= n) {
            return all;
        }
        return all.subList(all.size() - n, all.size());
    }

    /** All recorded days for the current profile. */
    public List<DayActivity> getAllDays() {
        return Collections.unmodifiableList(new ArrayList<>(days.values()));
    }
}