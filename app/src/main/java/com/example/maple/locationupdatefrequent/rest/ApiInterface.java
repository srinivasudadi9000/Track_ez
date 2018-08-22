package com.example.maple.locationupdatefrequent.rest;

import com.example.maple.locationupdatefrequent.Models.GetCat;
import com.example.maple.locationupdatefrequent.Models.GetParams;
import com.example.maple.locationupdatefrequent.Models.UploadInstall;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiInterface {

 /*   @Multipart
    @POST("/SendMessage?")
    Call<UploadInstall> getUploadInstall (@Part("Token") RequestBody Token,
                                         @Part("DeviceID") RequestBody DeviceID, @Part("MessageDescription") RequestBody MessageDescription,
                                          @Part("Long") RequestBody Long, @Part("Lat") RequestBody Lat,
                                          @Part("ReportedFrom") RequestBody ReportedFrom, @Part("ReportedDateTime") RequestBody ReportedDateTime,
                                          @Part("DR") RequestBody DR, @Part("MobileDeviceID") RequestBody MobileDeviceID,
                                         @Part MultipartBody.Part photo
    );

*/


    @FormUrlEncoded
    @POST("/tracker/trackernew.asmx/SendMessage?")
    Call<UploadInstall> sendMessage(
            @Header("appversion") String header,
            @Field("Token") String Token,
            @Field("DeviceID") String DeviceID,
            @Field("MessageDescription") String MessageDescription,
            @Field("Lat") String Lat,
            @Field("Long") String Long,
            @Field("ReportedFrom") String ReportedFrom,
            @Field("ReportedDateTime") String ReportedDateTime,
            @Field("DR") String DR,
            @Field("MobileDeviceID") String MobileDeviceID,
            @Field("Photo") String Photo);


    @FormUrlEncoded
    @POST("/tracker/trackernew.asmx/GetCenterDetails?")
    Call<GetCat> GetCenterDetails(
            @Field("Token") String Token,
            @Field("DeviceID") String DeviceID);

    @FormUrlEncoded
    @POST("/tracker/trackernew.asmx/GetReportParameters?")
    Call<GetParams> GetParamDetails(
            @Field("Token") String Token,
            @Field("CategoryID") String CategoryID,
            @Field("DeviceID") String DeviceID,
            @Field("MobileDeviceID") String MobileDeviceID);


}
