package com.auca.bookloan;

import java.time.LocalDate;

import com.auca.bookloan.BookLoan;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.librarybook.LibraryBook;
import com.auca.student.Student;

public class BookLoan implements Identifiable, Registrable {
    private String loanId;
    private Student student;
    private LibraryBook book;
    private LocalDate borrowedDate;
    private LocalDate dueDate;
    private LocalDate returnedDate;

    public BookLoan(String loanId, Student student, LibraryBook book, LocalDate borrowedDate, LocalDate dueDate, LocalDate returnedDate) {
        this.loanId = loanId;
        this.student = student;
        this.book = book;
        this.borrowedDate = borrowedDate;
        this.dueDate = dueDate;
        this.returnedDate = returnedDate;
    }

    @Override
    public String getId() {
        return loanId;
    }

    @Override
    public String getDisplayName() {
        return "[" + loanId + "] " + (student != null ? student.getDisplayName() : "N/A") + " / " + (book != null ? book.getTitle() : "N/A");
    }

    @Override
    public boolean isActive() {
        return returnedDate == null;
    }

    @Override
    public void validate() throws ValidationException {
        if (loanId == null || loanId.trim().isEmpty()) {
            throw new ValidationException("Loan ID is required.");
        }
        if (student == null) {
            throw new ValidationException("Student is required.");
        }
        if (book == null) {
            throw new ValidationException("Book is required.");
        }
        if (borrowedDate == null) {
            throw new ValidationException("Borrowed date is required.");
        }
        if (dueDate == null) {
            throw new ValidationException("Due date is required.");
        }
        if (dueDate.isBefore(borrowedDate) || dueDate.isEqual(borrowedDate)) {
            throw new ValidationException("Due date must be after borrowed date.");
        }
        if (returnedDate != null && returnedDate.isBefore(borrowedDate)) {
            throw new ValidationException("Returned date cannot be before borrowed date.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Book loan: " + getDisplayName() + " | Borrowed=" + borrowedDate + " | Due=" + dueDate + " | Returned=" + (returnedDate != null ? returnedDate : "Not returned"));
    }

    @Override
    public String getRegistrationSummary() {
        return "Loan=" + loanId + ", Student=" + (student != null ? student.getDisplayName() : "N/A") + ", Book=" + (book != null ? book.getTitle() : "N/A") + ", Due=" + dueDate;
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) throws ValidationException {
        if (loanId == null || loanId.trim().isEmpty()) {
            throw new ValidationException("Loan ID is required.");
        }
        this.loanId = loanId.trim();
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public LibraryBook getBook() {
        return book;
    }

    public void setBook(LibraryBook book) {
        this.book = book;
    }

    public LocalDate getBorrowedDate() {
        return borrowedDate;
    }

    public void setBorrowedDate(LocalDate borrowedDate) throws ValidationException {
        if (borrowedDate == null) {
            throw new ValidationException("Borrowed date is required.");
        }
        this.borrowedDate = borrowedDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) throws ValidationException {
        if (dueDate == null) {
            throw new ValidationException("Due date is required.");
        }
        if (borrowedDate != null && (dueDate.isBefore(borrowedDate) || dueDate.isEqual(borrowedDate))) {
            throw new ValidationException("Due date must be after borrowed date.");
        }
        this.dueDate = dueDate;
    }

    public LocalDate getReturnedDate() {
        return returnedDate;
    }

    public void setReturnedDate(LocalDate returnedDate) throws ValidationException {
        if (returnedDate != null && returnedDate.isBefore(borrowedDate)) {
            throw new ValidationException("Returned date cannot be before borrowed date.");
        }
        this.returnedDate = returnedDate;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
