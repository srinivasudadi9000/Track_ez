package com.example.maple.locationupdatefrequent.Models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by venky on 14-Aug-17.
 */

public class UploadInstall {
    @SerializedName("Message")
    List<DailyReportState> message_data;

    public List<DailyReportState> getMessage_data() {
        return message_data;
    }
}
