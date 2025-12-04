package org.example.mentalwellnessfocusapp;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ActivityLog {
    
    // Keep days in order (oldest → newest)
    private final Map<LocalDate, DayActivity> days = new LinkedHashMap<>();

    private DayActivity getOrCreate(LocalDate date) {
        return days.computeIfAbsent(date, DayActivity::new);
    }
    
    // DataStore can insert fully-built DayActivity <<<
    void putDay(DayActivity day) {
        days.put(day.getDate(), day);
    }

    public void recordToday(ActivityType type) {
        LocalDate today = LocalDate.now();
        DayActivity day = getOrCreate(today);
        switch (type) {
            case FOCUS -> day.setDidFocus(true);
            case BREATHING -> day.setDidBreathing(true);
            case JOURNAL -> day.setDidJournal(true);
        }
    }

    /** Last N days (or fewer if not enough data). */
    public List<DayActivity> getLastNDays(int n) {
        List<DayActivity> all = new ArrayList<>(days.values());
        if (all.size() <= n) {
            return all;
        }
        return all.subList(all.size() - n, all.size());
    }

    public List<DayActivity> getAllDays() {
        return Collections.unmodifiableList(new ArrayList<>(days.values()));
    }
}
