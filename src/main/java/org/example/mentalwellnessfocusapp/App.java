package org.example.mentalwellnessfocusapp;

import java.util.List;

public class App {
    private final Journal journal;

    public App() {
        journal = new Journal();
    }

    /** Adds a journal entry.
     * @param entryText The text of the entry
     * @return true if added successfully, false if empty
     */
    public boolean addEntry(String title, String mood, String entry) {
        if (entry == null || entry.trim().isEmpty()) {
            return false;
        }
        return journal.addEntry(new JournalEntry(title, mood, entry));
    }

    // Returns a copy of all journal entries
    public List<JournalEntry> getEntries() {
        return journal.getEntries();
    }

    /** Deletes an entry by index.
     * @param index Index of the entry to delete
     * @return true if deleted successfully
     */
    public boolean deleteEntry(int index) {
        return journal.deleteEntry(index);
    }

    // Returns number of entries in the journal
    public int getEntryCount() {
        return journal.getEntryCount();
    }

    // Console menu for testing without GUI
    public void runConsole() {
        java.util.Scanner scan = new java.util.Scanner(System.in);
        boolean run = true;

        while (run) {
            System.out.println("|==={ Wellness Journal }===|");
            System.out.println("[1] Add Entry");
            System.out.println("[2] View Entries");
            System.out.println("[3] Delete Entry");
            System.out.println("[4] Exit");
            System.out.println("|==========================|");
            System.out.print("Enter Your Choice: ");

            int choice;
            try {
                choice = scan.nextInt();
                scan.nextLine(); // consume newline
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid Entry\n");
                scan.nextLine();
                continue;
            }

            switch (choice) {
                case 1:
                    System.out.print("Enter Title: ");
                    String title = scan.nextLine().trim();

                    System.out.print("Enter Mood: ");
                    String mood = scan.nextLine().trim();

                    System.out.print("Enter Entry: ");
                    String entry = scan.nextLine().trim();

                    if (!addEntry(title, mood, entry)) {
                        System.out.println("Entry Cannot Be Empty\n");
                    } else {
                        System.out.println("Entry Added!\n");
                    }
                    break;
                case 2:
                    List<JournalEntry> entries = getEntries();
                    if (entries.isEmpty()) {
                        System.out.println("No Entries Found\n");
                    } else {
                        int num = 1;
                        System.out.println();
                        for (JournalEntry e : entries) {
                            System.out.println("Entry " + num++ + "\n" + e);
                        }
                        System.out.println("Total Entries: " + getEntryCount() + "\n");
                    }
                    break;
                case 3:
                    entries = getEntries();
                    if (entries.isEmpty()) {
                        System.out.println("No Entries Available\n");
                        break;
                    }
                    int i = 1;
                    for (JournalEntry e : entries) {
                        System.out.println("Entry " + i++ + ": " + e.getEntry() + "\n");
                    }
                    System.out.print("Select an Entry to Delete: ");
                    try {
                        int index = scan.nextInt() - 1;
                        if (deleteEntry(index)) {
                            System.out.println("Entry Deleted!\n");
                        } else {
                            System.out.println("Invalid Entry\n");
                        }
                    } catch (java.util.InputMismatchException e) {
                        System.out.println("Invalid Entry\n");
                        scan.nextLine();
                    }
                    break;
                case 4:
                    run = false;
                    break;
                default:
                    System.out.println("Invalid Option\n");
            }
        }
        scan.close();
    }

    public static void main(String[] args) {
        App app = new App();
        app.runConsole();
    }
}
