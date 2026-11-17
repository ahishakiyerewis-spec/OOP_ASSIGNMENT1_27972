package com.auca.administrator;

import java.time.LocalDate;

import com.auca.abstracts.Person;
import com.auca.exceptions.ValidationException;

public class Administrator extends Person {
    private String staffNumber;
    private String jobTitle;
    private String assignedOffice;
    private LocalDate employmentDate;
    private int accessLevel;
    private boolean active;

    public Administrator(String personId, String firstName, String lastName, String email, String phoneNumber,
                         LocalDate dateOfBirth, String staffNumber, String jobTitle, String assignedOffice,
                         LocalDate employmentDate, int accessLevel, boolean active) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.staffNumber = staffNumber;
        this.jobTitle = jobTitle;
        this.assignedOffice = assignedOffice;
        this.employmentDate = employmentDate;
        this.accessLevel = accessLevel;
        this.active = active;
    }

    @Override
    public String getRoleDescription() {
        return "Administrator";
    }

    @Override
    public void validate() throws ValidationException {
        super.validate();
        if (staffNumber == null || staffNumber.trim().length() < MIN_ID_LENGTH || staffNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Staff number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }
        if (jobTitle == null || jobTitle.trim().length() < MIN_NAME_LENGTH || jobTitle.trim().length() > 120) {
            throw new ValidationException("Job title must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        if (assignedOffice == null || assignedOffice.trim().isEmpty()) {
            throw new ValidationException("Assigned office is required.");
        }
        if (employmentDate == null) {
            throw new ValidationException("Employment date is required.");
        }
        if (employmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Employment date cannot be in the future.");
        }
        if (accessLevel < 1) {
            throw new ValidationException("Access level must be positive.");
        }
    }

    public String getStaffNumber() {
        return staffNumber;
    }

    public void setStaffNumber(String staffNumber) throws ValidationException {
        if (staffNumber == null || staffNumber.trim().length() < MIN_ID_LENGTH || staffNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Staff number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }
        this.staffNumber = staffNumber.trim();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) throws ValidationException {
        if (jobTitle == null || jobTitle.trim().length() < MIN_NAME_LENGTH || jobTitle.trim().length() > 120) {
            throw new ValidationException("Job title must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        this.jobTitle = jobTitle.trim();
    }

    public String getAssignedOffice() {
        return assignedOffice;
    }

    public void setAssignedOffice(String assignedOffice) throws ValidationException {
        if (assignedOffice == null || assignedOffice.trim().isEmpty()) {
            throw new ValidationException("Assigned office is required.");
        }
        this.assignedOffice = assignedOffice.trim();
    }

    public LocalDate getEmploymentDate() {
        return employmentDate;
    }

    public void setEmploymentDate(LocalDate employmentDate) throws ValidationException {
        if (employmentDate == null) {
            throw new ValidationException("Employment date is required.");
        }
        if (employmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Employment date cannot be in the future.");
        }
        this.employmentDate = employmentDate;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(int accessLevel) throws ValidationException {
        if (accessLevel < 1) {
            throw new ValidationException("Access level must be positive.");
        }
        this.accessLevel = accessLevel;
    }

    @Override
    public boolean isActive() {
        return active && super.isActive();
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void printDetails() {
        System.out.println("Administrator: " + getDisplayName() + " [" + staffNumber + "]");
    }

    @Override
    public String getRegistrationSummary() {
        return "Administrator=" + staffNumber + ", Name=" + getDisplayName() + ", Role=" + jobTitle;
    }

    @Override
    public String toString() {
        return "Administrator [" + staffNumber + "] " + getDisplayName();
    }
}
