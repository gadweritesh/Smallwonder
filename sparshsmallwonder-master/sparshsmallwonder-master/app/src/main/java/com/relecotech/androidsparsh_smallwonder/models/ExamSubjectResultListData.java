package com.relecotech.androidsparsh_smallwonder.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Amey on 14-12-2017.
 */

public class ExamSubjectResultListData implements Parcelable {
    String examSubject;
    String examSubjectMarkOtained;
    String examSubjectTotalMark;
    String examSubjectGrade;
    String examSubjectComment;

    public ExamSubjectResultListData(String examSubject, String examSubjectMarkOtained, String examSubjectTotalMark, String examSubjectGrade, String examSubjectComment) {
        this.examSubject = examSubject;
        this.examSubjectMarkOtained = examSubjectMarkOtained;
        this.examSubjectTotalMark = examSubjectTotalMark;
        this.examSubjectGrade = examSubjectGrade;
        this.examSubjectComment = examSubjectComment;
    }

    protected ExamSubjectResultListData(Parcel in) {
        examSubject = in.readString();
        examSubjectMarkOtained = in.readString();
        examSubjectTotalMark = in.readString();
        examSubjectGrade = in.readString();
        examSubjectComment = in.readString();
    }

    public static final Creator<ExamSubjectResultListData> CREATOR = new Creator<ExamSubjectResultListData>() {
        @Override
        public ExamSubjectResultListData createFromParcel(Parcel in) {
            return new ExamSubjectResultListData(in);
        }

        @Override
        public ExamSubjectResultListData[] newArray(int size) {
            return new ExamSubjectResultListData[size];
        }
    };

    public String getExamSubject() {
        return examSubject;
    }

    public void setExamSubject(String examSubject) {
        this.examSubject = examSubject;
    }

    public String getExamSubjectMarkOtained() {
        return examSubjectMarkOtained;
    }

    public void setExamSubjectMarkOtained(String examSubjectMarkOtained) {
        this.examSubjectMarkOtained = examSubjectMarkOtained;
    }

    public String getExamSubjectTotalMark() {
        return examSubjectTotalMark;
    }

    public void setExamSubjectTotalMark(String examSubjectTotalMark) {
        this.examSubjectTotalMark = examSubjectTotalMark;
    }

    public String getExamSubjectGrade() {
        return examSubjectGrade;
    }

    public void setExamSubjectGrade(String examSubjectGrade) {
        this.examSubjectGrade = examSubjectGrade;
    }

    public String getExamSubjectComment() {
        return examSubjectComment;
    }

    public void setExamSubjectComment(String examSubjectComment) {
        this.examSubjectComment = examSubjectComment;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(examSubject);
        dest.writeString(examSubjectMarkOtained);
        dest.writeString(examSubjectTotalMark);
        dest.writeString(examSubjectGrade);
        dest.writeString(examSubjectComment);
    }
}
