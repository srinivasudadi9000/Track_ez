package com.example.maple.locationupdatefrequent.Models;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetCat {

    @SerializedName("Message")
    List<CenterDetails> centers;

    public List<CenterDetails> getCenters() {
        return centers;
    }
}
