package com.relecotech.androidsparsh_smallwonder.models;

/**
 * Created by Amey on 07-11-2017.
 */

public class FeesInformationListData {
    String fees_title;
    String fees_total_amount;
    String fees_amount_payable;
    String fees_due_date;
    String fees_paid_date;
    String fees_Student_fee_category;
    String fees_status;
    String fees_Student_id;
    String fees_Comment;

    public FeesInformationListData(String fees_title, String fees_total_amount, String fees_amount_payable, String fees_due_date, String fees_paid_date, String fees_Student_fee_category, String fees_status, String fees_Student_id, String fees_Comment) {
        this.fees_title = fees_title;
        this.fees_total_amount = fees_total_amount;
        this.fees_amount_payable = fees_amount_payable;
        this.fees_due_date = fees_due_date;
        this.fees_paid_date = fees_paid_date;
        this.fees_Student_fee_category = fees_Student_fee_category;
        this.fees_status = fees_status;
        this.fees_Student_id = fees_Student_id;
        this.fees_Comment = fees_Comment;
    }


    public String getFees_title() {
        return fees_title;
    }

    public void setFees_title(String fees_title) {
        this.fees_title = fees_title;
    }

    public String getFees_total_amount() {
        return fees_total_amount;
    }

    public void setFees_total_amount(String fees_total_amount) {
        this.fees_total_amount = fees_total_amount;
    }

    public String getFees_amount_payable() {
        return fees_amount_payable;
    }

    public void setFees_amount_payable(String fees_amount_payable) {
        this.fees_amount_payable = fees_amount_payable;
    }

    public String getFees_due_date() {
        return fees_due_date;
    }

    public void setFees_due_date(String fees_due_date) {
        this.fees_due_date = fees_due_date;
    }

    public String getFees_paid_date() {
        return fees_paid_date;
    }

    public void setFees_paid_date(String fees_paid_date) {
        this.fees_paid_date = fees_paid_date;
    }

    public String getFees_Student_fee_category() {
        return fees_Student_fee_category;
    }

    public void setFees_Student_fee_category(String fees_Student_fee_category) {
        this.fees_Student_fee_category = fees_Student_fee_category;
    }

    public String getFees_status() {
        return fees_status;
    }

    public void setFees_status(String fees_status) {
        this.fees_status = fees_status;
    }

    public String getFees_Student_id() {
        return fees_Student_id;
    }

    public void setFees_Student_id(String fees_Student_id) {
        this.fees_Student_id = fees_Student_id;
    }

    public String getFees_Comment() {
        return fees_Comment;
    }

    public void setFees_Comment(String fees_Comment) {
        this.fees_Comment = fees_Comment;
    }
}
