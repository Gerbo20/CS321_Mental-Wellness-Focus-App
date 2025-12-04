package org.example.mentalwellnessfocusapp;

import java.time.LocalDate;

public class JournalEntry {

    private final String title;
    private final String mood;
    private final String entry;
    private final LocalDate date;

    public JournalEntry(String title, String mood, String entry, LocalDate date) {
        this.title = title;
        this.mood = mood;
        this.entry = entry;
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getMood() {
        return mood;
    }

    public String getEntry() {
        return entry;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public String toString() {
        // Only used if a ListView prints the entry directly
        return date + " - " + title + " (" + mood + ")";
    }
}