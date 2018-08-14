package com.example.maple.locationupdatefrequent.Models;

public class Checkins {

    String latitude,longitude,cdate,address,deviceid,deviceno,status,run;

    public Checkins(String latitude, String longitude, String cdate,String address,String deviceid,String deviceno,String status,String run){
        this.cdate= cdate;this.latitude=latitude;this.longitude= longitude;this.address = address;this.deviceid = deviceid;
        this.deviceno = deviceno;this.status= status;this.run=run;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getDeviceno() {
        return deviceno;
    }

    public void setDeviceno(String deviceno) {
        this.deviceno = deviceno;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRun() {
        return run;
    }

    public void setRun(String run) {
        this.run = run;
    }
}
