package com.auca.feepayment;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

public class FeePayment implements Identifiable, Registrable {
    private String paymentId;
    private Student student;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentMethod;
    private String receiptNumber;

    public FeePayment(String paymentId, Student student, BigDecimal amount, LocalDate paymentDate, String paymentMethod, String receiptNumber) {
        this.paymentId = paymentId;
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.receiptNumber = receiptNumber;
    }

    @Override
    public String getId() {
        return paymentId;
    }

    @Override
    public String getDisplayName() {
        return "[" + paymentId + "] " + (student != null ? student.getDisplayName() : "Unknown student");
    }

    @Override
    public boolean isActive() {
        return paymentId != null && !paymentId.trim().isEmpty();
    }

    @Override
    public void validate() throws ValidationException {
        if (paymentMethod == null || !java.util.Arrays.asList("CASH", "MOBILE_MONEY", "BANK_TRANSFER", "CARD").contains(paymentMethod.trim().toUpperCase())) {
            throw new ValidationException("Payment method must be CASH, MOBILE_MONEY, BANK_TRANSFER or CARD.");
        }
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new ValidationException("Payment ID is required.");
        }
        if (student == null) {
            throw new ValidationException("Student is required for a fee payment.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero.");
        }
        if (paymentDate == null) {
            throw new ValidationException("Payment date is required.");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Payment date cannot be in the future.");
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new ValidationException("Payment method is required.");
        }
        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            throw new ValidationException("Receipt number is required.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Fee payment: " + getDisplayName() + " | Amount=" + amount + " | Method=" + paymentMethod + " | Receipt=" + receiptNumber);
    }

    @Override
    public String getRegistrationSummary() {
        return "Payment=" + paymentId + ", Student=" + (student != null ? student.getDisplayName() : "N/A") + ", Amount=" + amount + ", Method=" + paymentMethod;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) throws ValidationException {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new ValidationException("Payment ID is required.");
        }
        this.paymentId = paymentId.trim();
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) throws ValidationException {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be greater than zero.");
        }
        this.amount = amount;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) throws ValidationException {
        if (paymentDate == null) {
            throw new ValidationException("Payment date is required.");
        }
        if (paymentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Payment date cannot be in the future.");
        }
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) throws ValidationException {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new ValidationException("Payment method is required.");
        }
        this.paymentMethod = paymentMethod.trim();
    }

    public String getReceiptNumber() {
        return receiptNumber;
    }

    public void setReceiptNumber(String receiptNumber) throws ValidationException {
        if (receiptNumber == null || receiptNumber.trim().isEmpty()) {
            throw new ValidationException("Receipt number is required.");
        }
        this.receiptNumber = receiptNumber.trim();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
