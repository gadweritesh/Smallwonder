package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Relecotech on 14-02-2018.
 */

public class Teacher implements Serializable {

    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("firstName")
    String firstName;
    @com.google.gson.annotations.SerializedName("lastName")
    String lastName;
    @com.google.gson.annotations.SerializedName("middleName")
    String middleName;
    @com.google.gson.annotations.SerializedName("address")
    String address;
    @com.google.gson.annotations.SerializedName("phone")
    String phone;
    @com.google.gson.annotations.SerializedName("dateOfBirth")
    Date dateOfBirth;
    @com.google.gson.annotations.SerializedName("gender")
    String gender;
    @com.google.gson.annotations.SerializedName("bloodGrp")
    String bloodGrp;
    @com.google.gson.annotations.SerializedName("email")
    String email;
    @com.google.gson.annotations.SerializedName("bus_id")
    String bus_id;
    @com.google.gson.annotations.SerializedName("busPickUpPoint")
    String busPickUpPoint;
    @com.google.gson.annotations.SerializedName("nationality")
    String nationality;
    @com.google.gson.annotations.SerializedName("aadharCardNo")
    String aadharCardNo;
    //    @com.google.gson.annotations.SerializedName("designation")
//    String Branch_id;
    @com.google.gson.annotations.SerializedName("designation")
    String designation;

    @com.google.gson.annotations.SerializedName("qualification")
    String qualification;
    @com.google.gson.annotations.SerializedName("maritalStatus")
    String maritalStatus;
    @com.google.gson.annotations.SerializedName("speciality")
    String speciality;
    @com.google.gson.annotations.SerializedName("Branch_id")
    String Branch_id;
    @com.google.gson.annotations.SerializedName("teacherRegid")
    String teacherRegid;

    @com.google.gson.annotations.SerializedName("Users_id")
    String Users_id;



//    @com.google.gson.annotations.SerializedName("userEmail")
//    String userEmail;
//    @com.google.gson.annotations.SerializedName("userPassword")
//    String userPassword;
//    @com.google.gson.annotations.SerializedName("userPin")
//    String userPin;
//    @com.google.gson.annotations.SerializedName("class")
//    String schoolClass;
//    @com.google.gson.annotations.SerializedName("division")
//    String division;


    public Teacher() {
    }

    public Teacher(String id, String firstName, String lastName, String middleName, String address, String phone, Date dateOfBirth, String gender, String bloodGrp, String email, String bus_id, String busPickUpPoint, String nationality, String aadharCardNo, String designation, String Branch_id) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.address = address;
        this.phone = phone;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.bloodGrp = bloodGrp;
        this.email = email;
        this.bus_id = bus_id;
        this.busPickUpPoint = busPickUpPoint;
        this.nationality = nationality;
        this.aadharCardNo = aadharCardNo;
        this.designation = designation;
        this.Branch_id = Branch_id;
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

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBloodGrp() {
        return bloodGrp;
    }

    public void setBloodGrp(String bloodGrp) {
        this.bloodGrp = bloodGrp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBus_id() {
        return bus_id;
    }

    public void setBus_id(String bus_id) {
        this.bus_id = bus_id;
    }

    public String getBusPickUpPoint() {
        return busPickUpPoint;
    }

    public void setBusPickUpPoint(String busPickUpPoint) {
        this.busPickUpPoint = busPickUpPoint;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAadharCardNo() {
        return aadharCardNo;
    }

    public void setAadharCardNo(String aadharCardNo) {
        this.aadharCardNo = aadharCardNo;
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

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }

    public String getTeacherRegid() {
        return teacherRegid;
    }

    public void setTeacherRegid(String teacherRegid) {
        this.teacherRegid = teacherRegid;
    }

    public String getUsers_id() {
        return Users_id;
    }

    public void setUsers_id(String users_id) {
        Users_id = users_id;
    }
}
