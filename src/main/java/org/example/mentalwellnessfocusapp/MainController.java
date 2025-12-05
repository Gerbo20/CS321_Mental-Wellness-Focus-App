package org.example.mentalwellnessfocusapp;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

// Main controller for the Mental Wellness Focus App
public class MainController {   

    // UI components
    @FXML
    private Label welcomeText;

    // User profile (name + notification toggle)
    private UserProfile userProfile;

    // Core components tied to the current profile
    private ActivityLog activityLog;
    private StreakTracker streakTracker;
    private NotificationManager notificationManager;

    // Helper method: update the welcome label based on current profile
    private void updateWelcomeText() {
        String name = userProfile.getName();
        if (name != null && !name.isBlank()) {
            welcomeText.setText("Welcome to MindAlign, " + name + " 🌱");
        } else {
            welcomeText.setText("Welcome to MindAlign 🌱");
        }
    }

    // Helper method to complete an activity and update the streak tracker
    // (we will only use this in places where the feature itself is not already logging)
    private void completeActivity(ActivityType activityType) {
        activityLog.recordToday(activityType);
        streakTracker.completeToday();
    }

    // Initialization method called by JavaFX after FXML is loaded
    @FXML
    public void initialize() {
        // Load the user profile
        userProfile = UserProfile.loadOrDefault();
        // Immediately clear the name so there is no auto sign-in
        //userProfile.setName("");

        // Create per-user components using this profile
        activityLog = new ActivityLog(userProfile);
        streakTracker = new StreakTracker(userProfile);
        notificationManager = new NotificationManager(streakTracker);

        // Always start with the generic welcome text
        updateWelcomeText();
    }

    // Menu button handler: open Profile Settings dialog
    @FXML
    private void onMenuClicked() {
        System.out.println(">>> Menu button clicked (Profile Settings)");

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Profile Settings");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your name");
        nameField.setText(userProfile.getName());

        CheckBox notificationsBox = new CheckBox("Enable motivational notifications");
        notificationsBox.setSelected(userProfile.isNotificationsEnabled());

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        saveButton.setOnAction(event -> {
            String newName = nameField.getText();

            if (newName == null || newName.isBlank()) {
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Error Saving Profile");
                error.setHeaderText(null);
                error.setContentText("You need a name to save your profile.");
                error.showAndWait();
                return;
            }

            userProfile.setName(newName.trim());
            userProfile.setNotificationsEnabled(notificationsBox.isSelected());
            userProfile.save();   // persists settings (mainly for notifications)

            // After changing the profile name, reload per-user data
            activityLog.reloadForCurrentProfile();
            streakTracker.reloadForCurrentProfile();

            // Update main welcome text
            updateWelcomeText();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Saved");
            alert.setHeaderText(null);
            alert.setContentText("Your profile has been updated.");
            alert.showAndWait();

            dialog.close();
        });

        cancelButton.setOnAction(event -> dialog.close());

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(10,
                new Label("Profile name:"),
                nameField,
                notificationsBox,
                buttons
        );
        root.setPadding(new Insets(15));

        dialog.setScene(new Scene(root));
        dialog.showAndWait();
    }

    // Focus Session button handler
    @FXML
    protected void onFocusSession() {
        // DO NOT call completeActivity here anymore
        // The focus screen will log & update streaks when the user starts the session
        FocusSessionGUI.show(streakTracker, activityLog, userProfile, notificationManager);

        if (userProfile.isNotificationsEnabled()) {
            String msg = notificationManager.getDailyStreakMessage();
            welcomeText.setText("Focus Session: Stay sharp and motivated!");
        } else {
            welcomeText.setText("Focus Session completed!");
        }

        System.out.println(">>> Focus Session button clicked");
    }

    // Breathing Exercises button handler
    @FXML
    protected void onBreathingExercises() {
        // DO NOT call completeActivity here anymore
        // The breathing screen will log & update streaks when the user starts the exercise
        BreathingGUI.show(userProfile, streakTracker, activityLog, notificationManager);

        if (userProfile.isNotificationsEnabled()) {
            String msg = notificationManager.getDailyStreakMessage();
            welcomeText.setText("Breathing Exercises complete!\n" + msg);
        } else {
            welcomeText.setText("Breathing Exercises complete!");
        }

        System.out.println(">>> Breathing Exercises button clicked");
    }

    // Journal Entry button handler
    @FXML
    protected void onJournalEntry() {
        // DO NOT log here anymore
        // JournalGUI will log & update streaks only if the user really adds an entry
        JournalGUI.showJournalWindow(userProfile, activityLog, streakTracker);

        if (userProfile.isNotificationsEnabled()) {
            String msg = notificationManager.getDailyStreakMessage();
            welcomeText.setText("Journal entry saved!\n" + msg);
        } else {
            welcomeText.setText("Journal entry saved!");
        }

        System.out.println(">>> Journal Entry button clicked");
    }

    // Progress & Streaks button handler
    @FXML
    protected void onProgressStreaks() {
        ProgressStreaksGUI.show(streakTracker, activityLog);

        if (userProfile.isNotificationsEnabled()) {
            String msg = notificationManager.getDailyStreakMessage();
            welcomeText.setText("Streaks Progress opened!\n" + msg);
        } else {
            welcomeText.setText("Streaks Progress opened!");
        }

        System.out.println(">>> Progress & Streaks button clicked");
    }
}