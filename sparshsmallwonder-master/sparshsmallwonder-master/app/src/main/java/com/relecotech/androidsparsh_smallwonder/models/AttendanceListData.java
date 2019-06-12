package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

/**
 * Created by Amey on 12-04-2018.
 */

public class AttendanceListData implements Serializable {
    String attendanceDate;
    String attendanceStatus;


    public AttendanceListData(String attendanceDate, String attendanceStatus) {
        this.attendanceDate = attendanceDate;
        this.attendanceStatus = attendanceStatus;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(String attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
    }
}

