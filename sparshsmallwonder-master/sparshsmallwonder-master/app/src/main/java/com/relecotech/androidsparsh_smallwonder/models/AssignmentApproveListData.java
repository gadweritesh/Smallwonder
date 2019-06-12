package com.relecotech.androidsparsh_smallwonder.models;

public class AssignmentApproveListData {

    String firstName;
    String lastName;
    String status;
    String rollNo;
    int nullId = 0;
    private String mName;
    private String assmntStatusTableId;
    private String assmntId;
    private String studentId;
    private String credits;
    private String grades;
    private String notes;

    public AssignmentApproveListData(String firstName, String lastName, String status, String rollNo, String mName, String assmntStatusTableId, String assmntId, String studentId, String credits, String grades, String notes) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = status;
        this.rollNo = rollNo;
        this.mName = mName;
        this.assmntStatusTableId = assmntStatusTableId;
        this.assmntId = assmntId;
        this.studentId = studentId;
        this.credits = credits;
        this.grades = grades;
        this.notes = notes;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public int getNullId() {
        return nullId;
    }

    public void setNullId(int nullId) {
        this.nullId = nullId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getAssmntStatusTableId() {
        return assmntStatusTableId;
    }

    public void setAssmntStatusTableId(String assmntStatusTableId) {
        this.assmntStatusTableId = assmntStatusTableId;
    }

    public String getAssmntId() {
        return assmntId;
    }

    public void setAssmntId(String assmntId) {
        this.assmntId = assmntId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCredits() {
        return credits;
    }

    public void setCredits(String credits) {
        this.credits = credits;
    }

    public String getGrades() {
        return grades;
    }

    public void setGrades(String grades) {
        this.grades = grades;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
