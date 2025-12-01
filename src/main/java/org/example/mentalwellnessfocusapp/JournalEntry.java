package org.example.mentalwellnessfocusapp;

import java.time.*;

public class JournalEntry {
    private final String title;
    private final String mood;
    private final LocalDate date;
    private final String entry;
    
    // Constructor for JournalEntry
    public JournalEntry(String title, String mood, String entry) {
        this.title = title;
        this.mood = mood;
        this.date = LocalDate.now();
        this.entry = entry;
    }

    // Gets the current date when entry is created
    public LocalDate getDate() {
        return date;
    }

    // Gets the title of the entry
    public String getTitle() {
        return title;
    }

    // Gets the chosen mood for the entry (Selection tab available in GUI)
    public String getMood() {
        return mood;
    }

    // The entry itself
    public String getEntry() {
        return entry;
    }

    // String representation of how the entry will be displayed
    @Override
    public String toString() {
        return "{" + getDate() +"} [" + mood + "]\n" +
        "Title: " + title + "\n" +
        "==============================\n\n" + "\"" + entry + "\"" + "\n\n==============================\n";
    }
}
