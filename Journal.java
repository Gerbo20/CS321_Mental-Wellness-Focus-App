import java.util.*;

public class Journal {
    private final List<JournalEntry> entries = new ArrayList<>();
    private int count = 0;

    public void addEntry(JournalEntry entry) {
        entries.add(entry);
        count++;
        System.out.println("Entry Added\n");
    }

    public void viewEntries() {
        if (entries.isEmpty()) {
            System.out.println("No Entries Found");
            return;
        }

        int entryNum = 1;
        for (JournalEntry entry : entries) {
            System.out.println("Entry " + entryNum++);
            System.out.println(entry);
        }
        System.out.println("Total Entries: " + count);
    }

    public void deleteEntry(int index) {
        index -= 1;
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
            count--;
            System.out.println("Entry Deleted");
        } else {
            System.out.println("Invalid Option\n");
        }
    }

    public List<JournalEntry> getEntries() {
        return entries;
    }
}