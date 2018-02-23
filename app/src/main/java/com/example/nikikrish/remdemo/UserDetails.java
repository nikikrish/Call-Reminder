package com.example.nikikrish.remdemo;


public class UserDetails {

    private int id;
    private String contactName;
    private String contactNumber;
    private String callTime;
    private String enabled;
    private String duration;
    private String reminderTime;

    UserDetails(){};
    UserDetails(String name,String number,String callTime,String duration){
        this.contactName = name;
        this.contactNumber = number;
        this.callTime = callTime;
        this.duration = duration;
    }

    public void setId(int id) {
        this.id = id;
    }

    void setContactName(String contactName) {
        this.contactName = contactName;
    }

    void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    void setDuration(String duration) {
        this.duration = duration;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    String getContactName() {
        return contactName;
    }

    String getContactNumber() {
        return contactNumber;
    }

     String getCallTime() {
        return callTime;
    }

    public String getEnabled() {
        return enabled;
    }

    public String getDuration() {
        return duration;
    }

    public String getReminderTime() {
        return reminderTime;
    }


}
