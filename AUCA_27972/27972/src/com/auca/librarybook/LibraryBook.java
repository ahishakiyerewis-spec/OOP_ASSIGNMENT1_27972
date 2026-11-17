package com.auca.librarybook;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class LibraryBook implements Identifiable, Registrable {
    private String bookId;
    private String isbn;
    private String title;
    private String author;
    private int totalCopies;
    private int availableCopies;

    public LibraryBook(String bookId, String isbn, String title, String author, int totalCopies, int availableCopies) {
        this.bookId = bookId;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    @Override
    public String getId() {
        return bookId;
    }

    @Override
    public String getDisplayName() {
        return "[" + bookId + "] " + title;
    }

    @Override
    public boolean isActive() {
        return bookId != null && !bookId.trim().isEmpty();
    }

    @Override
    public void validate() throws ValidationException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new ValidationException("Book ID is required.");
        }
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN is required.");
        }
        if (title == null || title.trim().length() < MIN_NAME_LENGTH || title.trim().length() > 120) {
            throw new ValidationException("Title must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        if (author == null || author.trim().length() < MIN_NAME_LENGTH || author.trim().length() > 120) {
            throw new ValidationException("Author must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        if (totalCopies < 0) {
            throw new ValidationException("Total copies cannot be negative.");
        }
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new ValidationException("Available copies must be between 0 and total copies.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Book: " + getDisplayName() + " | Author=" + author + " | Copies=" + availableCopies + "/" + totalCopies);
    }

    @Override
    public String getRegistrationSummary() {
        return "Book=" + bookId + ", ISBN=" + isbn + ", Title=" + title + ", Available=" + availableCopies + "/" + totalCopies;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) throws ValidationException {
        if (bookId == null || bookId.trim().isEmpty()) {
            throw new ValidationException("Book ID is required.");
        }
        this.bookId = bookId.trim();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) throws ValidationException {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new ValidationException("ISBN is required.");
        }
        this.isbn = isbn.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws ValidationException {
        if (title == null || title.trim().length() < MIN_NAME_LENGTH || title.trim().length() > 120) {
            throw new ValidationException("Title must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        this.title = title.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) throws ValidationException {
        if (author == null || author.trim().length() < MIN_NAME_LENGTH || author.trim().length() > 120) {
            throw new ValidationException("Author must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        this.author = author.trim();
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(int totalCopies) throws ValidationException {
        if (totalCopies < 0) {
            throw new ValidationException("Total copies cannot be negative.");
        }
        if (availableCopies > totalCopies) {
            throw new ValidationException("Available copies cannot exceed total copies.");
        }
        this.totalCopies = totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) throws ValidationException {
        if (availableCopies < 0 || availableCopies > totalCopies) {
            throw new ValidationException("Available copies must be between 0 and total copies.");
        }
        this.availableCopies = availableCopies;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
