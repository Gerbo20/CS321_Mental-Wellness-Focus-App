import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class JournalGUI extends Application {

    private final App app = new App();

    @Override
    public void start(Stage stage) {
        // --- Input Fields ---
        TextField titleField = new TextField();
        titleField.setPromptText("Title");

        TextArea entryArea = new TextArea();
        entryArea.setPromptText("Write your journal entry here...");
        entryArea.setPrefRowCount(5);

        ComboBox<String> moodComboBox = new ComboBox<>();
        moodComboBox.getItems().addAll("Happy", "Sad", "Neutral", "Stressed", "Excited");
        moodComboBox.setValue("Neutral"); // default

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

        HBox root = new HBox(10, inputBox, listBox);
        root.setPadding(new javafx.geometry.Insets(10));

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
            int selectedIndex = listView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                app.deleteEntry(selectedIndex);
                refreshList(listView, countLabel);
            } else {
                showAlert("Select an entry to delete!");
            }
        });

        // --- Scene & Stage ---
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Wellness Journal");
        stage.show();
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

    public static void main(String[] args) {
        launch();
    }
}
