import java.util.*;

public class Journal {

    private final List<JournalEntry> entries = new ArrayList<>();

    // Method to add entries
    public boolean addEntry(JournalEntry entry) {
        if (entry == null || entry.getEntry().trim().isEmpty()) {
            return false;
        }

        entries.add(entry);
        return true;
    }

    // Returns list of all entries
    public List<JournalEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    // Method to delete an entry
    public boolean deleteEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
            return true;
        }
        return false;
    }

    public int getEntryCount() {
        return entries.size();
    }
}
