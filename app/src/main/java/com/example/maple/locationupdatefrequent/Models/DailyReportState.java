package com.example.maple.locationupdatefrequent.Models;

import com.google.gson.annotations.SerializedName;

public class DailyReportState {

    @SerializedName("Response")
    String Response;

    @SerializedName("Message")
    String Message;

    @SerializedName("Upgrade")
    String Upgrade;


    public DailyReportState(String Response, String Message, String Upgrade) {
        this.Response = Response;
        this.Message = Message;
        this.Upgrade = Upgrade;
    }

    public String getResponse() {
        return Response;
    }

    public String getMessage() {
        return Message;
    }

    public String getUpgrade() {
        return Upgrade;
    }
}
