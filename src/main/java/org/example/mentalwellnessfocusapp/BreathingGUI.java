package org.example.mentalwellnessfocusapp;

import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
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

    // For changing text "Inhale" / "Exhale" in sync with animation
    private Timeline phaseTimeline;

    private BreathingGUI(StreakTracker streakTracker,
                        ActivityLog activityLog,
                        NotificationManager notificationManager) {
        this.streakTracker = Objects.requireNonNull(streakTracker);
        this.activityLog = Objects.requireNonNull(activityLog);
        this.notificationManager = Objects.requireNonNull(notificationManager);
    }

    public static void show(StreakTracker streakTracker,
                            ActivityLog activityLog,
                            NotificationManager notificationManager) {
        new BreathingGUI(streakTracker, activityLog, notificationManager).buildAndShow();
    }

    private void buildAndShow() {
        stage = new Stage();
        stage.setTitle("Breathing Exercises");
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: #f7f7f7;");

        // ----- Top bar: Back -----
        backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> stage.close());

        BorderPane topBar = new BorderPane();
        topBar.setLeft(backButton);
        topBar.setPadding(new Insets(4, 0, 4, 0));
        root.setTop(topBar);

        // ----- Center: title, circle, instructions, Start button -----
        VBox centerBox = new VBox(18);
        centerBox.setPadding(new Insets(16));
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setFillWidth(true);

        Label screenTitle = new Label("Breathing Exercises");
        screenTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        breathingCircle = new BreathingCircle(60, 100);

        phaseLabel = new Label("Inhale as the circle grows, exhale as it shrinks.");
        phaseLabel.setStyle("-fx-font-size: 16px;");
        phaseLabel.setWrapText(true);
        phaseLabel.setAlignment(Pos.CENTER);
        phaseLabel.setMaxWidth(280);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

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
        // Optional: log breathing activity once when user starts
        activityLog.recordToday(ActivityType.BREATHING);

        // 4 seconds inhale, 4 seconds exhale
        Duration inhale = Duration.seconds(4);
        Duration exhale = Duration.seconds(4);

        breathingCircle.startBreathing(inhale, exhale);
        startPhaseTextCycle(inhale, exhale);
        startButton.setDisable(true);
    }

    private void startPhaseTextCycle(Duration inhale, Duration exhale) {
        if (phaseTimeline != null) {
            phaseTimeline.stop();
        }

        double inhaleSecs = inhale.toSeconds();
        double exhaleSecs = exhale.toSeconds();
        double cycleSecs = inhaleSecs + exhaleSecs;

        phaseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO, e -> {
                    breathingCircle.setPhaseText("Inhale");
                    phaseLabel.setText("Breathe in slowly...");
                }),
                new KeyFrame(Duration.seconds(inhaleSecs), e -> {
                    breathingCircle.setPhaseText("Exhale");
                    phaseLabel.setText("Breathe out gently...");
                }),
                new KeyFrame(Duration.seconds(cycleSecs))
        );
        phaseTimeline.setCycleCount(Timeline.INDEFINITE);
        phaseTimeline.play();
    }
}