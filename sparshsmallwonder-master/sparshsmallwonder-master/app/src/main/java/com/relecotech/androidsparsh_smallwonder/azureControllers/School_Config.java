package com.relecotech.androidsparsh_smallwonder.azureControllers;

import java.io.Serializable;

/**
 * Created by Relecotech on 16-02-2018.
 */

public class School_Config implements Serializable {

    @com.google.gson.annotations.SerializedName("id")
    String id;
    @com.google.gson.annotations.SerializedName("Type")
    String Type;
    @com.google.gson.annotations.SerializedName("Value")
    String Value;
    @com.google.gson.annotations.SerializedName("value_one")
    String value_one;
    @com.google.gson.annotations.SerializedName("value_two")
    String value_two;
    @com.google.gson.annotations.SerializedName("Branch_id")
    String Branch_id;

    public School_Config() {
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getValue() {
        return Value;
    }

    public void setValue(String value) {
        Value = value;
    }

    public String getValue_one() {
        return value_one;
    }

    public void setValue_one(String value_one) {
        this.value_one = value_one;
    }

    public String getValue_two() {
        return value_two;
    }

    public void setValue_two(String value_two) {
        this.value_two = value_two;
    }

    public String getBranch_id() {
        return Branch_id;
    }

    public void setBranch_id(String branch_id) {
        Branch_id = branch_id;
    }
}