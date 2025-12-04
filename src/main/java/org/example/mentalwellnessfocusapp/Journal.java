package org.example.mentalwellnessfocusapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Journal {
    // All journal entries for this run of the app
    private final List<JournalEntry> entries = new ArrayList<>();
    // Add a new journal entry.
    // Returns false if both title and entry are blank.
    public boolean addEntry(String title, String mood, String text) {
        // Simple validation: must have some text
        if ((title == null || title.isBlank()) &&
            (text == null || text.isBlank())) {
            return false;
        }
        if (mood == null || mood.isBlank()) {
            mood = "Unspecified";
        }
        entries.add(new JournalEntry(title, mood, text, LocalDate.now()));
        return true;
    }
    // Delete the entry at the given index.
    // Returns true if deleted, false if index invalid.
    public boolean deleteEntry(int index) {
        if (index < 0 || index >= entries.size()) {
            return false;
        }
        entries.remove(index);
        return true;
    }
    // Read-only view of all entries.
    public List<JournalEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }
    // Get the total number of entries.
    public int getEntryCount() {
        return entries.size();
    }
}