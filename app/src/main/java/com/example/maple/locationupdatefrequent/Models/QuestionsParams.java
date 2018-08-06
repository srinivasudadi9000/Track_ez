package com.example.maple.locationupdatefrequent.Models;

import com.google.gson.annotations.SerializedName;

public class QuestionsParams {

    @SerializedName("CategoryID")
    String CategoryID;

    @SerializedName("Category")
    String Category;

    @SerializedName("ReportingParameterID")
    String ReportingParameterID;

    @SerializedName("ParameterDescription")
    String ParameterDescription;

    @SerializedName("IsActive")
    String IsActive;

    @SerializedName("isPhoto")
    String isPhoto;



    public QuestionsParams(String CategoryID, String Category,String ReportingParameterID,String ParameterDescription,String IsActive,String isPhoto){
       this.CategoryID = CategoryID;this.Category =Category;this.ReportingParameterID=ReportingParameterID;
       this.ParameterDescription = ParameterDescription;this.IsActive = IsActive;this.isPhoto= isPhoto;
    }

    public String getCategoryID() {
        return CategoryID;
    }

    public String getCategory() {
        return Category;
    }

    public String getReportingParameterID() {
        return ReportingParameterID;
    }

    public String getParameterDescription() {
        return ParameterDescription;
    }

    public String getIsActive() {
        return IsActive;
    }

    public String getIsPhoto() {
        return isPhoto;
    }
}
