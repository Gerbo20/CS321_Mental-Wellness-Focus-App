import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        Journal journal = new Journal();
        boolean run = true;
        
        // Menu for Journal Entries
        while (run) {
            System.out.println("|==={ Wellness Journal }===|");
            System.out.println("[1] Add Entry");
            System.out.println("[2] View Entries");
            System.out.println("[3] Delete Entry");
            System.out.println("[4] Exit\n");
            System.out.print("Enter Your Choice: ");

            int choice = scan.nextInt();
            scan.nextLine();

            switch (choice) {
                // Choice 1 lets users create an entry
                case 1:
                    System.out.println();
                    System.out.print("Enter Entry: ");
                    String entry = scan.nextLine();
                    journal.addEntry(new JournalEntry(entry));
                    System.out.println();
                    break;
                // Choice 2 lets users see all entries
                case 2:
                    System.out.println();
                    journal.viewEntries();
                    System.out.println();
                    break;
                // Choice 3 lets users delete an entry
                case 3:
                    if (journal.getEntries().isEmpty()) {
                        System.out.println();
                        System.out.print("No Entries Available");
                        System.out.println("\n");
                        break;
                    } else {
                        journal.viewEntries();
                        System.out.print("Select An Entry To Delete: ");
                        int index = scan.nextInt();
                        journal.deleteEntry(index);
                        System.out.println();
                        break;
                    }
                // Choice 4 to exit
                case 4:
                    run = false;
                    break;
                default:
                    System.out.println("Invalid Option\n");
                    break;
            }
        }
        scan.close();
    }
}