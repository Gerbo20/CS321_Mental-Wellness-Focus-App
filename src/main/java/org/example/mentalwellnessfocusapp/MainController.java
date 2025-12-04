package org.example.mentalwellnessfocusapp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

// Main controller for the Mental Wellness Focus App
public class MainController {   
    
    // UI components
    @FXML
    private Label welcomeText;
    
    // Core components
    private final ActivityLog activityLog = new ActivityLog();
    // Streak tracker and notification manager
    private final StreakTracker streakTracker = new StreakTracker();
    // Notification manager to handle daily streak messages
    private final NotificationManager notificationManager = new NotificationManager(streakTracker);

    // Helper method to complete an activity and update the streak tracker
    private  void completeActivity(ActivityType activityType) {
        activityLog.recordToday(activityType);
        streakTracker.completeToday();
    }

    // Initialization method called by JavaFX after FXML is loaded
    @FXML
    public void initialize() {
        welcomeText.setText("Welcome to your Mental Wellness Focus App 🌱");
    }

    // Menu button handler
    @FXML
    private void onMenuClicked() {
        // For now just log or print something
        System.out.println(">>> Menu button clicked");
        
        // For now: simple menu popup so the app runs without crashing.
        Alert menuAlert = new Alert(Alert.AlertType.INFORMATION);
        menuAlert.setTitle("Menu");
        menuAlert.setHeaderText(null);
        menuAlert.setContentText("Here we’ll later add:\n\n• Sign in / Switch user\n• Exit app");
        menuAlert.showAndWait();

        // Later you can open your sliding panel or popup here
        // e.g. show a small window with "Sign in" and "Exit"
    }

    // Focus Session button handler
    @FXML
    protected void onFocusSession() {
        // Simulate completing a focus session
        completeActivity(ActivityType.FOCUS);
        
        String msg = notificationManager.getDailyStreakMessage();
        welcomeText.setText("Focus Session complete!\n" + msg);
    }

    // Breathing Exercises button handler
    @FXML
    protected void onBreathingExercises() {
        // Simulate completing a breathing exercise
        completeActivity(ActivityType.BREATHING);

        welcomeText.setText("""
            Breathing Exercises: 

            • Inhale for 4 seconds
            • Hold for 4 seconds
            • Exhale for 6 seconds

            Repeat this a few times to reset your nervous system."""
        );
    }

    // Journal Entry button handler
    @FXML
    protected void onJournalEntry() {
        // Simulate completing a journal entry
        completeActivity(ActivityType.JOURNAL);
        // Open the journal GUI window
        JournalGUI.showJournalWindow();
    }

    // Progress & Streaks button handler
    @FXML
    protected void onProgressStreaks() {
        ProgressStreaksGUI.show(streakTracker, activityLog);
    }
}