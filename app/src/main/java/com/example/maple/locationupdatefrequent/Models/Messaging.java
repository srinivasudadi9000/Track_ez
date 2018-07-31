package com.example.maple.locationupdatefrequent.Models;

public class Messaging {

    String message,status,cdt;
    public Messaging(String message, String status, String cdt){
        this.message = message;this.status = status;this.cdt = cdt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCdt() {
        return cdt;
    }

    public void setCdt(String cdt) {
        this.cdt = cdt;
    }
}


