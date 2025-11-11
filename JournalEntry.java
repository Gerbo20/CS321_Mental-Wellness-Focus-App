import java.time.*;

public class JournalEntry {
    private final LocalDate date;
    private final String entry;
    
    public JournalEntry(String entry) {
        this.date = LocalDate.now();
        this.entry = entry;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getEntry() {
        return entry;
    }

    @Override
    public String toString() {
        return "{" + getDate() +"}\n" + "==============================\n\n" + entry + "\n\n==============================\n";
    }
}