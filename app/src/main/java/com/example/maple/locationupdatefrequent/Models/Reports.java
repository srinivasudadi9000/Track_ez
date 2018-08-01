package com.example.maple.locationupdatefrequent.Models;

public class Reports {

    String messagedescription,messagedatetime,Photo;
    public Reports(String messagedescription, String Photo, String messagedatetime){
        this.messagedescription = messagedescription;this.Photo = Photo;this.messagedatetime = messagedatetime;
    }

    public String getMessagedescription() {
        return messagedescription;
    }

    public void setMessagedescription(String messagedescription) {
        this.messagedescription = messagedescription;
    }

    public String getMessagedatetime() {
        return messagedatetime;
    }

    public void setMessagedatetime(String messagedatetime) {
        this.messagedatetime = messagedatetime;
    }

    public String getPhoto() {
        return Photo;
    }

    public void setPhoto(String photo) {
        Photo = photo;
    }
}


