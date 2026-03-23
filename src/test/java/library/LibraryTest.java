package library;

import library.exception.LibraryException;
import library.model.Book;
import library.model.BorrowRecord;
import library.model.Member;
import library.service.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryTest {

    private Library library;
    private Book book1;
    private Book book2;
    private Member member1;
    private Member member2;

    @BeforeEach
    void setUp() throws LibraryException {
        library = new Library("Test Library");
        book1 = new Book("ISBN-001", "Clean Code", "Robert C. Martin", "Technology", 2008);
        book2 = new Book("ISBN-002", "The Pragmatic Programmer", "Andrew Hunt", "Technology", 1999);
        member1 = new Member("M001", "Alice", "alice@test.com", "555-0001");
        member2 = new Member("M002", "Bob", "bob@test.com", "555-0002");
        library.addBook(book1);
        library.addBook(book2);
        library.addMember(member1);
        library.addMember(member2);
    }

    // -------------------------------------------------------------------------
    // Book tests
    // -------------------------------------------------------------------------

    @Test
    void addBook_success() throws LibraryException {
        Book newBook = new Book("ISBN-003", "Refactoring", "Martin Fowler", "Technology", 2018);
        library.addBook(newBook);
        assertEquals(3, library.getAllBooks().size());
    }

    @Test
    void addBook_duplicateIsbn_throwsLibraryException() {
        Book duplicate = new Book("ISBN-001", "Duplicate", "Author", "Genre", 2000);
        assertThrows(LibraryException.class, () -> library.addBook(duplicate));
    }

    @Test
    void addBook_nullBook_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> library.addBook(null));
    }

    @Test
    void removeBook_success() throws LibraryException {
        library.removeBook("ISBN-001");
        assertEquals(1, library.getAllBooks().size());
    }

    @Test
    void removeBook_notFound_throwsLibraryException() {
        assertThrows(LibraryException.class, () -> library.removeBook("UNKNOWN"));
    }

    @Test
    void removeBook_currentlyBorrowed_throwsLibraryException() throws LibraryException {
        library.borrowBook("ISBN-001", "M001");
        assertThrows(LibraryException.class, () -> library.removeBook("ISBN-001"));
    }

    @Test
    void getBookByIsbn_found() throws LibraryException {
        Book found = library.getBookByIsbn("ISBN-001");
        assertEquals("Clean Code", found.getTitle());
    }

    @Test
    void getBookByIsbn_notFound_throwsLibraryException() {
        assertThrows(LibraryException.class, () -> library.getBookByIsbn("MISSING"));
    }

    @Test
    void searchBooksByTitle_partialMatch() {
        List<Book> results = library.searchBooksByTitle("clean");
        assertEquals(1, results.size());
        assertEquals("Clean Code", results.get(0).getTitle());
    }

    @Test
    void searchBooksByTitle_caseInsensitive() {
        List<Book> results = library.searchBooksByTitle("CLEAN CODE");
        assertEquals(1, results.size());
    }

    @Test
    void searchBooksByAuthor_partialMatch() {
        List<Book> results = library.searchBooksByAuthor("martin");
        assertEquals(1, results.size());
    }

    @Test
    void getAvailableBooks_returnsOnlyAvailable() throws LibraryException {
        library.borrowBook("ISBN-001", "M001");
        List<Book> available = library.getAvailableBooks();
        assertEquals(1, available.size());
        assertEquals("ISBN-002", available.get(0).getIsbn());
    }

    // -------------------------------------------------------------------------
    // Book model tests
    // -------------------------------------------------------------------------

    @Test
    void book_blankIsbn_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Book("", "Title", "Author", "Genre", 2000));
    }

    @Test
    void book_nullTitle_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Book("ISBN-X", null, "Author", "Genre", 2000));
    }

    @Test
    void book_isAvailableByDefault() {
        assertTrue(book1.isAvailable());
    }

    // -------------------------------------------------------------------------
    // Member tests
    // -------------------------------------------------------------------------

    @Test
    void addMember_success() throws LibraryException {
        Member newMember = new Member("M003", "Carol", "carol@test.com", "555-0003");
        library.addMember(newMember);
        assertEquals(3, library.getAllMembers().size());
    }

    @Test
    void addMember_duplicateId_throwsLibraryException() {
        Member duplicate = new Member("M001", "Duplicate", "dup@test.com", "000");
        assertThrows(LibraryException.class, () -> library.addMember(duplicate));
    }

    @Test
    void removeMember_withUnreturnedBooks_throwsLibraryException() throws LibraryException {
        library.borrowBook("ISBN-001", "M001");
        assertThrows(LibraryException.class, () -> library.removeMember("M001"));
    }

    @Test
    void removeMember_success() throws LibraryException {
        library.removeMember("M001");
        assertEquals(1, library.getAllMembers().size());
    }

    @Test
    void getMemberById_notFound_throwsLibraryException() {
        assertThrows(LibraryException.class, () -> library.getMemberById("UNKNOWN"));
    }

    @Test
    void searchMembersByName_caseInsensitive() {
        List<Member> results = library.searchMembersByName("alice");
        assertEquals(1, results.size());
        assertEquals("Alice", results.get(0).getName());
    }

    // -------------------------------------------------------------------------
    // Member model tests
    // -------------------------------------------------------------------------

    @Test
    void member_blankId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Member("", "Name", "e@mail.com", "123"));
    }

    @Test
    void member_nullName_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Member("M999", null, "e@mail.com", "123"));
    }

    @Test
    void member_initialBorrowCount_isZero() {
        assertEquals(0, member1.getCurrentBorrowCount());
    }

    // -------------------------------------------------------------------------
    // Borrowing tests
    // -------------------------------------------------------------------------

    @Test
    void borrowBook_success() throws LibraryException {
        BorrowRecord record = library.borrowBook("ISBN-001", "M001");
        assertNotNull(record);
        assertFalse(book1.isAvailable());
        assertEquals(1, member1.getCurrentBorrowCount());
        assertNull(record.getReturnDate());
    }

    @Test
    void borrowBook_unavailableBook_throwsLibraryException() throws LibraryException {
        library.borrowBook("ISBN-001", "M001");
        assertThrows(LibraryException.class, () -> library.borrowBook("ISBN-001", "M002"));
    }

    @Test
    void borrowBook_exceedsLimit_throwsLibraryException() throws LibraryException {
        library.addBook(new Book("ISBN-003", "Book 3", "Author 3", "Genre", 2000));
        library.addBook(new Book("ISBN-004", "Book 4", "Author 4", "Genre", 2001));
        library.borrowBook("ISBN-001", "M001");
        library.borrowBook("ISBN-002", "M001");
        library.borrowBook("ISBN-003", "M001");
        assertThrows(LibraryException.class, () -> library.borrowBook("ISBN-004", "M001"));
    }

    @Test
    void returnBook_success() throws LibraryException {
        library.borrowBook("ISBN-001", "M001");
        BorrowRecord record = library.returnBook("ISBN-001", "M001");
        assertTrue(book1.isAvailable());
        assertNotNull(record.getReturnDate());
        assertEquals(0, member1.getCurrentBorrowCount());
    }

    @Test
    void returnBook_noActiveBorrow_throwsLibraryException() {
        assertThrows(LibraryException.class, () -> library.returnBook("ISBN-001", "M001"));
    }

    @Test
    void getActiveBorrowRecords_returnsOnlyActive() throws LibraryException {
        library.borrowBook("ISBN-001", "M001");
        library.borrowBook("ISBN-002", "M002");
        library.returnBook("ISBN-001", "M001");
        List<BorrowRecord> active = library.getActiveBorrowRecords();
        assertEquals(1, active.size());
    }

    // -------------------------------------------------------------------------
    // BorrowRecord tests
    // -------------------------------------------------------------------------

    @Test
    void borrowRecord_dueDateIsFourteenDaysAfterBorrow() {
        LocalDate today = LocalDate.now();
        BorrowRecord record = new BorrowRecord(book1, member1, today);
        assertEquals(today.plusDays(14), record.getDueDate());
    }

    @Test
    void borrowRecord_isNotOverdueWhenReturnedOnTime() {
        LocalDate borrowDate = LocalDate.now().minusDays(10);
        BorrowRecord record = new BorrowRecord(book1, member1, borrowDate);
        record.setReturnDate(LocalDate.now());
        assertFalse(record.isOverdue());
    }

    @Test
    void borrowRecord_isOverdueWhenReturnedLate() {
        LocalDate borrowDate = LocalDate.now().minusDays(20);
        BorrowRecord record = new BorrowRecord(book1, member1, borrowDate);
        record.setReturnDate(LocalDate.now());
        assertTrue(record.isOverdue());
    }

    @Test
    void borrowRecord_isOverdueWhenNotReturnedPastDueDate() {
        LocalDate borrowDate = LocalDate.now().minusDays(20);
        BorrowRecord record = new BorrowRecord(book1, member1, borrowDate);
        assertTrue(record.isOverdue());
    }

    @Test
    void borrowRecord_nullBook_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new BorrowRecord(null, member1));
    }

    @Test
    void borrowRecord_nullMember_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new BorrowRecord(book1, null));
    }
}
