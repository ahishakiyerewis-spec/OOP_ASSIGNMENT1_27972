package com.auca.enrollment;

import java.time.LocalDate;

import com.auca.course.Course;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;
import com.auca.student.Student;

public class Enrollment implements Identifiable, Registrable {
    private String enrollmentId;
    private Student student;
    private Course course;
    private LocalDate enrollmentDate;
    private String semester;
    private String enrollmentStatus;

    public Enrollment(String enrollmentId, Student student, Course course, LocalDate enrollmentDate, String semester, String enrollmentStatus) {
        this.enrollmentId = enrollmentId;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate;
        this.semester = semester;
        this.enrollmentStatus = enrollmentStatus;
    }

    @Override
    public String getId() {
        return enrollmentId;
    }

    @Override
    public String getDisplayName() {
        return "[" + enrollmentId + "] " + (student != null ? student.getDisplayName() : "N/A") + " -> " + (course != null ? course.getCourseCode() : "N/A");
    }

    @Override
    public boolean isActive() {
        return "ACTIVE".equalsIgnoreCase(enrollmentStatus);
    }

    @Override
    public void validate() throws ValidationException {
        if (semester == null || !semester.trim().toUpperCase().matches("^(FALL|SPRING|SUMMER) [0-9]{4}$")) {
            throw new ValidationException("Semester must look like FALL 2026, SPRING 2027 or SUMMER 2027.");
        }
        if (enrollmentId == null || enrollmentId.trim().isEmpty()) {
            throw new ValidationException("Enrollment ID is required.");
        }
        if (student == null) {
            throw new ValidationException("Student is required.");
        }
        if (course == null) {
            throw new ValidationException("Course is required.");
        }
        if (enrollmentDate == null) {
            throw new ValidationException("Enrollment date is required.");
        }
        if (enrollmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Enrollment date cannot be in the future.");
        }
        if (semester == null || semester.trim().isEmpty()) {
            throw new ValidationException("Semester is required.");
        }
        if (enrollmentStatus == null || (!enrollmentStatus.equalsIgnoreCase("ACTIVE") && !enrollmentStatus.equalsIgnoreCase("COMPLETED") && !enrollmentStatus.equalsIgnoreCase("WITHDRAWN"))) {
            throw new ValidationException("Enrollment status must be ACTIVE, COMPLETED or WITHDRAWN.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Enrollment: " + getDisplayName() + " | Semester=" + semester + " | Status=" + enrollmentStatus);
    }

    @Override
    public String getRegistrationSummary() {
        return "Enrollment=" + enrollmentId + ", Student=" + (student != null ? student.getDisplayName() : "N/A") + ", Course=" + (course != null ? course.getCourseTitle() : "N/A") + ", Status=" + enrollmentStatus;
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public void setEnrollmentId(String enrollmentId) throws ValidationException {
        if (enrollmentId == null || enrollmentId.trim().isEmpty()) {
            throw new ValidationException("Enrollment ID is required.");
        }
        this.enrollmentId = enrollmentId.trim();
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) throws ValidationException {
        if (enrollmentDate == null) {
            throw new ValidationException("Enrollment date is required.");
        }
        if (enrollmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Enrollment date cannot be in the future.");
        }
        this.enrollmentDate = enrollmentDate;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) throws ValidationException {
        if (semester == null || semester.trim().isEmpty()) {
            throw new ValidationException("Semester is required.");
        }
        this.semester = semester.trim();
    }

    public String getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public void setEnrollmentStatus(String enrollmentStatus) throws ValidationException {
        if (enrollmentStatus == null || (!enrollmentStatus.equalsIgnoreCase("ACTIVE") && !enrollmentStatus.equalsIgnoreCase("COMPLETED") && !enrollmentStatus.equalsIgnoreCase("WITHDRAWN"))) {
            throw new ValidationException("Enrollment status must be ACTIVE, COMPLETED or WITHDRAWN.");
        }
        this.enrollmentStatus = enrollmentStatus.toUpperCase();
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
