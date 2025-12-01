package org.example.mentalwellnessfocusapp;

import java.util.Random;

public class MessageBank {
    private static final String[] STREAK_SUCCESS = {
            "🔥 Day %d! Your consistency is becoming strength.",
            "You kept your streak alive – that’s discipline in action. (Day %d)",
            "%d day(s) of choosing yourself. Keep going! 🌱",
            "Momentum unlocked! You're building something powerful. (Day %d)"
    };

    private static final String[] STREAK_WARNING = {
            "Your streak is waiting 👀 Just a few minutes today!",
            "Don’t let the chain break — you’re almost there.",
            "Small steps count. Complete today to keep your streak 🔗"
    };

    private static final String[] STREAK_BROKEN = {
            "It’s okay. Let’s reset and begin again today 🌤️",
            "No guilt — just a fresh start.",
            "Progress isn’t perfect. Try again — you’re capable."
    };

    private static final String[] MORNING_MOTIVATION = {
            "Good morning ☀️ Take a deep breath — today starts now.",
            "Soft start, strong finish. You’ve got this.",
            "Today is another chance to care for yourself."
    };

    private static final Random rand = new Random();

    public static String getRandomStreakSuccess(int streak) {
        String template = STREAK_SUCCESS[rand.nextInt(STREAK_SUCCESS.length)];
        return String.format(template, streak);
    }

    public static String getRandomWarning() {
        return STREAK_WARNING[rand.nextInt(STREAK_WARNING.length)];
    }

    public static String getRandomBroken() {
        return STREAK_BROKEN[rand.nextInt(STREAK_BROKEN.length)];
    }

    public static String getMorningMotivation() {
        return MORNING_MOTIVATION[rand.nextInt(MORNING_MOTIVATION.length)];
    }
}
