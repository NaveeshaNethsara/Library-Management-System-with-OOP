package library.service;

import library.exception.LibraryException;
import library.model.Book;
import library.model.BorrowRecord;
import library.model.Member;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Core service class that manages the library's books, members, and borrowing operations.
 * Demonstrates OOP principles: encapsulation, abstraction, and single responsibility.
 */
public class Library {

    private static final int MAX_BORROW_LIMIT = 3;

    private final String name;
    private final Map<String, Book> books;       // keyed by ISBN
    private final Map<String, Member> members;   // keyed by member ID
    private final List<BorrowRecord> borrowRecords;

    public Library(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Library name cannot be null or blank.");
        }
        this.name = name;
        this.books = new HashMap<>();
        this.members = new HashMap<>();
        this.borrowRecords = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    // -------------------------------------------------------------------------
    // Book management
    // -------------------------------------------------------------------------

    /**
     * Adds a new book to the library.
     *
     * @throws LibraryException if a book with the same ISBN already exists.
     */
    public void addBook(Book book) throws LibraryException {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (books.containsKey(book.getIsbn())) {
            throw new LibraryException("A book with ISBN '" + book.getIsbn() + "' already exists.");
        }
        books.put(book.getIsbn(), book);
    }

    /**
     * Removes a book from the library by ISBN.
     *
     * @throws LibraryException if the book is not found or is currently borrowed.
     */
    public void removeBook(String isbn) throws LibraryException {
        Book book = getBookByIsbn(isbn);
        if (!book.isAvailable()) {
            throw new LibraryException("Cannot remove book '" + book.getTitle() + "' — it is currently borrowed.");
        }
        books.remove(isbn);
    }

    /**
     * Returns a book by ISBN.
     *
     * @throws LibraryException if the book is not found.
     */
    public Book getBookByIsbn(String isbn) throws LibraryException {
        Book book = books.get(isbn);
        if (book == null) {
            throw new LibraryException("No book found with ISBN '" + isbn + "'.");
        }
        return book;
    }

    /**
     * Searches books by title (case-insensitive, partial match).
     */
    public List<Book> searchBooksByTitle(String title) {
        if (title == null || title.isBlank()) {
            return getAllBooks();
        }
        String lower = title.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getTitle().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * Searches books by author (case-insensitive, partial match).
     */
    public List<Book> searchBooksByAuthor(String author) {
        if (author == null || author.isBlank()) {
            return getAllBooks();
        }
        String lower = author.toLowerCase();
        return books.values().stream()
                .filter(b -> b.getAuthor().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * Returns all books in the library.
     */
    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    /**
     * Returns all available (not borrowed) books.
     */
    public List<Book> getAvailableBooks() {
        return books.values().stream()
                .filter(Book::isAvailable)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Member management
    // -------------------------------------------------------------------------

    /**
     * Registers a new member.
     *
     * @throws LibraryException if a member with the same ID already exists.
     */
    public void addMember(Member member) throws LibraryException {
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }
        if (members.containsKey(member.getMemberId())) {
            throw new LibraryException("A member with ID '" + member.getMemberId() + "' already exists.");
        }
        members.put(member.getMemberId(), member);
    }

    /**
     * Removes a member from the library.
     *
     * @throws LibraryException if the member is not found or has unreturned books.
     */
    public void removeMember(String memberId) throws LibraryException {
        Member member = getMemberById(memberId);
        if (member.getCurrentBorrowCount() > 0) {
            throw new LibraryException("Cannot remove member '" + member.getName()
                    + "' — they have unreturned books.");
        }
        members.remove(memberId);
    }

    /**
     * Returns a member by ID.
     *
     * @throws LibraryException if the member is not found.
     */
    public Member getMemberById(String memberId) throws LibraryException {
        Member member = members.get(memberId);
        if (member == null) {
            throw new LibraryException("No member found with ID '" + memberId + "'.");
        }
        return member;
    }

    /**
     * Searches members by name (case-insensitive, partial match).
     */
    public List<Member> searchMembersByName(String name) {
        if (name == null || name.isBlank()) {
            return getAllMembers();
        }
        String lower = name.toLowerCase();
        return members.values().stream()
                .filter(m -> m.getName().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    /**
     * Returns all registered members.
     */
    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    // -------------------------------------------------------------------------
    // Borrowing and returning
    // -------------------------------------------------------------------------

    /**
     * Borrows a book for a member.
     *
     * @return the created BorrowRecord.
     * @throws LibraryException if the book/member is not found, the book is unavailable,
     *                          or the member has reached the borrow limit.
     */
    public BorrowRecord borrowBook(String isbn, String memberId) throws LibraryException {
        Book book = getBookByIsbn(isbn);
        Member member = getMemberById(memberId);

        if (!book.isAvailable()) {
            throw new LibraryException("Book '" + book.getTitle() + "' is currently not available.");
        }
        if (member.getCurrentBorrowCount() >= MAX_BORROW_LIMIT) {
            throw new LibraryException("Member '" + member.getName()
                    + "' has reached the maximum borrow limit of " + MAX_BORROW_LIMIT + " books.");
        }

        book.setAvailable(false);
        BorrowRecord record = new BorrowRecord(book, member);
        member.addBorrowRecord(record);
        borrowRecords.add(record);
        return record;
    }

    /**
     * Returns a borrowed book.
     *
     * @return the updated BorrowRecord.
     * @throws LibraryException if the book/member is not found or no active borrow record exists.
     */
    public BorrowRecord returnBook(String isbn, String memberId) throws LibraryException {
        Book book = getBookByIsbn(isbn);
        Member member = getMemberById(memberId);

        BorrowRecord record = member.getBorrowHistory().stream()
                .filter(r -> r.getBook().getIsbn().equals(isbn) && r.getReturnDate() == null)
                .findFirst()
                .orElseThrow(() -> new LibraryException(
                        "No active borrow record found for book '" + book.getTitle()
                                + "' and member '" + member.getName() + "'."));

        record.setReturnDate(LocalDate.now());
        book.setAvailable(true);
        return record;
    }

    /**
     * Returns all active (unreturned) borrow records.
     */
    public List<BorrowRecord> getActiveBorrowRecords() {
        return borrowRecords.stream()
                .filter(r -> r.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    /**
     * Returns all overdue borrow records.
     */
    public List<BorrowRecord> getOverdueBorrowRecords() {
        return borrowRecords.stream()
                .filter(BorrowRecord::isOverdue)
                .filter(r -> r.getReturnDate() == null)
                .collect(Collectors.toList());
    }

    /**
     * Returns all borrow records (full history).
     */
    public List<BorrowRecord> getAllBorrowRecords() {
        return Collections.unmodifiableList(borrowRecords);
    }
}
