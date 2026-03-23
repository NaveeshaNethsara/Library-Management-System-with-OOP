package library.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a library member.
 */
public class Member {

    private final String memberId;
    private String name;
    private String email;
    private String phone;
    private final List<BorrowRecord> borrowHistory;

    public Member(String memberId, String name, String email, String phone) {
        if (memberId == null || memberId.isBlank()) {
            throw new IllegalArgumentException("Member ID cannot be null or blank.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.borrowHistory = new ArrayList<>();
    }

    public String getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank.");
        }
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<BorrowRecord> getBorrowHistory() {
        return Collections.unmodifiableList(borrowHistory);
    }

    public void addBorrowRecord(BorrowRecord record) {
        borrowHistory.add(record);
    }

    /**
     * Returns the number of books currently borrowed by this member.
     */
    public long getCurrentBorrowCount() {
        return borrowHistory.stream().filter(r -> r.getReturnDate() == null).count();
    }

    @Override
    public String toString() {
        return String.format("Member{ID='%s', name='%s', email='%s', phone='%s', currentlyBorrowed=%d}",
                memberId, name, email, phone, getCurrentBorrowCount());
    }
}
