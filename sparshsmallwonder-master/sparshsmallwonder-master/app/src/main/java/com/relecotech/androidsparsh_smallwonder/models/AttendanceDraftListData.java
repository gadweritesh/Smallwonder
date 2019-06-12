package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by amey on 10/16/2015.
 */
public class AttendanceDraftListData {
    String schoolClassId;
    String attendanceClass;
    String attendanceDivision;
    String attendanceDate;

    public AttendanceDraftListData(String schoolClassId, String attendanceClass, String attendanceDivision, String attendanceDate) {
        this.schoolClassId = schoolClassId;
        this.attendanceClass = attendanceClass;
        this.attendanceDivision = attendanceDivision;
        this.attendanceDate = attendanceDate;
    }

    public String getSchoolClassId() {
        return schoolClassId;
    }

    public void setSchoolClassId(String schoolClassId) {
        this.schoolClassId = schoolClassId;
    }

    public String getAttendanceClass() {
        return attendanceClass;
    }

    public void setAttendanceClass(String attendanceClass) {
        this.attendanceClass = attendanceClass;
    }

    public String getAttendanceDivision() {
        return attendanceDivision;
    }

    public void setAttendanceDivision(String attendanceDivision) {
        this.attendanceDivision = attendanceDivision;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
}
