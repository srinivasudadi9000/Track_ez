package com.example.maple.locationupdatefrequent.rest;

import com.example.maple.locationupdatefrequent.Models.UploadInstall;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Multipart
    @POST("/SendMessage?")
    Call<UploadInstall> getUploadInstall (@Part("Token") RequestBody Token,
                                         @Part("DeviceID") RequestBody DeviceID, @Part("MessageDescription") RequestBody MessageDescription,
                                          @Part("Long") RequestBody Long, @Part("Lat") RequestBody Lat,
                                          @Part("ReportedFrom") RequestBody ReportedFrom, @Part("ReportedDateTime") RequestBody ReportedDateTime,
                                          @Part("DR") RequestBody DR, @Part("MobileDeviceID") RequestBody MobileDeviceID,
                                         @Part MultipartBody.Part photo
    );


}
