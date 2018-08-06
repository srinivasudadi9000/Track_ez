package com.example.maple.locationupdatefrequent.Models;

import com.google.gson.annotations.SerializedName;

public class CenterDetails {

    @SerializedName("centerid")
    String centerid;

    @SerializedName("CenterNumber")
    String CenterNumber;



     public CenterDetails(String CenterNumber, String centerid){
       this.CenterNumber = CenterNumber;this.centerid =centerid;
    }

    public String getCenterid() {
        return centerid;
    }

    public String getCenterNumber() {
        return CenterNumber;
    }
}
