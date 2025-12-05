package org.example.mentalwellnessfocusapp;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    // Singleton instance
    private static DatabaseManager INSTANCE;

    // SQLite DB connection
    private final Connection connection;

    // Private constructor for singleton
    private DatabaseManager() throws SQLException {
        
        // DB file will be created next to where you run the app
        String url = "jdbc:sqlite:mentalwellness.db";
        this.connection = DriverManager.getConnection(url);
        initSchema();
    }

    // Get singleton instance of DatabaseManager
    public static synchronized DatabaseManager getInstance() throws SQLException {
        if (INSTANCE == null) {
            INSTANCE = new DatabaseManager();
        }
        return INSTANCE;
    }

    // Get the DB connection
    public Connection getConnection() {
        return connection;
    }

    // Initialize DB schema (tables, migrations, backfills)
    private void initSchema() throws SQLException {
        // 1. Create tables normally
        try (Statement st = connection.createStatement()) {

            // Journal entries table
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS journal_entries (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "profile_name TEXT," +
                    "title TEXT," +
                    "mood TEXT," +
                    "entry_text TEXT," +
                    "date TEXT" +
                ")"
            );

            // Daily activities log
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS day_activity (" +
                    "profile_name TEXT," +
                    "date TEXT NOT NULL," +
                    "did_focus INTEGER NOT NULL," +
                    "did_breathing INTEGER NOT NULL," +
                    "did_journal INTEGER NOT NULL," +
                    "PRIMARY KEY (profile_name, date)" +
                ")"
            );

            // Streaks tracking
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS streak_state (" +
                    "profile_name TEXT PRIMARY KEY," +
                    "streak INTEGER NOT NULL," +
                    "last_completed TEXT" +
                ")"
            );

            // User profile (single-user settings)
            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS user_profile (" +
                    "id INTEGER PRIMARY KEY CHECK (id = 1)," +
                    "name TEXT NOT NULL," +
                    "notifications_enabled INTEGER NOT NULL" +
                ")"
            );
        }

        // 2. MIGRATION: Add profile_name column to existing tables if missing
        try (Statement st = connection.createStatement()) {
            st.executeUpdate("ALTER TABLE journal_entries ADD COLUMN profile_name TEXT");
        } catch (SQLException ignore) {}

        try (Statement st = connection.createStatement()) {
            st.executeUpdate("ALTER TABLE day_activity ADD COLUMN profile_name TEXT");
        } catch (SQLException ignore) {}

        try (Statement st = connection.createStatement()) {
            st.executeUpdate("ALTER TABLE streak_state ADD COLUMN profile_name TEXT");
        } catch (SQLException ignore) {}

        // 3. BACKFILL: Set default profile_name for existing records
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(
                "UPDATE journal_entries SET profile_name = 'Toledo' " +
                "WHERE profile_name IS NULL OR profile_name = ''"
            );
            st.executeUpdate(
                "UPDATE day_activity SET profile_name = 'Toledo' " +
                "WHERE profile_name IS NULL OR profile_name = ''"
            );
            st.executeUpdate(
                "UPDATE streak_state SET profile_name = 'Toledo' " +
                "WHERE profile_name IS NULL OR profile_name = ''"
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}