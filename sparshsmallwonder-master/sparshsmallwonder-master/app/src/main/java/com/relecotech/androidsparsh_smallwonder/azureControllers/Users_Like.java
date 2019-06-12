package com.relecotech.androidsparsh_smallwonder.azureControllers;

/**
 * Created by Relecotech on 06-03-2018.
 */

public class Users_Like {

    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("selectedItemId")
    String selectedItemId;
    @com.google.gson.annotations.SerializedName("Student_id")
    String Student_id;
    @com.google.gson.annotations.SerializedName("Branch_id")
    String Branch_id;

    public Users_Like() {
    }

    public Users_Like(String id, String selectedItemId, String student_id, String branch_id) {
        this.id = id;
        this.selectedItemId = selectedItemId;
        Student_id = student_id;
        Branch_id = branch_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSelectedItemId() {
        return selectedItemId;
    }

    public void setSelectedItemId(String selectedItemId) {
        this.selectedItemId = selectedItemId;
    }

    public String getStudent_id() {
        return Student_id;
    }

    public void setStudent_id(String student_id) {
        Student_id = student_id;
    }

    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }
}
