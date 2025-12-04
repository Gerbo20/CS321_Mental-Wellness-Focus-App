package org.example.mentalwellnessfocusapp;

import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ProgressStreaksGUI {
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("MMM d");

    public static void show(StreakTracker tracker, ActivityLog log) {
        Stage stage = new Stage();
        stage.setTitle("Progress Streaks");

        // --- Back button ---
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> stage.close());
        backButton.setStyle(
                "-fx-background-color: linear-gradient(#5ab4f5, #368de8);" +
                "-fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 6; -fx-padding: 4 12;"
        );
        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

        // --- Streak summary label ---
        Label summary = new Label();
        summary.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        int streak = tracker.getStreak();
        if (streak == 0) {
            summary.setText("You haven’t started a streak yet. 🌱");
        } else {
            summary.setText("Current streak: " + streak +
                    " day" + (streak == 1 ? "" : "s") + " in a row 🔥");
        }

        // --- TABLE: daily activity ---
    
        TableView<DayActivity> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        TableColumn<DayActivity, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getDate().format(DATE_FMT)));

        TableColumn<DayActivity, String> focusCol = new TableColumn<>("Focus");
        focusCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isDidFocus() ? "✓" : ""));

        TableColumn<DayActivity, String> breathCol = new TableColumn<>("Breathing");
        breathCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isDidBreathing() ? "✓" : ""));

        TableColumn<DayActivity, String> journalCol = new TableColumn<>("Journal");
        journalCol.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().isDidJournal() ? "✓" : ""));

        TableColumn<DayActivity, Number> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(c ->
                new SimpleIntegerProperty(c.getValue().getScore()));

        // table.getColumns().addAll(java.util.List.of(dateCol, focusCol, breathCol, journalCol, scoreCol));

        // Add columns one-by-one (avoids generic-array warnings)
        table.getColumns().add(dateCol);
        table.getColumns().add(focusCol);
        table.getColumns().add(breathCol);
        table.getColumns().add(journalCol);
        table.getColumns().add(scoreCol);

        List<DayActivity> allDays = log.getAllDays();
        table.getItems().setAll(allDays);
        table.setPrefHeight(150);   // ≈ “show about 3–4 rows, then scroll”
        VBox.setVgrow(table, Priority.NEVER);

        // --- WELLNESS WAVE: stacked bar chart ---
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Day");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Activity Level");

        // StackedBarChart<String, Number> chart = new StackedBarChart<>(xAxis, yAxis);
        
        // Change from StackedBarChart to BarChart
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);

        chart.setLegendVisible(true);
        chart.setCategoryGap(15);
        chart.setAnimated(false);

        chart.setTitle("Wellness Wave (Last 7 Days)");
        
        XYChart.Series<String, Number> focusSeries = new XYChart.Series<>();
        focusSeries.setName("Focus Session");

        XYChart.Series<String, Number> breathingSeries = new XYChart.Series<>();
        breathingSeries.setName("Breathing");

        XYChart.Series<String, Number> journalSeries = new XYChart.Series<>();
        journalSeries.setName("Journal");

        List<DayActivity> last7 = log.getLastNDays(7);
        for (DayActivity day : last7) {
            String label = day.getDate()
                    .getDayOfWeek()
                    .getDisplayName(TextStyle.SHORT, Locale.getDefault());

            focusSeries.getData().add(new XYChart.Data<>(
                    label, day.isDidFocus() ? 1 : 0));
            breathingSeries.getData().add(new XYChart.Data<>(
                    label, day.isDidBreathing() ? 1 : 0));
            journalSeries.getData().add(new XYChart.Data<>(
                    label, day.isDidJournal() ? 1 : 0));
        }

        // chart.getData().addAll(java.util.List.of(focusSeries, breathingSeries, journalSeries));
        
        // Add series one-by-one (avoids generic-array warnings)
        chart.getData().add(focusSeries);
        chart.getData().add(breathingSeries);
        chart.getData().add(journalSeries);
        
        chart.setPrefHeight(220);

        VBox content = new VBox(10, summary, chart, table);
        content.setPadding(new Insets(10));

        BorderPane root = new BorderPane();
        root.setTop(topBar);
        root.setCenter(content);
        root.setStyle("-fx-background-color: #f5f5f5;");

        Scene scene = new Scene(root, 360, 640);
        stage.setScene(scene);
        stage.show();
    }
}
