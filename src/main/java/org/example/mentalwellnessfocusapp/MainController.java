package org.example.mentalwellnessfocusapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainController {

    @FXML
    private Label welcomeText;

    @FXML
    private StackPane focusTile;

    @FXML
    private StackPane breathingTile;

    @FXML
    private StackPane journalTile;

    @FXML
    private StackPane streakTile;

    // Streak + notification system
    private final StreakTracker streakTracker = new StreakTracker();
    private final NotificationManager notificationManager =
            new NotificationManager(streakTracker);

    @FXML
    public void initialize() {
        welcomeText.setText("Welcome to your Mental Wellness Focus App 🌱");
    }

    // TILE: Focus Session  (use this as “complete today’s task”)
    @FXML
    protected void onFocusSession() {
        streakTracker.completeToday();
        String msg = notificationManager.getDailyStreakMessage();
        welcomeText.setText("Focus Session complete!\n" + msg);
    }

    // TILE: Breathing Exercises
    @FXML
    protected void onBreathingExercises() {
        welcomeText.setText(
                "Breathing Exercises:\n\n" +
                    "• Inhale for 4 seconds\n" +
                    "• Hold for 4 seconds\n" +
                    "• Exhale for 6 seconds\n\n" +
                "Repeat this a few times to reset your nervous system."
        );
    }

    // TILE: Journal Entry  (you can later open your JournalGUI here)
    @FXML
    protected void onJournalEntry() {
        JournalGUI.showJournalWindow();
    }

    // TILE: Progress Streaks
    @FXML
    protected void onProgressStreaks() {
        String msg = notificationManager.getDailyStreakMessage();
        welcomeText.setText("Your current streak info:\n" + msg);
    }

    // (Optional: you can keep your old button handlers if you still use them elsewhere)
}