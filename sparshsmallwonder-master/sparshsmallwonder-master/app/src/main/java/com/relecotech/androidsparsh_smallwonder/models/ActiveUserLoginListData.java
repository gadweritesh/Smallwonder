package com.relecotech.androidsparsh_smallwonder.models;

public class ActiveUserLoginListData {
    String userName;
    String userRole;
    Integer serialNo;

    public ActiveUserLoginListData(String userName, String userRole, Integer serialNo) {
        this.userName = userName;
        this.userRole = userRole;
        this.serialNo = serialNo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Integer getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }
}
