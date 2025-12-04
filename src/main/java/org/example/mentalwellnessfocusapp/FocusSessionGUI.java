package org.example.mentalwellnessfocusapp;

import java.util.Objects;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class FocusSessionGUI {

    // Match HomepageGUI size (adjust if your HomepageGUI uses different values)
    private static final double WIDTH = 360;
    private static final double HEIGHT = 640;

    // Core app objects
    private final StreakTracker streakTracker;
    private final ActivityLog activityLog;
    private final NotificationManager notificationManager;

    // UI
    private Stage stage;
    private VBox centerBox; // main vertical layout
    private HBox controlRow; // holds session length buttons

    private Label timerLabel;
    private Label statusLabel;

    private Button backButton;
    private Button btn15;
    private Button btn30;
    private Button btn45;
    private Button btnCustom;
    private Button startButton;
    private Button pauseButton;
    private Button abortButton;
    private Button resumeButton;
    private Button quickJournalAfterButton;
    private Button closeButton;

    // Timing state
    private Timeline focusTimeline;
    private int remainingSeconds = 0;
    private int originalDurationSeconds = 0;
    private boolean thirtyPopupShown = false;

    private FocusSessionGUI(StreakTracker streakTracker,
                            ActivityLog activityLog,
                            NotificationManager notificationManager) {
        this.streakTracker = Objects.requireNonNull(streakTracker);
        this.activityLog = Objects.requireNonNull(activityLog);
        this.notificationManager = Objects.requireNonNull(notificationManager);
    }

    // --------- Entry point from MainController ---------
    public static void show(StreakTracker streakTracker,
                            ActivityLog activityLog,
                            NotificationManager notificationManager) {
        FocusSessionGUI gui = new FocusSessionGUI(streakTracker, activityLog, notificationManager);
        gui.buildAndShow();
    }

    // --------- UI construction ---------
    private void buildAndShow() {
        stage = new Stage();
        stage.setTitle("Focus Session");
        stage.setResizable(false);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setStyle("-fx-background-color: #f7f7f7;");

        // ========== Top bar: Back + centered window title ==========
        backButton = new Button("Back");
        backButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        backButton.setOnAction(e -> stage.close());

        // Label windowTitle = new Label("Focus Session");
        //windowTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Use a StackPane-style trick with BorderPane so title is visually centered
        BorderPane topBar = new BorderPane();
        topBar.setLeft(backButton);
        // topBar.setCenter(windowTitle);
        // BorderPane.setAlignment(windowTitle, Pos.CENTER);
        topBar.setPadding(new Insets(4, 0, 4, 0));

        root.setTop(topBar);

        // ========== Center content (what you drew in the sketches) ==========
        centerBox = new VBox(16);
        centerBox.setPadding(new Insets(16, 16, 16, 16));
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(true);

        // Screen title inside the page
        Label screenTitle = new Label("Focus Session");
        screenTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");

        // Big timer in the middle
        timerLabel = new Label("00:00");
        timerLabel.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");

        // Status text centered under timer
        statusLabel = new Label("Choose a session length to begin.");
        statusLabel.setStyle("-fx-font-size: 20px;");
        statusLabel.setWrapText(true);
        statusLabel.setAlignment(Pos.CENTER);

        // Spacer to push controls toward bottom, like on a phone screen
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ----- Session length buttons row -----
        btn15 = createLengthButton("15 min", 15 * 60);
        btn30 = createLengthButton("30 min", 30 * 60);
        btn45 = createLengthButton("45 min", 45 * 60);
        btnCustom = new Button("Custom");
        styleLengthButton(btnCustom);
        btnCustom.setOnAction(e -> showCustomTimeDialog());

        HBox lengthRow = new HBox(8, btn15, btn30, btn45, btnCustom);
        lengthRow.setAlignment(Pos.CENTER);

        // Big green Start button under the length buttons
        startButton = new Button("Start");
        startButton.setDisable(true);
        startButton.setMinWidth(220);
        startButton.setMinHeight(60);
        startButton.setStyle(
                "-fx-background-color: #27ae60; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 18px; " +
                "-fx-font-weight: bold;"
        );
        startButton.setOnAction(e -> startSession());

        // ----- Running controls (Pause / Abort) -----
        pauseButton = new Button("Pause");
        pauseButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold;");
        pauseButton.setMinWidth(130);
        pauseButton.setMinHeight(42);
        pauseButton.setOnAction(e -> pauseSession());

        abortButton = new Button("Abort");
        abortButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        abortButton.setMinWidth(130);
        abortButton.setMinHeight(42);
        abortButton.setOnAction(e -> abortSession());

        // HBox runningButtons = new HBox(20, pauseButton, abortButton);
        // runningButtons.setAlignment(Pos.CENTER);

        // Single row that we will reuse for both states
        controlRow = new HBox(20, pauseButton, abortButton);
        controlRow.setAlignment(Pos.CENTER);
        
        // ----- Paused controls (Resume / Abort) -----
        resumeButton = new Button("Resume");
        resumeButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        resumeButton.setMinWidth(130);
        resumeButton.setMinHeight(42);
        resumeButton.setOnAction(e -> resumeSession());

        // HBox pausedButtons = new HBox(20, resumeButton, abortButton);
        // pausedButtons.setAlignment(Pos.CENTER);

        // Single row that we will reuse for both states
        controlRow = new HBox(20, resumeButton, abortButton);
        controlRow.setAlignment(Pos.CENTER);

        // ----- Quick Journal after completion -----
        quickJournalAfterButton = new Button("Quick Journal");
        quickJournalAfterButton.setStyle(
                "-fx-background-color: #f1c40f; " +
                "-fx-text-fill: black; " +
                "-fx-font-weight: bold;"
        );
        quickJournalAfterButton.setMinWidth(220);
        quickJournalAfterButton.setMinHeight(50);
        quickJournalAfterButton.setOnAction(e -> JournalGUI.showJournalWindow());
        quickJournalAfterButton.setVisible(false);
        
        // Close button (acts like Back)
        closeButton = new Button("Close");
        closeButton.setStyle(
                "-fx-background-color: #e74c3c; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold;"
        );
        closeButton.setMinWidth(220);
        closeButton.setMinHeight(40);
        closeButton.setOnAction(e -> stage.close());
        closeButton.setVisible(false);

        // Add everything to centerBox in a single vertical flow:
        //  [screen title]
        //  [timer]
        //  [status]
        //  [spacer]
        //  [length row]
        //  [start button]
        //  [running buttons]      (shown only while running)
        //  [paused buttons]       (shown only while paused)
        //  [Quick Journal button] (shown only after completion)
        centerBox.getChildren().addAll(
                screenTitle,
                timerLabel,
                statusLabel,
                spacer,
                lengthRow,
                startButton,
                // runningButtons,
                // pausedButtons,
                controlRow,
                quickJournalAfterButton,
                closeButton
        );

        root.setCenter(centerBox);

        // Initial visibility for "pre-session" state
        showPreSessionUI();

        Scene scene = new Scene(root, WIDTH, HEIGHT);
        stage.setScene(scene);
        stage.show();
    }

    private Button createLengthButton(String text, int seconds) {
        Button b = new Button(text);
        styleLengthButton(b);
        b.setOnAction(e -> selectDuration(seconds, b));
        return b;
    }

    private void styleLengthButton(Button b) {
        b.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: #2c3e50; -fx-font-weight: bold;");
        b.setMinWidth(80);
        b.setMinHeight(36);
    }

    // --------- State helpers ---------

    private void selectDuration(int seconds, Button sourceButton) {
        this.originalDurationSeconds = seconds;
        this.remainingSeconds = seconds;
        this.thirtyPopupShown = false;

        // Enable Start, update timer & status
        startButton.setDisable(false);
        timerLabel.setText(formatTime(remainingSeconds));
        statusLabel.setText("Press Start when you're ready!");

        // Simple visual cue (bold border on selected)
        clearSelectionStyles();
        sourceButton.setStyle(sourceButton.getStyle() + "; -fx-border-color: #2980b9; -fx-border-width: 2px;");
    }

    private void clearSelectionStyles() {
        styleLengthButton(btn15);
        styleLengthButton(btn30);
        styleLengthButton(btn45);
        styleLengthButton(btnCustom);
    }

    private void showCustomTimeDialog() {
        Stage dialog = new Stage();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Custom Session Length");

        Label hLabel = new Label("Hours:");
        Label mLabel = new Label("Minutes:");
        Label sLabel = new Label("Seconds:");

        // Start at 0 for all fields so user chooses fully
        TextField hField = new TextField("0");
        TextField mField = new TextField("0");
        TextField sField = new TextField("0");

        GridPane grid = new GridPane();
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setPadding(new Insets(12));
        grid.addRow(0, hLabel, hField);
        grid.addRow(1, mLabel, mField);
        grid.addRow(2, sLabel, sField);

        Button accept = new Button("Accept");
        accept.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        Button cancel = new Button("Cancel");
        cancel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        HBox buttonRow = new HBox(10, accept, cancel);
        buttonRow.setAlignment(Pos.CENTER_RIGHT);

        VBox vbox = new VBox(10, grid, errorLabel, buttonRow);
        vbox.setPadding(new Insets(10));

        accept.setOnAction(e -> {
            try {
                int h = Integer.parseInt(hField.getText().trim());
                int m = Integer.parseInt(mField.getText().trim());
                int s = Integer.parseInt(sField.getText().trim());
                int total = h * 3600 + m * 60 + s;
                if (total <= 0) {
                    errorLabel.setText("Please enter a time greater than 0.");
                    return;
                }
                // Apply selection
                selectDuration(total, btnCustom);
                dialog.close();
            } catch (NumberFormatException ex) {
                errorLabel.setText("Please enter valid numbers.");
            }
        });

        cancel.setOnAction(e -> dialog.close());

        Scene scene = new Scene(vbox);
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    private void startSession() {
        if (originalDurationSeconds <= 0) {
            return;
        }

        // Reset flags
        remainingSeconds = originalDurationSeconds;
        thirtyPopupShown = false;

        // Clean up any previous timeline
        if (focusTimeline != null) {
            focusTimeline.stop();
        }

        focusTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> onFocusTick()));
        focusTimeline.setCycleCount(Timeline.INDEFINITE);
        focusTimeline.play();

        statusLabel.setText("You're in focus mode! You've got this!");
        timerLabel.setText(formatTime(remainingSeconds));

        showRunningUI();
    }

    private void onFocusTick() {
        remainingSeconds--;
        timerLabel.setText(formatTime(remainingSeconds));

        int elapsed = originalDurationSeconds - remainingSeconds;
        // If total session is more than 30 minutes, pop once at 30 elapsed
        if (!thirtyPopupShown && originalDurationSeconds > 30 * 60 && elapsed >= 30 * 60) {
            thirtyPopupShown = true;
            showThirtyMinutePopup();
        }

        if (remainingSeconds <= 0) {
            focusTimeline.stop();
            onSessionComplete();
        }
    }

    private void pauseSession() {
        if (focusTimeline != null) {
            focusTimeline.pause();
        }
        statusLabel.setText("Paused. Take a breath, then resume.");
        showPausedUI();
    }

    private void resumeSession() {
        if (focusTimeline != null) {
            focusTimeline.play();
        }
        statusLabel.setText("You're in focus mode! You've got this!");
        showRunningUI();
    }

    private void abortSession() {
        if (focusTimeline != null) {
            focusTimeline.stop();
        }
        remainingSeconds = 0;
        statusLabel.setText("Session aborted. Choose a session length to start again.");
        timerLabel.setText("00:00");
        originalDurationSeconds = 0;
        showPreSessionUI();
        clearSelectionStyles();
    }

    private void onSessionComplete() {
        // Log activity + streak
        activityLog.recordToday(ActivityType.FOCUS);
        streakTracker.completeToday();

        String duration = formatTime(originalDurationSeconds);
        statusLabel.setText("Great job! " + duration + " completed successfully.");
        timerLabel.setText("00:00");

        // Reset selection so next run requires choosing length again
        originalDurationSeconds = 0;
        remainingSeconds = 0;
        clearSelectionStyles();
        startButton.setDisable(true);

        // Show "Quick Journal" button after completion
        // quickJournalAfterButton.setVisible(true);
        // closeButton.setVisible(true);

        showCompletedUI();
    }

    // --------- 30-minute popup + break window ---------

    private void showThirtyMinutePopup() {
        Stage popup = new Stage();
        popup.initOwner(stage);
        popup.initModality(Modality.NONE);
        popup.setTitle("Great job!");

        Label msg = new Label("Great job! 30 minutes done \uD83D\uDE0A");
        msg.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        Button breakBtn = new Button("5 minute break");
        breakBtn.setStyle("-fx-background-color: #5dade2; -fx-text-fill: white; -fx-font-weight: bold;");

        Button quickJournalBtn = new Button("Quick Journal");
        quickJournalBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: black; -fx-font-weight: bold;");

        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");

        breakBtn.setOnAction(e -> {
            popup.close();
            startFiveMinuteBreakWindow();
        });

        quickJournalBtn.setOnAction(e -> JournalGUI.showJournalWindow());

        closeBtn.setOnAction(e -> popup.close());

        HBox buttonsRow = new HBox(10, breakBtn, quickJournalBtn, closeBtn);
        buttonsRow.setAlignment(Pos.CENTER);

        VBox vbox = new VBox(10, msg, buttonsRow);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(12));

        popup.setScene(new Scene(vbox));
        popup.show();
    }

    private void startFiveMinuteBreakWindow() {
        // Pause the focus timer while on break
        if (focusTimeline != null) {
            focusTimeline.pause();
        }

        Stage breakStage = new Stage();
        breakStage.initOwner(stage);
        breakStage.initModality(Modality.WINDOW_MODAL);
        breakStage.setTitle("5 minute break");

        Label breakLabel = new Label("Break: 05:00");
        breakLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        VBox vbox = new VBox(10, new Label("Enjoy your short break!"), breakLabel);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(16));

        Scene scene = new Scene(vbox, 260, 140);
        breakStage.setScene(scene);
        breakStage.show();

        final int[] breakSeconds = {5 * 60};

        Timeline breakTimeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            breakSeconds[0]--;
            breakLabel.setText("Break: " + formatTime(breakSeconds[0]));
            if (breakSeconds[0] <= 0) {
                breakStage.close();
                // Resume main focus session
                if (focusTimeline != null) {
                    focusTimeline.play();
                }
            }
        }));
        breakTimeline.setCycleCount(5 * 60);
        breakTimeline.play();
    }

    // --------- UI visibility helpers ---------

    private void showPreSessionUI() {
        backButton.setVisible(true);
        backButton.setManaged(true);

        startButton.setVisible(true);
        startButton.setManaged(true);
        startButton.setDisable(originalDurationSeconds <= 0);

        btn15.setVisible(true);
        btn15.setManaged(true);

        btn30.setVisible(true);
        btn30.setManaged(true);

        btn45.setVisible(true);
        btn45.setManaged(true);

        btnCustom.setVisible(true);
        btnCustom.setManaged(true);

        // pauseButton.setVisible(false);
        // resumeButton.setVisible(false);
        // abortButton.setVisible(false);
        //controlRow.getChildren().setAll(pauseButton, abortButton);
        controlRow.setVisible(false);
        controlRow.setManaged(false);

        quickJournalAfterButton.setVisible(false);
        quickJournalAfterButton.setManaged(false);

        closeButton.setVisible(false);
        closeButton.setManaged(false);
    }

    private void showRunningUI() {
        backButton.setVisible(false);
        backButton.setManaged(false);

        btn15.setVisible(false);
        btn15.setManaged(false);

        btn30.setVisible(false);
        btn30.setManaged(false);

        btn45.setVisible(false);
        btn45.setManaged(false);
        
        btnCustom.setVisible(false);
        btnCustom.setManaged(false);
        
        startButton.setVisible(false);
        startButton.setManaged(false);

        // pauseButton.setVisible(true);
        // abortButton.setVisible(true);
        controlRow.getChildren().setAll(pauseButton, abortButton);
        controlRow.setVisible(true);
        controlRow.setManaged(true);

        quickJournalAfterButton.setVisible(false);
        quickJournalAfterButton.setManaged(false);

        closeButton.setVisible(false);
        closeButton.setManaged(false);
    }

    private void showPausedUI() {
        btn15.setVisible(false);
        btn15.setManaged(false);

        btn30.setVisible(false);
        btn30.setManaged(false);

        btn45.setVisible(false);
        btn45.setManaged(false);
        
        btnCustom.setVisible(false);
        btnCustom.setManaged(false);

        startButton.setVisible(false);
        startButton.setManaged(false);;

        // resumeButton.setVisible(true);
        // abortButton.setVisible(true);
        controlRow.getChildren().setAll(resumeButton, abortButton);
        controlRow.setVisible(true);
        controlRow.setManaged(true);

        quickJournalAfterButton.setVisible(false);
        quickJournalAfterButton.setManaged(false);

        closeButton.setVisible(false);
        closeButton.setManaged(false);
    }

    private void showCompletedUI() {

        backButton.setVisible(false);
        backButton.setManaged(false);

        btn15.setVisible(false);
        btn15.setManaged(false);

        btn30.setVisible(false);
        btn30.setManaged(false);


        btn45.setVisible(false);
        btn45.setManaged(false);

        btnCustom.setVisible(false);
        btnCustom.setManaged(false);


        startButton.setVisible(false);
        startButton.setManaged(false);

        pauseButton.setVisible(false);
        pauseButton.setManaged(false);

        resumeButton.setVisible(false);
        resumeButton.setManaged(false);

        abortButton.setVisible(false);
        abortButton.setManaged(false);

        // Quick Journal button visibility controlled separately
        quickJournalAfterButton.setVisible(true);
        quickJournalAfterButton.setManaged(true);

        closeButton.setVisible(true);
        closeButton.setManaged(true);
    }

    // --------- Utility ---------

    private String formatTime(int totalSeconds) {
        if (totalSeconds < 0) totalSeconds = 0;
        int h = totalSeconds / 3600;
        int m = (totalSeconds % 3600) / 60;
        int s = totalSeconds % 60;

        if (h > 0) {
            return String.format("%d:%02d:%02d", h, m, s);
        } else {
            return String.format("%02d:%02d", m, s);
        }
    }
}