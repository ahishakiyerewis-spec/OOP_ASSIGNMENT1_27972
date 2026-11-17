package com.auca.department;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Department implements Identifiable, Registrable {
    private String departmentId;
    private String departmentName;
    private String departmentCode;
    private String officeLocation;
    private String contactEmail;
    private Teacher headTeacher;

    public Department(String departmentId, String departmentName, String departmentCode, String officeLocation, String contactEmail, Teacher headTeacher) {
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.departmentCode = departmentCode;
        this.officeLocation = officeLocation;
        this.contactEmail = contactEmail;
        this.headTeacher = headTeacher;
    }

    @Override
    public String getId() {
        return departmentId;
    }

    @Override
    public String getDisplayName() {
        return "[" + departmentId + "] " + departmentName;
    }

    @Override
    public boolean isActive() {
        return departmentId != null && !departmentId.trim().isEmpty();
    }

    @Override
    public void validate() throws ValidationException {
        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new ValidationException("Department ID is required.");
        }
        if (departmentName == null || departmentName.trim().length() < MIN_NAME_LENGTH || departmentName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Department name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        if (departmentCode == null || departmentCode.trim().length() < 2 || departmentCode.trim().length() > 20) {
            throw new ValidationException("Department code must be between 2 and 20 characters.");
        }
        if (officeLocation == null || officeLocation.trim().length() < 2 || officeLocation.trim().length() > 120) {
            throw new ValidationException("Office location must be between 2 and 120 characters.");
        }
        if (contactEmail == null || !contactEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Contact email is invalid.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Department: " + getDisplayName() + " | Code=" + departmentCode + " | Head=" + (headTeacher != null ? headTeacher.getDisplayName() : "Unassigned"));
    }

    @Override
    public String getRegistrationSummary() {
        return "Department=" + departmentId + ", Name=" + departmentName + ", Code=" + departmentCode + ", Head=" + (headTeacher != null ? headTeacher.getDisplayName() : "Unassigned");
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) throws ValidationException {
        if (departmentId == null || departmentId.trim().isEmpty()) {
            throw new ValidationException("Department ID is required.");
        }
        this.departmentId = departmentId.trim();
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) throws ValidationException {
        if (departmentName == null || departmentName.trim().length() < MIN_NAME_LENGTH || departmentName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Department name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        this.departmentName = departmentName.trim();
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) throws ValidationException {
        if (departmentCode == null || departmentCode.trim().length() < 2 || departmentCode.trim().length() > 20) {
            throw new ValidationException("Department code must be between 2 and 20 characters.");
        }
        this.departmentCode = departmentCode.trim();
    }

    public String getOfficeLocation() {
        return officeLocation;
    }

    public void setOfficeLocation(String officeLocation) throws ValidationException {
        if (officeLocation == null || officeLocation.trim().length() < 2 || officeLocation.trim().length() > 120) {
            throw new ValidationException("Office location must be between 2 and 120 characters.");
        }
        this.officeLocation = officeLocation.trim();
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) throws ValidationException {
        if (contactEmail == null || !contactEmail.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new ValidationException("Contact email is invalid.");
        }
        this.contactEmail = contactEmail.trim();
    }

    public Teacher getHeadTeacher() {
        return headTeacher;
    }

    public void setHeadTeacher(Teacher headTeacher) {
        this.headTeacher = headTeacher;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
