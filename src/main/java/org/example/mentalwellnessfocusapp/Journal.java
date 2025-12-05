package org.example.mentalwellnessfocusapp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Journal {

    // In-memory list for the GUI
    private final List<JournalEntry> entries = new ArrayList<>();
    // Parallel list mapping entry index -> DB id
    private final List<Long> entryIds = new ArrayList<>();

    // DB connection
    private Connection conn;   // may be null if DB fails

    // Current profile (who owns these entries)
    private final UserProfile userProfile;

    public Journal(UserProfile userProfile) {
        this.userProfile = userProfile;
        try {
            conn = DatabaseManager.getInstance().getConnection();
            loadFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            conn = null; // fall back to pure in-memory
        }
    }

    // Helper: current profile key
    private String getCurrentProfileName() {
        String name = userProfile.getName();
        return (name == null) ? "" : name.trim();
    }

    // Load all entries for the CURRENT profile from the database into memory.
    private void loadFromDatabase() throws SQLException {
        if (conn == null) return;

        String profile = getCurrentProfileName();
        if (profile == null) {
            // No profile → show empty journal
            entries.clear();
            entryIds.clear();
            return;
        }
        
        entries.clear();
        entryIds.clear();

        String sql = "SELECT id, title, mood, entry_text, date " +
                     "FROM journal_entries " +
                     "WHERE profile_name = ? " +
                     "ORDER BY date ASC, id ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, getCurrentProfileName());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String title = rs.getString("title");
                    String mood = rs.getString("mood");
                    String text = rs.getString("entry_text");
                    String dateStr = rs.getString("date");
                    LocalDate date = LocalDate.parse(dateStr);

                    JournalEntry entry = new JournalEntry(title, mood, text, date);
                    entries.add(entry);
                    entryIds.add(id);
                }
            }
        }
    }

    // Public method to reload entries when user changes profile
    public void reloadForCurrentProfile() {
        try {
            loadFromDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add a new journal entry.
    public boolean addEntry(String title, String mood, String text) {
        // Do not allow saving for anonymous user
        String profile = getCurrentProfileName();
        if (profile == null) {
            return false;
        }

        // Do not allow empty entries
        if ((title == null || title.isBlank()) &&
            (text == null || text.isBlank())) {
            return false;
        }

        String safeTitle = (title == null) ? "" : title.trim();
        String safeMood  = (mood == null)  ? "" : mood.trim();
        String safeText  = (text == null)  ? "" : text.trim();
        LocalDate date   = LocalDate.now();
        //String profile   = getCurrentProfileName();

        JournalEntry entry = new JournalEntry(safeTitle, safeMood, safeText, date);
        entries.add(entry);

        if (conn != null) {
            String sql = "INSERT INTO journal_entries(profile_name, title, mood, entry_text, date) " +
                         "VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS)) {

                ps.setString(1, profile);
                ps.setString(2, safeTitle);
                ps.setString(3, safeMood);
                ps.setString(4, safeText);
                ps.setString(5, date.toString());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        long id = keys.getLong(1);
                        entryIds.add(id);
                    } else {
                        entryIds.add(-1L);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                entryIds.add(-1L);
            }
        } else {
            entryIds.add(-1L);
        }

        return true;
    }

    // Delete by index for current profile
    public boolean deleteEntry(int index) {
        if (index < 0 || index >= entries.size()) {
            return false;
        }

        entries.remove(index);
        long id = entryIds.remove(index);

        if (conn != null && id > 0) {
            String sql = "DELETE FROM journal_entries WHERE id = ? AND profile_name = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.setString(2, getCurrentProfileName());
                ps.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public List<JournalEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public int getEntryCount() {
        return entries.size();
    }
}