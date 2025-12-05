package org.example.mentalwellnessfocusapp;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class JournalGUI {

    // One shared App instance backing the journal, tied to the current user profile
    private static App app;
    

    // Per-window references so we can log activity + streaks
    private final ActivityLog activityLog;
    private final StreakTracker streakTracker;

    private JournalGUI(ActivityLog activityLog, StreakTracker streakTracker) {
        this.activityLog = activityLog;
        this.streakTracker = streakTracker;
    }

    // Called from MainController, now with the current UserProfile + logs
    public static void showJournalWindow(UserProfile userProfile,
                                         ActivityLog activityLog,
                                         StreakTracker streakTracker) {
        
        // Require a profile name before using the journal
        String name = userProfile.getName();
        if (name == null || name.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Required");
            alert.setHeaderText(null);
            alert.setContentText("Please add your name in the Menu > Profile Settings before using the journal.");
            alert.showAndWait();
            return;
        }
        
        // Initialize or refresh App for this profile
        if (app == null) {
            app = new App(userProfile);
        } else {
            // Profile name may have changed -> reload journal entries for current profile
            app.reloadForCurrentProfile();
        }

        JournalGUI gui = new JournalGUI(activityLog, streakTracker);
        gui.show();
    }

    // Show the journal window
    private void show() {
        Stage stage = new Stage();
        stage.setTitle("Journal");

        // --- Back button (blue) ---
        Button backButton = new Button("Back");
        backButton.setStyle(
                "-fx-background-color: linear-gradient(#5ab4f5, #368de8);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 4 12;"
        );
        backButton.setOnAction(e -> stage.close());

        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // --- Inputs ---
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        ComboBox<String> moodBox = new ComboBox<>();
        moodBox.getItems().addAll(
                "😊 Happy",
                "😐 Neutral",
                "😔 Sad",
                "😤 Stressed",
                "😴 Tired"
        );
        moodBox.setPromptText("Mood");
        moodBox.setStyle("-fx-font-size: 16px;");

        TextArea entryArea = new TextArea();
        entryArea.setPromptText("Write your thoughts here...");
        entryArea.setWrapText(true);
        entryArea.setPrefRowCount(10);
        entryArea.setMinHeight(160);
        entryArea.setMaxHeight(220);

        Label titleLabel = new Label("Title:");
        Label moodLabel  = new Label("Mood:");
        Label entryLabel = new Label("Entry:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        moodLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        entryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button addButton = new Button("Add Entry");
        Button deleteButton = new Button("Delete Selected");

        addButton.setStyle(
                "-fx-background-color: linear-gradient(#72c96b, #4caf50);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 4 12;"
        );
        deleteButton.setStyle(
                "-fx-background-color: linear-gradient(#ff6b6b, #e53935);" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 6;" +
                "-fx-padding: 4 12;"
        );
        // --- Layout ---
        HBox buttonRow = new HBox(10, addButton, deleteButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        // --- List of entries ---
        ListView<JournalEntry> listView = new ListView<>();
        Label entriesLabel = new Label("Your Entries:");
        entriesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label countLabel = new Label("Total Entries: 0");

        // Custom cell to show date + title
        listView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(JournalEntry item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDate() + " - " + item.getTitle());
                }
            }
        });

        // When an entry is selected, populate the input fields
        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldEntry, entry) -> {
                    if (entry != null) {
                        titleField.setText(entry.getTitle());
                        moodBox.setValue(entry.getMood());
                        entryArea.setText(entry.getEntry());
                    }
                });
        // --- Final assembly ---
        VBox inputBox = new VBox(8,
                titleLabel, titleField,
                moodLabel, moodBox,
                entryLabel, entryArea,
                buttonRow
        );
        
        inputBox.setPadding(new Insets(10));
        

        VBox listBox = new VBox(8,
                entriesLabel,
                listView,
                countLabel
        );

        
        VBox.setVgrow(listView, Priority.ALWAYS);
        listBox.setPadding(new Insets(10));
        
        // --- Overall layout ---
        VBox mainContent = new VBox(10, inputBox, listBox);
        mainContent.setPadding(new Insets(10));
        // Stretch to fill
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(mainContent);
        root.setStyle("-fx-background-color: #f5f5f5;");
        
        // --- Scene and stage ---
        Scene scene = new Scene(root, 360, 640);
        stage.setScene(scene);
        stage.show();

        // --- Button logic ---
        // Add button logic
        addButton.setOnAction(e -> {
            String title = titleField.getText();
            String mood = (moodBox.getValue() == null) ? "Unspecified" : moodBox.getValue();
            String entry = entryArea.getText();

            if (app.addEntry(title, mood, entry)) {
                // Only here do we log a JOURNAL activity + streak
                activityLog.recordToday(ActivityType.JOURNAL);
                streakTracker.completeToday();

                refreshList(listView, countLabel);
                listView.getSelectionModel().clearSelection();
                titleField.clear();
                entryArea.clear();
                moodBox.getSelectionModel().clearSelection();
            } else {
                showAlert("Please write something before adding an entry.");
            }
        });

        // Delete button logic
        deleteButton.setOnAction(e -> {
            int index = listView.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                if (app.deleteEntry(index)) {
                    refreshList(listView, countLabel);
                    listView.getSelectionModel().clearSelection();
                    titleField.clear();
                    entryArea.clear();
                    moodBox.getSelectionModel().clearSelection();
                }
            } else {
                showAlert("Select an entry to delete.");
            }
        });

        // initial population
        refreshList(listView, countLabel);
    }
    // Refresh the list of entries from the App
    private void refreshList(ListView<JournalEntry> listView, Label countLabel) {
        listView.getItems().setAll(app.getEntries());
        countLabel.setText("Total Entries: " + app.getEntryCount());
    }
    
    // Show an alert dialog with a message
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Journal");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}