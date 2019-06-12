package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Relecotech on 15-02-2018.
 */

public class Admin implements Serializable {

    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("firstName")
    String firstName;
    @com.google.gson.annotations.SerializedName("lastName")
    String lastName;
    @com.google.gson.annotations.SerializedName("phone")
    String phone;
    @com.google.gson.annotations.SerializedName("dateOfBirth")
    Date dateOfBirth;
    @com.google.gson.annotations.SerializedName("designation")
    String designation;
    @com.google.gson.annotations.SerializedName("qualification")
    String qualification;
    @com.google.gson.annotations.SerializedName("aadharCardNo")
    String aadharCardNo;

    public Admin() {
    }

    public Admin(String id, String firstName, String lastName, String phone, Date dateOfBirth,String designation, String qualification, String aadharCardNo) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.designation = designation;
        this.qualification = qualification;
        this.aadharCardNo = aadharCardNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getAadharCardNo() {
        return aadharCardNo;
    }

    public void setAadharCardNo(String aadharCardNo) {
        this.aadharCardNo = aadharCardNo;
    }
}
