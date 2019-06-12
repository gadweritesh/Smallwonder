package com.relecotech.androidsparsh_smallwonder.models;

import java.util.Date;

public class ClassScheduleListData {
    private java.util.Date slotStartTime;
    private java.util.Date slotEndTime;
//    private String slotStartTime;
//    private String slotEndTime;
    private String slotSubject;
    private String subjectTeacherName;
    private String slotType;
    private String teacherClassDivision;
    private Boolean expandable_list_status;


    public ClassScheduleListData(Date slotStartTime, Date slotEndTime, String slotSubject, String subjectTeacherName, String slotType, String teacherClassDivision, Boolean expandable_list_status) {
        this.slotStartTime = slotStartTime;
        this.slotEndTime = slotEndTime;
        this.slotSubject = slotSubject;
        this.subjectTeacherName = subjectTeacherName;
        this.slotType = slotType;
        this.teacherClassDivision = teacherClassDivision;
        this.expandable_list_status = expandable_list_status;
    }


    public Date getSlotStartTime() {
        return slotStartTime;
    }

    public void setSlotStartTime(Date slotStartTime) {
        this.slotStartTime = slotStartTime;
    }

    public Date getSlotEndTime() {
        return slotEndTime;
    }

    public void setSlotEndTime(Date slotEndTime) {
        this.slotEndTime = slotEndTime;
    }

    public String getSlotSubject() {
        return slotSubject;
    }

    public void setSlotSubject(String slotSubject) {
        this.slotSubject = slotSubject;
    }

    public String getSubjectTeacherName() {
        return subjectTeacherName;
    }

    public void setSubjectTeacherName(String subjectTeacherName) {
        this.subjectTeacherName = subjectTeacherName;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getTeacherClassDivision() {
        return teacherClassDivision;
    }

    public void setTeacherClassDivision(String teacherClassDivision) {
        this.teacherClassDivision = teacherClassDivision;
    }

    public Boolean getExpandable_list_status() {
        return expandable_list_status;
    }

    public void setExpandable_list_status(Boolean expandable_list_status) {
        this.expandable_list_status = expandable_list_status;
    }
}

