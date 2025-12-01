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

    // static so entries persist between openings
    private static final App app = new App();

    // Called from MainController
    public static void showJournalWindow() {
        JournalGUI gui = new JournalGUI();
        gui.show();
    }

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
        // Font to match subtitles (not bold)
        moodBox.setStyle("-fx-font-size: 16px;");

        TextArea entryArea = new TextArea();
        entryArea.setPromptText("Write your thoughts here...");
        entryArea.setWrapText(true);
        entryArea.setPrefRowCount(10);
        entryArea.setMinHeight(160);
        entryArea.setMaxHeight(220);


        // Subtitle labels (Title, Mood, Entry)
        Label titleLabel = new Label("Title:");
        Label moodLabel  = new Label("Mood:");
        Label entryLabel = new Label("Entry:");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        moodLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        entryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button addButton = new Button("Add Entry");
        Button deleteButton = new Button("Delete Selected");

        // Button styles like small homepage tiles
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

        HBox buttonRow = new HBox(10, addButton, deleteButton);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        // --- List of entries ---
        ListView<JournalEntry> listView = new ListView<>();
        Label entriesLabel = new Label("Your Entries:");
        entriesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label countLabel = new Label("Total Entries: 0");

        // Only show date + title in the list
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

        // When an entry is selected, show its contents in the fields
        listView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldEntry, entry) -> {
                    if (entry != null) {
                        titleField.setText(entry.getTitle());
                        moodBox.setValue(entry.getMood());
                        entryArea.setText(entry.getEntry());
                    }
                });

        // Layout: input area
        VBox inputBox = new VBox(8,
                titleLabel, titleField,
                moodLabel, moodBox,
                entryLabel, entryArea,
                buttonRow
        );
        inputBox.setPadding(new Insets(10));

        // Layout: entries list
        VBox listBox = new VBox(8,
                entriesLabel,
                listView,
                countLabel
        );
        VBox.setVgrow(listView, Priority.ALWAYS);
        listBox.setPadding(new Insets(10));

        // Main vertical content (under the back bar)
        VBox mainContent = new VBox(10, inputBox, listBox);
        mainContent.setPadding(new Insets(10));

        // Root with top bar + content
        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(mainContent);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Scene scene = new Scene(root, 360, 640); // same mobile-ish size
        stage.setScene(scene);
        stage.show();

        // --- Button logic ---

        addButton.setOnAction(e -> {
            String title = titleField.getText();
            String mood = (moodBox.getValue() == null) ? "Unspecified" : moodBox.getValue();
            String entry = entryArea.getText();

            if (app.addEntry(title, mood, entry)) {
                refreshList(listView, countLabel);
                // clear selection + fields after saving
                listView.getSelectionModel().clearSelection();
                titleField.clear();
                entryArea.clear();
                moodBox.getSelectionModel().clearSelection();
            } else {
                showAlert("Please write something before adding an entry.");
            }
        });

        deleteButton.setOnAction(e -> {
            int index = listView.getSelectionModel().getSelectedIndex();
            if (index >= 0) {
                if (app.deleteEntry(index)) {
                    refreshList(listView, countLabel);
                    // clear fields after delete
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

    private void refreshList(ListView<JournalEntry> listView, Label countLabel) {
        listView.getItems().setAll(app.getEntries());
        countLabel.setText("Total Entries: " + app.getEntryCount());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Journal");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}