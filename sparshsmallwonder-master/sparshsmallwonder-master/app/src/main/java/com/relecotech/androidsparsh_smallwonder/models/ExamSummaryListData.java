package com.relecotech.androidsparsh_smallwonder.models;

import java.util.Date;

/**
 * Created by Amey on 14-12-2017.
 */

public class ExamSummaryListData {

    String examType;
    String totalMarks;
    String obtainedMarks;
    String result_summary_comment;
    Date examDate;
    String examFinalGrade;

    public ExamSummaryListData(String examType, String totalMarks, String obtainedMarks, String result_summary_comment, Date examDate, String examFinalGrade) {
        this.examType = examType;
        this.totalMarks = totalMarks;
        this.obtainedMarks = obtainedMarks;
        this.result_summary_comment = result_summary_comment;
        this.examDate = examDate;
        this.examFinalGrade = examFinalGrade;
    }

    public String getExamType() {
        return examType;
    }

    public void setExamType(String examType) {
        this.examType = examType;
    }

    public String getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(String totalMarks) {
        this.totalMarks = totalMarks;
    }

    public String getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(String obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public String getResult_summary_comment() {
        return result_summary_comment;
    }

    public void setResult_summary_comment(String result_summary_comment) {
        this.result_summary_comment = result_summary_comment;
    }

    public Date getExamDate() {
        return examDate;
    }

    public void setExamDate(Date examDate) {
        this.examDate = examDate;
    }

    public String getExamFinalGrade() {
        return examFinalGrade;
    }

    public void setExamFinalGrade(String examFinalGrade) {
        this.examFinalGrade = examFinalGrade;
    }
}
