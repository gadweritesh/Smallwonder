package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

/**
 * Created by amey on 10/16/2015.
 */
public class LeavesListData implements Serializable {

    String leaveId;
    String leavePostDate;
    String leaveCause;
    String leaveStatus;
    String leaveStartDay;
    String leaveEndDay;
    String leaveReply;
    String studentName;
    String studentRollNo;
    String studentId;
    int leaveDayCount;

    public LeavesListData(String leaveId, String studentId, String leavePostDate, String leaveCause, String leaveStatus, String leaveStartDay, String leaveEndDay, String studentName, String leaveReply, String studentRollNo, int leaveDayCount) {

        this.leaveId = leaveId;
        this.studentId = studentId;
        this.leavePostDate = leavePostDate;
        this.leaveCause = leaveCause;
        this.leaveStatus = leaveStatus;
        this.leaveStartDay = leaveStartDay;
        this.leaveEndDay = leaveEndDay;
        this.leaveReply = leaveReply;
        this.studentName = studentName;
        this.studentRollNo = studentRollNo;
        this.leaveDayCount = leaveDayCount;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getLeaveId() {
        return leaveId;
    }

    public void setLeaveId(String leaveId) {
        this.leaveId = leaveId;
    }

    public int getLeaveDayCount() {
        return leaveDayCount;
    }

    public void setLeaveDayCount(int leaveDayCount) {
        this.leaveDayCount = leaveDayCount;
    }


    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }


    public String getLeavePostDate() {
        return leavePostDate;
    }

    public void setLeavePostDate(String leavePostDate) {
        this.leavePostDate = leavePostDate;
    }

    public String getLeaveCause() {
        return leaveCause;
    }

    public void setLeaveCause(String leaveCause) {
        this.leaveCause = leaveCause;
    }

    public String getLeaveStatus() {
        return leaveStatus;
    }

    public void setLeaveStatus(String leaveStatus) {
        this.leaveStatus = leaveStatus;
    }

    public String getLeaveStartDay() {
        return leaveStartDay;
    }

    public void setLeaveStartDay(String leaveStartDay) {
        this.leaveStartDay = leaveStartDay;
    }

    public String getLeaveEndDay() {
        return leaveEndDay;
    }

    public void setLeaveEndDay(String leaveEndDay) {
        this.leaveEndDay = leaveEndDay;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getLeaveReply() {
        return leaveReply;
    }

    public void setLeaveReply(String leaveReply) {
        this.leaveReply = leaveReply;
    }

}
