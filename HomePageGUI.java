import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class HomepageGUI extends Application {

    private Scene homeScene; // store homepage scene for navigation

    @Override
    public void start(Stage stage) {
        // --- Grid layout for homepage ---
        GridPane grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(50);
        grid.setPadding(new Insets(50));
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #ffffff;");

        // --- Create clickable image buttons ---
        StackPane studyBtn = createImageButton("/icons/Focus.png", 300, 300, () -> {
            System.out.println("Open Study GUI");
            // TODO: Open Study GUI
        });

        StackPane journalBtn = createImageButton("/icons/Journal.png", 300, 300, () -> {
            // Open Journal GUI
            JournalGUI journalGUI = new JournalGUI(this, stage);
            stage.setScene(journalGUI.getScene());
        });

        StackPane breathingBtn = createImageButton("/icons/Breathing.png", 300, 300, () -> {
            System.out.println("Open Breathing GUI");
            // TODO: Open Breathing GUI
        });

        StackPane trackerBtn = createImageButton("/icons/Progress.png", 300, 300, () -> {
            System.out.println("Open Progress Tracker GUI");
            // TODO: Open Progress Tracker GUI
        });

        // --- Add buttons to grid ---
        grid.add(studyBtn, 0, 0);
        grid.add(breathingBtn, 1, 0);
        grid.add(journalBtn, 0, 1);
        grid.add(trackerBtn, 1, 1);

        // --- Set homepage scene ---
        homeScene = new Scene(grid, 800, 800);
        stage.setTitle("Wellness App");
        stage.setScene(homeScene);
        stage.show();
    }

    // --- Helper method to create image buttons with hover animation ---
    private StackPane createImageButton(String iconPath, double width, double height, Runnable onClick) {
        ImageView icon = new ImageView(new Image(getClass().getResource(iconPath).toExternalForm()));
        icon.setFitWidth(width);
        icon.setFitHeight(height);
        icon.setPreserveRatio(true);

        StackPane pane = new StackPane(icon);
        pane.setAlignment(Pos.CENTER);

        // Click handler
        pane.setOnMouseClicked(e -> onClick.run());

        // Hover animation
        ScaleTransition grow = new ScaleTransition(Duration.millis(150), pane);
        grow.setToX(1.1);
        grow.setToY(1.1);

        ScaleTransition shrink = new ScaleTransition(Duration.millis(150), pane);
        shrink.setToX(1.0);
        shrink.setToY(1.0);

        pane.setOnMouseEntered(e -> grow.playFromStart());
        pane.setOnMouseExited(e -> shrink.playFromStart());

        return pane;
    }

    // --- Method called from JournalGUI to return to homepage ---
    public void goToHome(Stage stage) {
        stage.setScene(homeScene);
    }

    public static void main(String[] args) {
        launch();
    }
}
