package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

public class ExamTimeTableListData implements Serializable {

    String examId;
    String examTitle;
    String examDateMain;
    String tentativeDateComment;
    String examDate;
    String examTime;
    String examSubject;

    public ExamTimeTableListData(String examId, String examTitle,String examDateMain,String tentativeDateComment, String examDate, String examTime, String examSubject) {
        this.examId = examId;
        this.examTitle = examTitle;
        this.examDateMain = examDateMain;
        this.tentativeDateComment = tentativeDateComment;
        this.examDate = examDate;
        this.examTime = examTime;
        this.examSubject = examSubject;
    }

    public String getExamTitle() {
        return examTitle;
    }

    public void setExamTitle(String examTitle) {
        this.examTitle = examTitle;
    }

    public String getExamDate() {
        return examDate;
    }

    public void setExamDate(String examDate) {
        this.examDate = examDate;
    }

    public String getExamTime() {
        return examTime;
    }

    public void setExamTime(String examTime) {
        this.examTime = examTime;
    }

    public String getExamSubject() {
        return examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public String getExamId() {
        return examId;
    }

    public void setExamId(String examId) {
        this.examId = examId;
    }


    public String getTentativeDateComment() {
        return tentativeDateComment;
    }

    public void setTentativeDateComment(String tentativeDateComment) {
        this.tentativeDateComment = tentativeDateComment;
    }


    public String getExamDateMain() {
        return examDateMain;
    }

    public void setExamDateMain(String examDateMain) {
        this.examDateMain = examDateMain;
    }
}
