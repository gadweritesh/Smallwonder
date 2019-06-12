package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Relecotech on 06-03-2018.
 */

public class AssignmentListData implements Serializable {
    private int drawableId;
    private String maxCredits;
    private String subject;
    private Date dueDate;
    private Date issueDate;
    private String classStd;
    private String submittedBy;
    private String description;
    private String assId;
    private String division;
    private String assStatus;
    private String creditsEarned;
    private String gradeEarned;
    private String note;
    private String scoreType;
    private int likeCount;
    private String assignmentAttachmentIdentifier;
    private boolean likeCheck;

    public AssignmentListData(int drawableId, String assId, String maxCredits, String subject, Date issueDate, Date dueDate, String classStd, String division, String submittedBy, String description, String assStatus, String creditsEarned, String gradeEarned, String note, String scoreType, String attachmentCount, int likeCount, String assignmentAttachmentIdentifier, boolean likeCheck) {
        super();
        this.drawableId = drawableId;
        this.maxCredits = maxCredits;
        this.subject = subject;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.classStd = classStd;
        this.division = division;
        this.assId = assId;
        this.submittedBy = submittedBy;
        this.description = description;
        this.assStatus = assStatus;
        this.creditsEarned = creditsEarned;
        this.gradeEarned = gradeEarned;
        this.note = note;
        this.scoreType = scoreType;
        this.attachmentCount = attachmentCount;
        this.likeCount = likeCount;
        this.assignmentAttachmentIdentifier = assignmentAttachmentIdentifier;
        this.likeCheck = likeCheck;
    }

    public String getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(String attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    private String attachmentCount;

    public String getScoreType() {
        return scoreType;
    }

    public void setScoreType(String scoreType) {
        this.scoreType = scoreType;
    }

    public void setissueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getAssStatus() {
        return assStatus;
    }

    public void setAssStatus(String assStatus) {
        this.assStatus = assStatus;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getMaxCredits() {
        return maxCredits;
    }

    public void setMaxCredits(String maxCredits) {
        this.maxCredits = maxCredits;
    }

    public String getSubmittedBy() {
        return submittedBy;
    }

    public void setSubmittedBy(String submittedBy) {
        this.submittedBy = submittedBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAssId() {
        return assId;
    }

    public void setAssId(String assId) {
        this.assId = assId;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getClassStd() {
        return classStd;
    }

    public void setClassStd(String classStd) {
        this.classStd = classStd;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public int getDrawableId() {
        return drawableId;
    }

    public void setDrawableId(int drawableId) {
        this.drawableId = drawableId;
    }

    public String getCreditsEarned() {
        return creditsEarned;
    }

    public void setCreditsEarned(String creditsEarned) {
        this.creditsEarned = creditsEarned;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getGradeEarned() {
        return gradeEarned;
    }

    public void setGradeEarned(String gradeEarned) {
        this.gradeEarned = gradeEarned;
    }


    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getAssignmentAttachmentIdentifier() {
        return assignmentAttachmentIdentifier;
    }

    public void setAssignmentAttachmentIdentifier(String assignmentAttachmentIdentifier) {
        this.assignmentAttachmentIdentifier = assignmentAttachmentIdentifier;
    }

    public boolean isLikeCheck() {
        return likeCheck;
    }

    public void setLikeCheck(boolean likeCheck) {
        this.likeCheck = likeCheck;
    }

}
