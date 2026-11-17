package com.auca.classroom;

import com.auca.exceptions.ValidationException;
import com.auca.interfaces.Identifiable;
import com.auca.interfaces.Registrable;

public class Classroom implements Identifiable, Registrable {
    private String classroomId;
    private String buildingName;
    private String roomNumber;
    private int capacity;
    private int floorNumber;
    private boolean available;

    public Classroom(String classroomId, String buildingName, String roomNumber, int capacity, int floorNumber, boolean available) {
        this.classroomId = classroomId;
        this.buildingName = buildingName;
        this.roomNumber = roomNumber;
        this.capacity = capacity;
        this.floorNumber = floorNumber;
        this.available = available;
    }

    @Override
    public String getId() {
        return classroomId;
    }

    @Override
    public String getDisplayName() {
        return "[" + classroomId + "] " + buildingName + " - " + roomNumber;
    }

    @Override
    public boolean isActive() {
        return classroomId != null && !classroomId.trim().isEmpty();
    }

    @Override
    public void validate() throws ValidationException {
        if (classroomId == null || classroomId.trim().isEmpty()) {
            throw new ValidationException("Classroom ID is required.");
        }
        if (buildingName == null || buildingName.trim().length() < MIN_NAME_LENGTH || buildingName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Building name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new ValidationException("Room number is required.");
        }
        if (capacity <= 0) {
            throw new ValidationException("Classroom capacity must be greater than zero.");
        }
        if (floorNumber < 0) {
            throw new ValidationException("Floor number cannot be negative.");
        }
    }

    @Override
    public void printDetails() {
        System.out.println("Classroom: " + getDisplayName() + " | Capacity=" + capacity + " | Floor=" + floorNumber + " | Available=" + available);
    }

    @Override
    public String getRegistrationSummary() {
        return "Classroom=" + classroomId + ", Building=" + buildingName + ", Room=" + roomNumber + ", Capacity=" + capacity;
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) throws ValidationException {
        if (classroomId == null || classroomId.trim().isEmpty()) {
            throw new ValidationException("Classroom ID is required.");
        }
        this.classroomId = classroomId.trim();
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) throws ValidationException {
        if (buildingName == null || buildingName.trim().length() < MIN_NAME_LENGTH || buildingName.trim().length() > MAX_NAME_LENGTH) {
            throw new ValidationException("Building name must be between " + MIN_NAME_LENGTH + " and " + MAX_NAME_LENGTH + " characters.");
        }
        this.buildingName = buildingName.trim();
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) throws ValidationException {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new ValidationException("Room number is required.");
        }
        this.roomNumber = roomNumber.trim();
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) throws ValidationException {
        if (capacity <= 0) {
            throw new ValidationException("Classroom capacity must be greater than zero.");
        }
        this.capacity = capacity;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) throws ValidationException {
        if (floorNumber < 0) {
            throw new ValidationException("Floor number cannot be negative.");
        }
        this.floorNumber = floorNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
