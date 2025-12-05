package org.example.mentalwellnessfocusapp;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class BreathingCircle extends StackPane {

    private final Circle outerCircle;
    private final Circle innerCircle;
    private final Text phaseText;

    private final double minInnerRadius;
    private final double maxInnerRadius;

    private SequentialTransition breathingSequence;

    public BreathingCircle(double minInnerRadius, double maxInnerRadius) {
        this.minInnerRadius = minInnerRadius;
        this.maxInnerRadius = maxInnerRadius;

        // Outer circle as soft border
        outerCircle = new Circle(maxInnerRadius + 18);
        outerCircle.setFill(Color.web("#e8f5ff"));
        outerCircle.setStroke(Color.web("#5ab4f5"));
        outerCircle.setStrokeWidth(3);

        // Inner circle that actually grows/shrinks
        innerCircle = new Circle(minInnerRadius);
        innerCircle.setFill(Color.web("#5ab4f5"));
        innerCircle.setStroke(Color.web("#3b87c6"));
        innerCircle.setStrokeWidth(2);

        // Text in the middle ("Inhale", "Exhale", etc.)
        phaseText = new Text("Ready");
        phaseText.setFill(Color.web("#ffffff"));
        phaseText.setFont(Font.font("System", 18));

        setAlignment(Pos.CENTER);
        getChildren().addAll(outerCircle, innerCircle, phaseText);

        double size = (maxInnerRadius + 40) * 2;
        setMinSize(size, size);
        setMaxSize(size, size);
    }

    /**
     * Start the breathing animation with an inhale, optional hold, and exhale.
     */
    public void startBreathing(Duration inhaleDuration,
                               Duration holdDuration,
                               Duration exhaleDuration) {
        stopBreathing();

        // Inhale animation
        Timeline inhale = createRadiusAnimation(
                innerCircle,
                minInnerRadius,
                maxInnerRadius,
                inhaleDuration
        );

        // Hold at max radius (no radius change, just "wait")
        Timeline hold = new Timeline(new KeyFrame(
                holdDuration,
                new KeyValue(
                        innerCircle.radiusProperty(),
                        maxInnerRadius,
                        Interpolator.DISCRETE)
        ));

        // Exhale animation
        Timeline exhale = createRadiusAnimation(
                innerCircle,
                maxInnerRadius,
                minInnerRadius,
                exhaleDuration
        );

        // If holdDuration is 0, we can skip the hold Timeline
        if (holdDuration.greaterThan(Duration.ZERO)) {
            breathingSequence = new SequentialTransition(inhale, hold, exhale);
        } else {
            breathingSequence = new SequentialTransition(inhale, exhale);
        }

        breathingSequence.setCycleCount(Animation.INDEFINITE);
        breathingSequence.play();
    }

    /**
     * Convenience overload: inhale + exhale only.
     */
    public void startBreathing(Duration inhaleDuration, Duration exhaleDuration) {
        startBreathing(inhaleDuration, Duration.ZERO, exhaleDuration);
    }

    /**
     * Stop the breathing animation completely.
     */
    public void stopBreathing() {
        if (breathingSequence != null) {
            breathingSequence.stop();
        }
    }

    /**
     * Pause the breathing animation (can be resumed).
     */
    public void pauseBreathing() {
        if (breathingSequence != null) {
            breathingSequence.pause();
        }
    }

    /**
     * Resume a paused breathing animation.
     */
    public void resumeBreathing() {
        if (breathingSequence != null) {
            breathingSequence.play();
        }
    }

    /**
     * Reset the circle to its initial state.
     */
    public void resetBreathing() {
        stopBreathing();
        innerCircle.setRadius(minInnerRadius);
    }

    private Timeline createRadiusAnimation(Circle circle,
                                           double fromRadius,
                                           double toRadius,
                                           Duration duration) {
        Timeline timeline = new Timeline();

        KeyValue startKV = new KeyValue(
                circle.radiusProperty(),
                fromRadius,
                Interpolator.EASE_BOTH
        );
        KeyValue endKV = new KeyValue(
                circle.radiusProperty(),
                toRadius,
                Interpolator.EASE_BOTH
        );

        KeyFrame startKF = new KeyFrame(Duration.ZERO, startKV);
        KeyFrame endKF = new KeyFrame(duration, endKV);

        timeline.getKeyFrames().addAll(startKF, endKF);
        return timeline;
    }

    /** Let the UI set "Inhale" / "Exhale" / "Hold" / etc. */
    public void setPhaseText(String text) {
        phaseText.setText(text);
    }

    /** Allow the GUI to read the current phase text. */
    public String getPhaseText() {
        return phaseText.getText();
    }
}
