package org.example.mentalwellnessfocusapp;

import java.util.List;

public class App {

    private final UserProfile userProfile;
    private final Journal journal;

    // App is now tied to a specific user/profile
    public App(UserProfile userProfile) {
        this.userProfile = userProfile;
        this.journal = new Journal(userProfile);
    }

    // If the current profile name changes, reload that user's entries
    public void reloadForCurrentProfile() {
        journal.reloadForCurrentProfile();
    }

    public boolean addEntry(String title, String mood, String entry) {
        return journal.addEntry(title, mood, entry);
    }

    public List<JournalEntry> getEntries() {
        return journal.getEntries();
    }

    public int getEntryCount() {
        return journal.getEntryCount();
    }

    public boolean deleteEntry(int index) {
        return journal.deleteEntry(index);
    }
}