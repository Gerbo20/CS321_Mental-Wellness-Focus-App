package org.example.mentalwellnessfocusapp;

import java.time.LocalDate;

public class DayActivity {

    private final LocalDate date;
    private boolean didFocus;
    private boolean didBreathing;
    private boolean didJournal;

    public DayActivity(LocalDate date) {
        this.date = date;
    }

    public LocalDate getDate() {
        return date;
    }

    public boolean isDidFocus() {
        return didFocus;
    }

    public void setDidFocus(boolean didFocus) {
        this.didFocus = didFocus;
    }

    public boolean isDidBreathing() {
        return didBreathing;
    }

    public void setDidBreathing(boolean didBreathing) {
        this.didBreathing = didBreathing;
    }

    public boolean isDidJournal() {
        return didJournal;
    }

    public void setDidJournal(boolean didJournal) {
        this.didJournal = didJournal;
    }

    /** Simple “effort score” for charts. */
    public int getScore() {
        int score = 0;
        if (didFocus)     score += 1; 
        if (didJournal)   score += 1;
        if (didBreathing) score += 1;
        return score;
    }
}
