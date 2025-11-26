import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class JournalGUI {

    private final App app = new App();
    private final Scene scene;

    public JournalGUI(HomepageGUI homeController, Stage stage) {

        // --- Back Button ---
        Button backButton = new Button("← Back");
        backButton.setOnAction(e -> homeController.goToHome(stage));

        // --- Input Fields ---
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea entryArea = new TextArea();
        entryArea.setPromptText("Write your journal entry here...");
        entryArea.setPrefRowCount(5);

        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy", "Sad", "Neutral", "Stressed", "Excited");
        moodComboBox.setValue("Neutral");

        Button addButton = new Button("Add Entry");

        // --- Display List ---
        ListView<String> listView = new ListView<>();
        Label countLabel = new Label("Total Entries: 0");
        Button deleteButton = new Button("Delete Selected Entry");

        // --- Layout ---
        VBox inputBox = new VBox(5, titleField, moodComboBox, entryArea, addButton);
        inputBox.setPrefWidth(400);

        VBox listBox = new VBox(5, listView, deleteButton, countLabel);
        listBox.setPrefWidth(400);

        HBox content = new HBox(10, inputBox, listBox);
        content.setPadding(new Insets(10));

        VBox root = new VBox(10, backButton, content);
        root.setPadding(new Insets(10));

        // --- Button Actions ---
        addButton.setOnAction(e -> {
            String title = titleField.getText().trim();
            String entry = entryArea.getText().trim();
            String mood = moodComboBox.getValue();

            if (!app.addEntry(title, mood, entry)) {
                showAlert("Entry cannot be empty!");
            } else {
                titleField.clear();
                entryArea.clear();
                moodComboBox.setValue("Neutral");
                refreshList(listView, countLabel);
            }
        });

        deleteButton.setOnAction(e -> {
            int idx = listView.getSelectionModel().getSelectedIndex();
            if (idx >= 0) {
                app.deleteEntry(idx);
                refreshList(listView, countLabel);
            } else {
                showAlert("Select an entry to delete!");
            }
        });

        scene = new Scene(root, 800, 500);
    }

    public Scene getScene() {
        return scene;
    }

    private void refreshList(ListView<String> listView, Label countLabel) {
        listView.getItems().clear();
        for (JournalEntry entry : app.getEntries()) {
            listView.getItems().add(entry.toString());
        }
        countLabel.setText("Total Entries: " + app.getEntryCount());
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Journal Alert");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
