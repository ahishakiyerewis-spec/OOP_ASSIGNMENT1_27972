package com.auca.abstracts;

import java.time.LocalDate;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;

public abstract class AcademicMember extends Person {
    private String memberNumber;
    private Department department;
    private LocalDate joinDate;
    private String academicStatus;
    private String institutionalEmail;
    private String advisorName;

    protected AcademicMember(String personId, String firstName, String lastName, String email, String phoneNumber,
                            LocalDate dateOfBirth, String memberNumber, Department department, LocalDate joinDate,
                            String academicStatus, String institutionalEmail, String advisorName) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth);
        this.memberNumber = memberNumber;
        this.department = department;
        this.joinDate = joinDate;
        this.academicStatus = academicStatus;
        this.institutionalEmail = institutionalEmail;
        this.advisorName = advisorName;
    }

    public abstract String getAcademicCategory();

    public void validate() throws ValidationException {
        super.validate();
        if (memberNumber == null || memberNumber.trim().length() < MIN_ID_LENGTH || memberNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Member number length must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + ".");
        }
        if (department == null) {
            throw new ValidationException("Department is required for an academic member.");
        }
        if (joinDate == null) {
            throw new ValidationException("Join date is required.");
        }
        if (joinDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Join date cannot be in the future.");
        }
        if (academicStatus == null || (!academicStatus.equalsIgnoreCase("ACTIVE") && !academicStatus.equalsIgnoreCase("INACTIVE"))) {
            throw new ValidationException("Academic status must be ACTIVE or INACTIVE.");
        }
        if (institutionalEmail == null || !institutionalEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Institutional email is invalid.");
        }
        if (advisorName == null || advisorName.trim().length() < MIN_NAME_LENGTH || advisorName.trim().length() > 120) {
            throw new ValidationException("Advisor name must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
    }

    public String getDisplayName() {
        return super.getDisplayName() + " (" + memberNumber + ")";
    }

    public String getMemberNumber() {
        return memberNumber;
    }

    public void setMemberNumber(String memberNumber) throws ValidationException {
        if (memberNumber == null || memberNumber.trim().length() < MIN_ID_LENGTH || memberNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Member number length must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + ".");
        }
        this.memberNumber = memberNumber.trim();
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) throws ValidationException {
        if (joinDate == null) {
            throw new ValidationException("Join date is required.");
        }
        if (joinDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Join date cannot be in the future.");
        }
        this.joinDate = joinDate;
    }

    public String getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(String academicStatus) throws ValidationException {
        if (academicStatus == null || (!academicStatus.equalsIgnoreCase("ACTIVE") && !academicStatus.equalsIgnoreCase("INACTIVE"))) {
            throw new ValidationException("Academic status must be ACTIVE or INACTIVE.");
        }
        this.academicStatus = academicStatus.toUpperCase();
    }

    public String getInstitutionalEmail() {
        return institutionalEmail;
    }

    public void setInstitutionalEmail(String institutionalEmail) throws ValidationException {
        if (institutionalEmail == null || !institutionalEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Institutional email is invalid.");
        }
        this.institutionalEmail = institutionalEmail.trim();
    }

    public String getAdvisorName() {
        return advisorName;
    }

    public void setAdvisorName(String advisorName) throws ValidationException {
        if (advisorName == null || advisorName.trim().length() < MIN_NAME_LENGTH || advisorName.trim().length() > 120) {
            throw new ValidationException("Advisor name must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        this.advisorName = advisorName.trim();
    }

    public String getRegistrationSummary() {
        return "Member=" + memberNumber + ", Department=" + (department != null ? department.getDepartmentName() : "N/A") + ", Status=" + academicStatus;
    }
}
