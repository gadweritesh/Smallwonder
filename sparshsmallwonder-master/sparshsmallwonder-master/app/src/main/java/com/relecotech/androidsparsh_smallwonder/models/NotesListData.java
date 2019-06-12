package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

/**
 * Created by amey on 10/16/2015.
 */
public class NotesListData implements Serializable {

    String description;
    String noteCategory;
    String postDatetime;
    String meetingScheduleDateTime;
    String notesStatus;
    String reply;
    String concernedStudent;
    String concernedTeacher;
    String studentRollNo;
    String noteId;
    String studentId;

    public NotesListData(String noteId, String studentId, String description, String noteCategory, String postDatetime, String meetingScheduleDateTime, String notesStatus, String reply, String concernedTeacher, String concernedStudent, String studentRollNo) {
        this.description = description;
        this.noteCategory = noteCategory;
        this.postDatetime = postDatetime;
        this.meetingScheduleDateTime = meetingScheduleDateTime;
        this.notesStatus = notesStatus;
        this.reply = reply;
        this.concernedStudent = concernedStudent;
        this.concernedTeacher = concernedTeacher;
        this.studentRollNo = studentRollNo;
        this.noteId = noteId;
        this.studentId = studentId;

    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getNoteId() {
        return noteId;
    }

    public void setNoteId(String noteId) {
        this.noteId = noteId;
    }

    public String getStudentRollNo() {
        return studentRollNo;
    }

    public void setStudentRollNo(String studentRollNo) {
        this.studentRollNo = studentRollNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNoteCategory() {
        return noteCategory;
    }

    public void setNoteCategory(String noteCategory) {
        this.noteCategory = noteCategory;
    }

    public String getPostDatetime() {
        return postDatetime;
    }

    public void setPostDatetime(String postDatetime) {
        this.postDatetime = postDatetime;
    }

    public String getMeetingScheduleDateTime() {
        return meetingScheduleDateTime;
    }

    public void setMeetingScheduleDateTime(String meetingScheduleDateTime) {
        this.meetingScheduleDateTime = meetingScheduleDateTime;
    }

    public String getNotesStatus() {
        return notesStatus;
    }

    public void setNotesStatus(String notesStatus) {
        this.notesStatus = notesStatus;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getConcernedTeacher() {
        return concernedTeacher;
    }

    public void setConcernedTeacher(String concernedTeacher) {
        this.concernedTeacher = concernedTeacher;
    }

    public String getConcernedStudent() {
        return concernedStudent;
    }

    public void setConcernedStudent(String concernedStudent) {
        this.concernedStudent = concernedStudent;
    }

}
