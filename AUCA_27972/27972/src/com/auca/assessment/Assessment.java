package com.auca.assessment;

import java.time.LocalDate;

import com.auca.enrollment.Enrollment;
import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class Assessment implements Identifiable, Registrable {
    private String assessmentId;
    private Enrollment enrollment;
    private String assessmentType;
    private double maximumScore;
    private double obtainedScore;
    private LocalDate assessmentDate;

    public Assessment(String assessmentId, Enrollment enrollment, String assessmentType, double maximumScore, double obtainedScore, LocalDate assessmentDate) {
        this.assessmentId = assessmentId;
        this.enrollment = enrollment;
        this.assessmentType = assessmentType;
        this.maximumScore = maximumScore;
        this.obtainedScore = obtainedScore;
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String getId() {
        return assessmentId;
    }

    @Override
    public String getDisplayName() {
        return "[" + assessmentId + "] " + assessmentType;
    }

    @Override
    public boolean isActive() {
        return assessmentId != null && !assessmentId.trim().isEmpty();
    }

    @Override
    public void validate() throws ValidationException {
        if (assessmentType == null || !java.util.Arrays.asList("QUIZ", "ASSIGNMENT", "MIDTERM", "FINAL_EXAM", "PROJECT").contains(assessmentType.trim().toUpperCase())) {
            throw new ValidationException("Assessment type must be QUIZ, ASSIGNMENT, MIDTERM, FINAL_EXAM or PROJECT.");
        }
        if (assessmentId == null || assessmentId.trim().isEmpty()) {
            throw new ValidationException("Assessment ID is required.");
        }
        if (enrollment == null) {
            throw new ValidationException("Enrollment is required for an assessment.");
        }
        if (assessmentType == null || assessmentType.trim().isEmpty()) {
            throw new ValidationException("Assessment type is required.");
        }
        if (maximumScore <= 0) {
            throw new ValidationException("Maximum score must be greater than zero.");
        }
        if (obtainedScore < 0 || obtainedScore > maximumScore) {
            throw new ValidationException("Obtained score must be between 0 and the maximum score.");
        }
        if (assessmentDate == null) {
            throw new ValidationException("Assessment date is required.");
        }
        if (assessmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Assessment date cannot be in the future.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Assessment: " + getDisplayName() + " | Student=" + (enrollment != null ? enrollment.getStudent().getDisplayName() : "N/A") + " | Score=" + obtainedScore + "/" + maximumScore);
    }

    @Override
    public String getRegistrationSummary() {
        return "Assessment=" + assessmentId + ", Type=" + assessmentType + ", Score=" + obtainedScore + "/" + maximumScore;
    }

    public String getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(String assessmentId) throws ValidationException {
        if (assessmentId == null || assessmentId.trim().isEmpty()) {
            throw new ValidationException("Assessment ID is required.");
        }
        this.assessmentId = assessmentId.trim();
    }

    public Enrollment getEnrollment() {
        return enrollment;
    }

    public void setEnrollment(Enrollment enrollment) {
        this.enrollment = enrollment;
    }

    public String getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(String assessmentType) throws ValidationException {
        if (assessmentType == null || assessmentType.trim().isEmpty()) {
            throw new ValidationException("Assessment type is required.");
        }
        this.assessmentType = assessmentType.trim();
    }

    public double getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(double maximumScore) throws ValidationException {
        if (maximumScore <= 0) {
            throw new ValidationException("Maximum score must be greater than zero.");
        }
        this.maximumScore = maximumScore;
    }

    public double getObtainedScore() {
        return obtainedScore;
    }

    public void setObtainedScore(double obtainedScore) throws ValidationException {
        if (obtainedScore < 0 || obtainedScore > maximumScore) {
            throw new ValidationException("Obtained score must be between 0 and the maximum score.");
        }
        this.obtainedScore = obtainedScore;
    }

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public void setAssessmentDate(LocalDate assessmentDate) throws ValidationException {
        if (assessmentDate == null) {
            throw new ValidationException("Assessment date is required.");
        }
        if (assessmentDate.isAfter(LocalDate.now())) {
            throw new ValidationException("Assessment date cannot be in the future.");
        }
        this.assessmentDate = assessmentDate;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
