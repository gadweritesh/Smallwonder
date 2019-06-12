package com.relecotech.androidsparsh_smallwonder.models;

import java.io.Serializable;

public class BusRouteListData implements Serializable {

    String busRoute;
    String vehicleNo;
    String routeNo;
    String name;
    String mobileNo;
    String startTime;
    String busId;

    public BusRouteListData(String busRoute, String vehicleNo, String routeNo, String name, String mobileNo, String startTime,String busId) {
        this.busRoute = busRoute;
        this.vehicleNo = vehicleNo;
        this.routeNo = routeNo;
        this.name = name;
        this.mobileNo = mobileNo;
        this.startTime = startTime;
        this.busId = busId;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getRouteNo() {
        return routeNo;
    }

    public void setRouteNo(String routeNo) {
        this.routeNo = routeNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }
}
