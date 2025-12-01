package org.example.mentalwellnessfocusapp;

import java.time.LocalDate;

public class StreakTracker {

    private int streak = 0;
    private LocalDate lastCompleted = null;

    // Call this when the user completes their wellness task for the day
    public void completeToday() {
        LocalDate today = LocalDate.now();

        if (lastCompleted == null) {
            streak = 1;
        } else if (lastCompleted.plusDays(1).equals(today)) {
            // yesterday → continue streak
            streak++;
        } else if (lastCompleted.equals(today)) {
            // already completed today; do nothing
        } else {
            // missed one or more days → reset streak
            streak = 1;
        }

        lastCompleted = today;
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
