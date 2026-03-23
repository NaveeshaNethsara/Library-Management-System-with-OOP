package library.model;

import java.time.LocalDate;

/**
 * Represents a borrowing transaction between a member and a book.
 */
public class BorrowRecord {

    private static final int DEFAULT_BORROW_DAYS = 14;

    private final Book book;
    private final Member member;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public BorrowRecord(Book book, Member member) {
        this(book, member, LocalDate.now());
    }

    public BorrowRecord(Book book, Member member, LocalDate borrowDate) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if (member == null) {
            throw new IllegalArgumentException("Member cannot be null.");
        }
        if (borrowDate == null) {
            throw new IllegalArgumentException("Borrow date cannot be null.");
        }
        this.book = book;
        this.member = member;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(DEFAULT_BORROW_DAYS);
        this.returnDate = null;
    }

    public Book getBook() {
        return book;
    }

    public Member getMember() {
        return member;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isOverdue() {
        if (returnDate != null) {
            return returnDate.isAfter(dueDate);
        }
        return LocalDate.now().isAfter(dueDate);
    }

    @Override
    public String toString() {
        return String.format("BorrowRecord{book='%s', member='%s', borrowed=%s, due=%s, returned=%s%s}",
                book.getTitle(), member.getName(), borrowDate, dueDate,
                returnDate != null ? returnDate.toString() : "Not returned",
                isOverdue() ? " [OVERDUE]" : "");
    }
}
