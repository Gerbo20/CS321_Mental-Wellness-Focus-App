package org.example.mentalwellnessfocusapp;

import java.util.List;

public class App {

    // This Journal holds all the entries in memory
    private final Journal journal = new Journal();

    public App() {
        // Optional: you can start with a sample entry using journal.addEntry(...)
        // journal.addEntry("Welcome!", "😊 Happy", "This is your first journal entry.");
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