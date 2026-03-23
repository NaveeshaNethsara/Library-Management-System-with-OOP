package library;

import library.exception.LibraryException;
import library.model.Book;
import library.model.BorrowRecord;
import library.model.Member;
import library.service.Library;

import java.util.List;
import java.util.Scanner;

/**
 * Entry point for the Library Management System.
 * Provides an interactive command-line interface.
 */
public class Main {

    private static final Library library = new Library("City Public Library");
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        seedSampleData();
        System.out.println("===========================================");
        System.out.println("  Welcome to " + library.getName());
        System.out.println("===========================================");
        boolean running = true;
        while (running) {
            printMainMenu();
            String choice = scanner.nextLine().trim();
            switch (choice) {
                case "1" -> manageBooks();
                case "2" -> manageMembers();
                case "3" -> manageBorrowing();
                case "4" -> viewReports();
                case "0" -> {
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    // -------------------------------------------------------------------------
    // Menus
    // -------------------------------------------------------------------------

    private static void printMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Manage Books");
        System.out.println("2. Manage Members");
        System.out.println("3. Borrow / Return Books");
        System.out.println("4. View Reports");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void manageBooks() {
        System.out.println("\n--- Manage Books ---");
        System.out.println("1. Add Book");
        System.out.println("2. Remove Book");
        System.out.println("3. Search Books by Title");
        System.out.println("4. Search Books by Author");
        System.out.println("5. List All Books");
        System.out.println("6. List Available Books");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> addBook();
            case "2" -> removeBook();
            case "3" -> searchBooksByTitle();
            case "4" -> searchBooksByAuthor();
            case "5" -> listAllBooks();
            case "6" -> listAvailableBooks();
            default -> System.out.println("Invalid option.");
        }
    }

    private static void manageMembers() {
        System.out.println("\n--- Manage Members ---");
        System.out.println("1. Register Member");
        System.out.println("2. Remove Member");
        System.out.println("3. Search Members by Name");
        System.out.println("4. List All Members");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> addMember();
            case "2" -> removeMember();
            case "3" -> searchMembersByName();
            case "4" -> listAllMembers();
            default -> System.out.println("Invalid option.");
        }
    }

    private static void manageBorrowing() {
        System.out.println("\n--- Borrow / Return ---");
        System.out.println("1. Borrow a Book");
        System.out.println("2. Return a Book");
        System.out.println("3. View Active Borrows");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> borrowBook();
            case "2" -> returnBook();
            case "3" -> viewActiveBorrows();
            default -> System.out.println("Invalid option.");
        }
    }

    private static void viewReports() {
        System.out.println("\n--- Reports ---");
        System.out.println("1. All Borrow History");
        System.out.println("2. Overdue Books");
        System.out.print("Choose an option: ");
        String choice = scanner.nextLine().trim();
        switch (choice) {
            case "1" -> viewAllBorrowHistory();
            case "2" -> viewOverdueBooks();
            default -> System.out.println("Invalid option.");
        }
    }

    // -------------------------------------------------------------------------
    // Book operations
    // -------------------------------------------------------------------------

    private static void addBook() {
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Author: ");
        String author = scanner.nextLine().trim();
        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();
        System.out.print("Publication Year: ");
        int year = parseIntOrDefault(scanner.nextLine().trim(), 0);
        try {
            library.addBook(new Book(isbn, title, author, genre, year));
            System.out.println("Book added successfully.");
        } catch (LibraryException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeBook() {
        System.out.print("Enter ISBN of the book to remove: ");
        String isbn = scanner.nextLine().trim();
        try {
            library.removeBook(isbn);
            System.out.println("Book removed successfully.");
        } catch (LibraryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchBooksByTitle() {
        System.out.print("Enter title to search: ");
        String title = scanner.nextLine().trim();
        List<Book> results = library.searchBooksByTitle(title);
        printBooks(results);
    }

    private static void searchBooksByAuthor() {
        System.out.print("Enter author to search: ");
        String author = scanner.nextLine().trim();
        List<Book> results = library.searchBooksByAuthor(author);
        printBooks(results);
    }

    private static void listAllBooks() {
        printBooks(library.getAllBooks());
    }

    private static void listAvailableBooks() {
        printBooks(library.getAvailableBooks());
    }

    // -------------------------------------------------------------------------
    // Member operations
    // -------------------------------------------------------------------------

    private static void addMember() {
        System.out.print("Member ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Phone: ");
        String phone = scanner.nextLine().trim();
        try {
            library.addMember(new Member(id, name, email, phone));
            System.out.println("Member registered successfully.");
        } catch (LibraryException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void removeMember() {
        System.out.print("Enter Member ID to remove: ");
        String id = scanner.nextLine().trim();
        try {
            library.removeMember(id);
            System.out.println("Member removed successfully.");
        } catch (LibraryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchMembersByName() {
        System.out.print("Enter name to search: ");
        String name = scanner.nextLine().trim();
        List<Member> results = library.searchMembersByName(name);
        printMembers(results);
    }

    private static void listAllMembers() {
        printMembers(library.getAllMembers());
    }

    // -------------------------------------------------------------------------
    // Borrowing operations
    // -------------------------------------------------------------------------

    private static void borrowBook() {
        System.out.print("ISBN of book to borrow: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        try {
            BorrowRecord record = library.borrowBook(isbn, memberId);
            System.out.println("Book borrowed successfully. Due date: " + record.getDueDate());
        } catch (LibraryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.print("ISBN of book to return: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Member ID: ");
        String memberId = scanner.nextLine().trim();
        try {
            BorrowRecord record = library.returnBook(isbn, memberId);
            System.out.println("Book returned successfully." + (record.isOverdue() ? " Note: This book was overdue." : ""));
        } catch (LibraryException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void viewActiveBorrows() {
        List<BorrowRecord> records = library.getActiveBorrowRecords();
        if (records.isEmpty()) {
            System.out.println("No active borrows.");
        } else {
            records.forEach(r -> System.out.println("  " + r));
        }
    }

    // -------------------------------------------------------------------------
    // Reports
    // -------------------------------------------------------------------------

    private static void viewAllBorrowHistory() {
        List<BorrowRecord> records = library.getAllBorrowRecords();
        if (records.isEmpty()) {
            System.out.println("No borrow history.");
        } else {
            records.forEach(r -> System.out.println("  " + r));
        }
    }

    private static void viewOverdueBooks() {
        List<BorrowRecord> records = library.getOverdueBorrowRecords();
        if (records.isEmpty()) {
            System.out.println("No overdue books.");
        } else {
            records.forEach(r -> System.out.println("  " + r));
        }
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            System.out.println("No books found.");
        } else {
            books.forEach(b -> System.out.println("  " + b));
        }
    }

    private static void printMembers(List<Member> members) {
        if (members.isEmpty()) {
            System.out.println("No members found.");
        } else {
            members.forEach(m -> System.out.println("  " + m));
        }
    }

    private static int parseIntOrDefault(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private static void seedSampleData() {
        try {
            library.addBook(new Book("978-0-06-112008-4", "To Kill a Mockingbird", "Harper Lee", "Fiction", 1960));
            library.addBook(new Book("978-0-7432-7356-5", "1984", "George Orwell", "Dystopian", 1949));
            library.addBook(new Book("978-0-14-028329-7", "The Great Gatsby", "F. Scott Fitzgerald", "Fiction", 1925));
            library.addBook(new Book("978-0-316-76948-0", "The Catcher in the Rye", "J.D. Salinger", "Fiction", 1951));
            library.addBook(new Book("978-0-7432-7357-2", "Brave New World", "Aldous Huxley", "Dystopian", 1932));

            library.addMember(new Member("M001", "Alice Johnson", "alice@example.com", "555-1001"));
            library.addMember(new Member("M002", "Bob Smith", "bob@example.com", "555-1002"));
            library.addMember(new Member("M003", "Carol White", "carol@example.com", "555-1003"));
        } catch (LibraryException e) {
            System.err.println("Error seeding sample data: " + e.getMessage());
        }
    }
}
