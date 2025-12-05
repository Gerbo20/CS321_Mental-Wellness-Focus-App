package org.example.mentalwellnessfocusapp;

import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BreathingGUI {

    private static final double WIDTH = 360;
    private static final double HEIGHT = 640;

    private final StreakTracker streakTracker;
    private final ActivityLog activityLog;
    private final NotificationManager notificationManager;

    private Stage stage;
    private BreathingCircle breathingCircle;
    private Label phaseLabel;

    private Button startButton;
    private Button backButton;
    private Button pauseButton;
    private Button resetButton;

    private BorderPane root;
    private VBox centerBox;
    private HBox controlButtonsBox;

    // For changing text "Inhale" / "Exhale" / "Hold" in sync with animation
    private Timeline phaseTimeline;
    private boolean isPaused = false;

    private BreathingGUI(StreakTracker streakTracker,
                         ActivityLog activityLog,
                         NotificationManager notificationManager) {
        this.streakTracker = Objects.requireNonNull(streakTracker);
        this.activityLog = Objects.requireNonNull(activityLog);
        this.notificationManager = Objects.requireNonNull(notificationManager);
    }

    /**
     * Show the breathing screen.
     * We REQUIRE a profile name so the app does not track anonymous usage.
     */
    public static void show(UserProfile userProfile,
                            StreakTracker streakTracker,
                            ActivityLog activityLog,
                            NotificationManager notificationManager) {

        String name = userProfile.getName();
        if (name == null || name.isBlank()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Required");
            alert.setHeaderText(null);
            alert.setContentText("Please add your name in the Menu > Profile Settings before using the breathing exercises.");
            alert.showAndWait();
            return;
        }

        new BreathingGUI(streakTracker, activityLog, notificationManager).buildAndShow();
    }

    private void buildAndShow() {
        stage = new Stage();
        stage.setTitle("Breathing Exercises");
        stage.setResizable(false);

        root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: #f7f7f7;");

        // ----- Top bar: Back (initial position) -----
        backButton = new Button("Back");
        backButton.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold;"
        );
        backButton.setOnAction(e -> stage.close());

        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        root.setTop(topBar);

        // ----- Center content -----
        centerBox = new VBox(16);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(10, 0, 30, 0));

        Label screenTitle = new Label("Breathing Timer");
        screenTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        breathingCircle = new BreathingCircle(60, 100);

        phaseLabel = new Label("Inhale as the circle grows, exhale as it shrinks.");
        phaseLabel.setStyle("-fx-font-size: 16px;");
        phaseLabel.setWrapText(true);
        phaseLabel.setAlignment(Pos.CENTER);
        phaseLabel.setTextAlignment(TextAlignment.CENTER);
        phaseLabel.setMaxWidth(280);

        Region spacer = new Region();
        spacer.setMinHeight(40);
        VBox.setVgrow(spacer, Priority.SOMETIMES);

        startButton = new Button("Start Exercise");
        startButton.setMinWidth(220);
        startButton.setMinHeight(50);
        startButton.setStyle(
                "-fx-background-color: #27ae60; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold;"
        );
        startButton.setOnAction(e -> startBreathing());

        centerBox.getChildren().addAll(
                screenTitle,
                breathingCircle,
                phaseLabel,
                spacer,
                startButton
        );

        root.setCenter(centerBox);

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    private void startBreathing() {
        // Log breathing activity + give streak credit
        activityLog.recordToday(ActivityType.BREATHING);
        streakTracker.completeToday();

        // 4 seconds inhale, 4 seconds hold, 6 seconds exhale
        Duration inhale = Duration.seconds(4);
        Duration hold   = Duration.seconds(4);
        Duration exhale = Duration.seconds(6);

        isPaused = false;

        breathingCircle.startBreathing(inhale, hold, exhale);
        startPhaseTextCycle(inhale, hold, exhale);

        // Swap layout to Back / Pause / Reset buttons row
        swapToControlButtonsLayout();
    }

    /**
     * After the exercise has started:
     * - remove "Start Exercise"
     * - move Back button down
     * - show Back / Pause / Reset in a row
     */
    private void swapToControlButtonsLayout() {
        // Remove Start button from the bottom
        centerBox.getChildren().remove(startButton);

        // Remove Back button from the top bar and move it down
        root.setTop(null);

        if (controlButtonsBox == null) {
            pauseButton = new Button("Pause");
            resetButton = new Button("Reset");

            backButton.setMinWidth(80);
            pauseButton.setMinWidth(80);
            resetButton.setMinWidth(80);

            String controlStyle =
                    "-fx-background-color: #3498db; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold;";

            backButton.setStyle(controlStyle);
            pauseButton.setStyle(controlStyle);
            resetButton.setStyle(controlStyle);

            pauseButton.setOnAction(e -> onPauseResume());
            resetButton.setOnAction(e -> onReset());

            controlButtonsBox = new HBox(10, backButton, pauseButton, resetButton);
            controlButtonsBox.setAlignment(Pos.CENTER);
            controlButtonsBox.setPadding(new Insets(10, 0, 10, 0));
        } else {
            controlButtonsBox.getChildren().setAll(backButton, pauseButton, resetButton);
        }

        if (!centerBox.getChildren().contains(controlButtonsBox)) {
            centerBox.getChildren().add(controlButtonsBox);
        }
    }

    /**
     * Restore the original layout: top-left Back + big Start button.
     */
    private void restoreStartLayout() {
        if (phaseTimeline != null) {
            phaseTimeline.stop();
        }

        // Remove the control buttons from the center
        if (controlButtonsBox != null) {
            controlButtonsBox.getChildren().remove(backButton);
            centerBox.getChildren().remove(controlButtonsBox);
        }

        // Put the Back button back on the top bar
        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(0, 0, 10, 0));
        root.setTop(topBar);

        // Restore the big Start Exercise button
        if (!centerBox.getChildren().contains(startButton)) {
            centerBox.getChildren().add(startButton);
        }

        isPaused = false;
        if (pauseButton != null) {
            pauseButton.setText("Pause");
        }

        // Reset texts
        breathingCircle.setPhaseText("Ready");
        phaseLabel.setText("Inhale as the circle grows, exhale as it shrinks.");
        phaseLabel.setTextAlignment(TextAlignment.CENTER);
    }

    private void onPauseResume() {
        if (isPaused) {
            // Resume
            isPaused = false;
            breathingCircle.resumeBreathing();
            if (phaseTimeline != null) {
                phaseTimeline.play();
            }
            pauseButton.setText("Pause");
            updatePhasePromptForCurrentStage();
        } else {
            // Pause
            isPaused = true;
            breathingCircle.pauseBreathing();
            if (phaseTimeline != null) {
                phaseTimeline.pause();
            }
            pauseButton.setText("Resume");
            phaseLabel.setText("Paused — tap Resume when you're ready.");
            phaseLabel.setTextAlignment(TextAlignment.CENTER);
        }
    }

    private void onReset() {
        breathingCircle.resetBreathing();
        restoreStartLayout();
    }

    /**
     * Look at the current phase text inside the circle and
     * set the matching instructional prompt.
     */
    private void updatePhasePromptForCurrentStage() {
        String phase = breathingCircle.getPhaseText();
        if ("Inhale".equalsIgnoreCase(phase)) {
            phaseLabel.setText("Breathe in slowly...");
        } else if ("Hold".equalsIgnoreCase(phase)) {
            phaseLabel.setText("Gently hold your breath...");
        } else if ("Exhale".equalsIgnoreCase(phase)) {
            phaseLabel.setText("Breathe out gently...");
        } else {
            phaseLabel.setText("Inhale as the circle grows, exhale as it shrinks.");
        }
        phaseLabel.setTextAlignment(TextAlignment.CENTER);
    }

    private void startPhaseTextCycle(Duration inhale, Duration hold, Duration exhale) {
        if (phaseTimeline != null) {
            phaseTimeline.stop();
        }

        double inhaleSecs = inhale.toSeconds();
        double holdSecs   = hold.toSeconds();
        double exhaleSecs = exhale.toSeconds();
        double cycleSecs  = inhaleSecs + holdSecs + exhaleSecs;

        phaseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    breathingCircle.setPhaseText("Inhale");
                    phaseLabel.setText("Breathe in slowly...");
                    phaseLabel.setTextAlignment(TextAlignment.CENTER);
                }),
                new KeyFrame(Duration.seconds(inhaleSecs), e -> {
                    breathingCircle.setPhaseText("Hold");
                    phaseLabel.setText("Gently hold your breath...");
                    phaseLabel.setTextAlignment(TextAlignment.CENTER);
                }),
                new KeyFrame(Duration.seconds(inhaleSecs + holdSecs), e -> {
                    breathingCircle.setPhaseText("Exhale");
                    phaseLabel.setText("Breathe out gently...");
                    phaseLabel.setTextAlignment(TextAlignment.CENTER);
                }),
                new KeyFrame(Duration.seconds(cycleSecs))
        );
        phaseTimeline.setCycleCount(Timeline.INDEFINITE);
        phaseTimeline.play();
    }
}