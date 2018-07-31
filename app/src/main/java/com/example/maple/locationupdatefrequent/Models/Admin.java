package com.example.maple.locationupdatefrequent.Models;

public class Admin {

    String Message,MessageDateTime;
    int MessageID,ReadStatus;

    public Admin(String Message, int MessageID, String MessageDateTime, int ReadStatus){
        this.Message= Message;this.MessageID=MessageID;this.MessageDateTime= MessageDateTime;this.ReadStatus = ReadStatus;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getMessageID() {
        return MessageID;
    }

    public void setMessageID(int messageID) {
        MessageID = messageID;
    }

    public String getMessageDateTime() {
        return MessageDateTime;
    }

    public void setMessageDateTime(String messageDateTime) {
        MessageDateTime = messageDateTime;
    }

    public int getReadStatus() {
        return ReadStatus;
    }

    public void setReadStatus(int readStatus) {
        ReadStatus = readStatus;
    }
}
