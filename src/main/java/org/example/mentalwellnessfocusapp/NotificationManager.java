package org.example.mentalwellnessfocusapp;

public class NotificationManager {

    private final StreakTracker tracker;

    public NotificationManager(StreakTracker tracker) {
        this.tracker = tracker;
    }

    public String getDailyStreakMessage() {
        if (tracker.hasMissedDay()) {
            return MessageBank.getRandomBroken();
        } else if (tracker.getStreak() > 0) {
            return MessageBank.getRandomStreakSuccess(tracker.getStreak());
        } else {
            return "Start your first wellness day today 🌱";
        }
    }

    public String getWarningMessage() {
        return MessageBank.getRandomWarning();
    }

    public String getMorningMotivationMessage() {
        return MessageBank.getMorningMotivation();
    }
}
