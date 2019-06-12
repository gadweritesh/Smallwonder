package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

public class StudentAttendanceListData implements Serializable {
    int Month;
    int PresentDays;
    int TotalDays;

    public StudentAttendanceListData(int month, int presentDays, int totalDays) {
        Month = month;
        PresentDays = presentDays;
        TotalDays = totalDays;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public int getPresentDays() {
        return PresentDays;
    }

    public void setPresentDays(int presentDays) {
        PresentDays = presentDays;
    }

    public int getTotalDays() {
        return TotalDays;
    }

    public void setTotalDays(int totalDays) {
        TotalDays = totalDays;
    }
}
