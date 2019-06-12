package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Amey on 29-05-2018.
 */

public class AbsentStudentListData {
    String absentStudentName;
    String absentStudentUniqueNo;
    String absentStudentClassDivision;

    public AbsentStudentListData(String absentStudentName, String absentStudentUniqueNo, String absentStudentClassDivision) {
        this.absentStudentName = absentStudentName;
        this.absentStudentUniqueNo = absentStudentUniqueNo;
        this.absentStudentClassDivision = absentStudentClassDivision;
    }

    public String getAbsentStudentName() {
        return absentStudentName;
    }

    public void setAbsentStudentName(String absentStudentName) {
        this.absentStudentName = absentStudentName;
    }

    public String getAbsentStudentUniqueNo() {
        return absentStudentUniqueNo;
    }

    public void setAbsentStudentUniqueNo(String absentStudentUniqueNo) {
        this.absentStudentUniqueNo = absentStudentUniqueNo;
    }

    public String getAbsentStudentClassDivision() {
        return absentStudentClassDivision;
    }

    public void setAbsentStudentClassDivision(String absentStudentClassDivision) {
        this.absentStudentClassDivision = absentStudentClassDivision;
    }
}
