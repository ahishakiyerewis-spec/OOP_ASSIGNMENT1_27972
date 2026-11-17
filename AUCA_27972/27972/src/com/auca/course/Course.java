package com.auca.course;

import com.auca.department.Department;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.teacher.Teacher;

public class Course implements Identifiable, Registrable {
    private String courseId;
    private String courseCode;
    private String courseTitle;
    private int creditHours;
    private Department department;
    private Teacher assignedTeacher;

    public Course(String courseId, String courseCode, String courseTitle, int creditHours, Department department, Teacher assignedTeacher) {
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.creditHours = creditHours;
        this.department = department;
        this.assignedTeacher = assignedTeacher;
    }

    @Override
    public String getId() {
        return courseId;
    }

    @Override
    public String getDisplayName() {
        return "[" + courseId + "] " + courseTitle;
    }

    @Override
    public boolean isActive() {
        return courseId != null && !courseId.trim().isEmpty();
    }

    @Override
    public void validate() throws ValidationException {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new ValidationException("Course ID is required.");
        }
        if (courseCode == null || courseCode.trim().length() < 2 || courseCode.trim().length() > 20) {
            throw new ValidationException("Course code must be between 2 and 20 characters.");
        }
        if (courseTitle == null || courseTitle.trim().length() < MIN_NAME_LENGTH || courseTitle.trim().length() > 120) {
            throw new ValidationException("Course title must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        if (creditHours < 1 || creditHours > 6) {
            throw new ValidationException("Credit hours must be between 1 and 6.");
        }
        if (department == null) {
            throw new ValidationException("Course department is required.");
        }
        if (assignedTeacher == null) {
            throw new ValidationException("Assigned teacher is required.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Course: " + getDisplayName() + " | Code=" + courseCode + " | Teacher=" + (assignedTeacher != null ? assignedTeacher.getDisplayName() : "N/A"));
    }

    @Override
    public String getRegistrationSummary() {
        return "Course=" + courseId + ", Code=" + courseCode + ", Title=" + courseTitle + ", Department=" + (department != null ? department.getDepartmentName() : "N/A");
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) throws ValidationException {
        if (courseId == null || courseId.trim().isEmpty()) {
            throw new ValidationException("Course ID is required.");
        }
        this.courseId = courseId.trim();
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) throws ValidationException {
        if (courseCode == null || courseCode.trim().length() < 2 || courseCode.trim().length() > 20) {
            throw new ValidationException("Course code must be between 2 and 20 characters.");
        }
        this.courseCode = courseCode.trim();
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) throws ValidationException {
        if (courseTitle == null || courseTitle.trim().length() < MIN_NAME_LENGTH || courseTitle.trim().length() > 120) {
            throw new ValidationException("Course title must be between " + MIN_NAME_LENGTH + " and 120 characters.");
        }
        this.courseTitle = courseTitle.trim();
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) throws ValidationException {
        if (creditHours < 1 || creditHours > 6) {
            throw new ValidationException("Credit hours must be between 1 and 6.");
        }
        this.creditHours = creditHours;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Teacher getAssignedTeacher() {
        return assignedTeacher;
    }

    public void setAssignedTeacher(Teacher assignedTeacher) {
        this.assignedTeacher = assignedTeacher;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
