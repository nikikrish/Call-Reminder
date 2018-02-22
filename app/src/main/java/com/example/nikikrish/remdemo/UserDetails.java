package com.example.nikikrish.remdemo;

/**
 * Created by Nikikrish on 22-Feb-18.
 */

public class UserDetails {

    int id;
    String contactName;
    String contactNumber;
    String callTime;
    String enabled;
    String duration;
    String reminderTime;
    String profilePath;

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

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }

    public int getId() {
        return id;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getCallTime() {
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

    public String getProfilePath() {
        return profilePath;
    }
}
