package com.auca.teacher;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.auca.abstracts.AcademicMember;
import com.auca.department.Department;
import com.auca.exceptions.ValidationException;

public class Teacher extends AcademicMember {
    private String employeeNumber;
    private String specialization;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String officeNumber;
    private String employmentType;

    public Teacher(String personId, String firstName, String lastName, String email, String phoneNumber, LocalDate dateOfBirth,
                  String memberNumber, Department department, LocalDate joinDate, String academicStatus,
                  String institutionalEmail, String advisorName,
                  String employeeNumber, String specialization, LocalDate hireDate, BigDecimal salary,
                  String officeNumber, String employmentType) {
        super(personId, firstName, lastName, email, phoneNumber, dateOfBirth, memberNumber, department, joinDate,
                academicStatus, institutionalEmail, advisorName);
        this.employeeNumber = employeeNumber;
        this.specialization = specialization;
        this.hireDate = hireDate;
        this.salary = salary;
        this.officeNumber = officeNumber;
        this.employmentType = employmentType;
    }

    @Override
    public String getRoleDescription() {
        return "Teacher";
    }

    @Override
    public String getAcademicCategory() {
        return "Teacher";
    }

    @Override
    public void validate() throws ValidationException {
        if (employmentType == null || !java.util.Arrays.asList("FULL_TIME", "PART_TIME", "CONTRACT").contains(employmentType.trim().toUpperCase())) {
            throw new ValidationException("Employment type must be FULL_TIME, PART_TIME or CONTRACT.");
        }
        super.validate();
        if (employeeNumber == null || employeeNumber.trim().length() < MIN_ID_LENGTH || employeeNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Employee number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }
        if (specialization == null || specialization.trim().length() < MIN_NAME_LENGTH || specialization.trim().length() > 120) {
            throw new ValidationException("Specialization must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        if (hireDate == null) {
            throw new ValidationException("Hire date is required.");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Hire date cannot be in the future.");
        }
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Teacher salary must be strictly positive.");
        }
        if (officeNumber == null || officeNumber.trim().isEmpty()) {
            throw new ValidationException("Office number is required.");
        }
        if (employmentType == null || employmentType.trim().isEmpty()) {
            throw new ValidationException("Employment type is required.");
        }
    }

    public String getEmployeeNumber() {
        return employeeNumber;
    }

    public void setEmployeeNumber(String employeeNumber) throws ValidationException {
        if (employeeNumber == null || employeeNumber.trim().length() < MIN_ID_LENGTH || employeeNumber.trim().length() > MAX_ID_LENGTH) {
            throw new ValidationException("Employee number must be between " + MIN_ID_LENGTH + " and " + MAX_ID_LENGTH + " characters.");
        }
        this.employeeNumber = employeeNumber.trim();
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) throws ValidationException {
        if (specialization == null || specialization.trim().length() < MIN_NAME_LENGTH || specialization.trim().length() > 120) {
            throw new ValidationException("Specialization must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        this.specialization = specialization.trim();
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) throws ValidationException {
        if (hireDate == null) {
            throw new ValidationException("Hire date is required.");
        }
        if (hireDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Hire date cannot be in the future.");
        }
        this.hireDate = hireDate;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) throws ValidationException {
        if (salary == null || salary.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Teacher salary must be strictly positive.");
        }
        this.salary = salary;
    }

    public String getOfficeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) throws ValidationException {
        if (officeNumber == null || officeNumber.trim().isEmpty()) {
            throw new ValidationException("Office number is required.");
        }
        this.officeNumber = officeNumber.trim();
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) throws ValidationException {
        if (employmentType == null || employmentType.trim().isEmpty()) {
            throw new ValidationException("Employment type is required.");
        }
        this.employmentType = employmentType.trim();
    }

    @Override
    public void printDetails() {
        System.out.println("Teacher: " + getDisplayName() + " [" + employeeNumber + "]");
    }

    @Override
    public String getRegistrationSummary() {
        return "Teacher=" + employeeNumber + ", Name=" + getDisplayName() + ", Specialization=" + specialization + ", Department=" + getDepartment();
    }

    @Override
    public String toString() {
        return "Teacher [" + employeeNumber + "] " + getDisplayName();
    }
}
