package com.example.maple.locationupdatefrequent.Models;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by venky on 14-Aug-17.
 */

public class UploadInstall {
    @SerializedName("Message")
    JsonArray message_data;

    public JsonArray getMessage_data() {
        return message_data;
    }
}
