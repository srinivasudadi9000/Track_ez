package com.example.maple.locationupdatefrequent.Models;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class GetParams {

    @SerializedName("Message")
    List<QuestionsParams> questionsParams;

    public List<QuestionsParams> getQuestionsParams() {
        return questionsParams;
    }
}
