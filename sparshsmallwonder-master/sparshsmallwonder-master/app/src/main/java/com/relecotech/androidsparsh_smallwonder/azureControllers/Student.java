package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Relecotech on 13-02-2018.
 */

public class Student implements Serializable {


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
    //    @com.google.gson.annotations.SerializedName("Branch_id")
//    String Branch_id;
    @com.google.gson.annotations.SerializedName("studentEnrollmentNo")
    String studentEnrollmentNo;
    @com.google.gson.annotations.SerializedName("admissionNumber")
    String admissionNumber;
    @com.google.gson.annotations.SerializedName("rollNo")
    String rollNo;
    @com.google.gson.annotations.SerializedName("houseColor")
    String houseColor;
    @com.google.gson.annotations.SerializedName("hostelStudents")
    String hostelStudents;
    @com.google.gson.annotations.SerializedName("caste")
    String caste;
    @com.google.gson.annotations.SerializedName("religion")
    String religion;
    @com.google.gson.annotations.SerializedName("category")
    String category;
    @com.google.gson.annotations.SerializedName("information")
    String information;
    @com.google.gson.annotations.SerializedName("illness")
    String illness;
    @com.google.gson.annotations.SerializedName("motherName")
    String motherName;
    @com.google.gson.annotations.SerializedName("emergencyContact")
    String emergencyContact;
    @com.google.gson.annotations.SerializedName("specialInfo")
    String specialInfo;

    @com.google.gson.annotations.SerializedName("bankName")
    String bankName;
    @com.google.gson.annotations.SerializedName("bank_branchName")
    String bank_branchName;
    @com.google.gson.annotations.SerializedName("bankAccountNumber")
    String bankAccountNumber;
    @com.google.gson.annotations.SerializedName("bankIfscCode")
    String bankIfscCode;

    @com.google.gson.annotations.SerializedName("Users_id")
    String Users_id;
    @com.google.gson.annotations.SerializedName("School_Class_id")
    String School_Class_id;
    @com.google.gson.annotations.SerializedName("Branch_id")
    String Branch_id;


//
//    @com.google.gson.annotations.SerializedName("qualification")
//    String qualification;
//    @com.google.gson.annotations.SerializedName("maritalStatus")
//    String maritalStatus;
//    @com.google.gson.annotations.SerializedName("speciality")
//    String speciality;
//
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


    public Student() {
    }


    public Student(String id, String firstName, String lastName, String middleName, String address, String phone, Date dateOfBirth, String gender, String bloodGrp, String email, String bus_id, String busPickUpPoint, String nationality, String aadharCardNo, String studentEnrollmentNo, String admissionNumber, String rollNo, String houseColor, String hostelStudents, String caste, String religion, String category, String information, String illness, String motherName, String emergencyContact, String specialInfo) {
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
        this.studentEnrollmentNo = studentEnrollmentNo;
        this.admissionNumber = admissionNumber;
        this.rollNo = rollNo;
        this.houseColor = houseColor;
        this.hostelStudents = hostelStudents;
        this.caste = caste;
        this.religion = religion;
        this.category = category;
        this.information = information;
        this.illness = illness;
        this.motherName = motherName;
        this.emergencyContact = emergencyContact;
        this.specialInfo = specialInfo;
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


    public String getStudentEnrollmentNo() {
        return studentEnrollmentNo;
    }

    public void setStudentEnrollmentNo(String studentEnrollmentNo) {
        this.studentEnrollmentNo = studentEnrollmentNo;
    }

    public String getAdmissionNumber() {
        return admissionNumber;
    }

    public void setAdmissionNumber(String admissionNumber) {
        this.admissionNumber = admissionNumber;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getHouseColor() {
        return houseColor;
    }

    public void setHouseColor(String houseColor) {
        this.houseColor = houseColor;
    }

    public String getHostelStudents() {
        return hostelStudents;
    }

    public void setHostelStudents(String hostelStudents) {
        this.hostelStudents = hostelStudents;
    }

    public String getCaste() {
        return caste;
    }

    public void setCaste(String caste) {
        this.caste = caste;
    }

    public String getReligion() {
        return religion;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getIllness() {
        return illness;
    }

    public void setIllness(String illness) {
        this.illness = illness;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getSpecialInfo() {
        return specialInfo;
    }

    public void setSpecialInfo(String specialInfo) {
        this.specialInfo = specialInfo;
    }


    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBank_branchName() {
        return bank_branchName;
    }

    public void setBank_branchName(String bank_branchName) {
        this.bank_branchName = bank_branchName;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankIfscCode() {
        return bankIfscCode;
    }

    public void setBankIfscCode(String bankIfscCode) {
        this.bankIfscCode = bankIfscCode;
    }

    public String getUsers_id() {
        return Users_id;
    }

    public void setUsers_id(String users_id) {
        Users_id = users_id;
    }

    public String getSchool_Class_id() {
        return School_Class_id;
    }

    public void setSchool_Class_id(String school_Class_id) {
        School_Class_id = school_Class_id;
    }

    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }
}