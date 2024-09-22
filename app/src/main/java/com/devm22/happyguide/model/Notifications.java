package com.devm22.happyguide.model;

public class Notifications {
    private int notificationId;
    private String notificationTitle;
    private String notificationMessage;
    private String notificationIcon;
    private String notificationLargeIcon;
    private String notificationPicture;
    private String notificationLaunchURL;
    private String notificationSendingDate;
    private Boolean notificationShowed;


    public Notifications(int notificationId, String notificationTitle, String notificationMessage, String notificationIcon, String notificationLargeIcon, String notificationPicture, String notificationLaunchURL, String notificationSendingDate, Boolean notificationShowed) {
        this.notificationId = notificationId;
        this.notificationTitle = notificationTitle;
        this.notificationMessage = notificationMessage;
        this.notificationIcon = notificationIcon;
        this.notificationLargeIcon = notificationLargeIcon;
        this.notificationPicture = notificationPicture;
        this.notificationLaunchURL = notificationLaunchURL;
        this.notificationSendingDate = notificationSendingDate;
        this.notificationShowed = notificationShowed;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationMessage() {
        return notificationMessage;
    }

    public void setNotificationMessage(String notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    public String getNotificationIcon() {
        return notificationIcon;
    }

    public void setNotificationIcon(String notificationIcon) {
        this.notificationIcon = notificationIcon;
    }

    public String getNotificationLargeIcon() {
        return notificationLargeIcon;
    }

    public void setNotificationLargeIcon(String notificationLargeIcon) {
        this.notificationLargeIcon = notificationLargeIcon;
    }

    public String getNotificationPicture() {
        return notificationPicture;
    }

    public void setNotificationPicture(String notificationPicture) {
        this.notificationPicture = notificationPicture;
    }

    public String getNotificationLaunchURL() {
        return notificationLaunchURL;
    }

    public void setNotificationLaunchURL(String notificationLaunchURL) {
        this.notificationLaunchURL = notificationLaunchURL;
    }

    public String getNotificationSendingDate() {
        return notificationSendingDate;
    }

    public void setNotificationSendingDate(String notificationSendingDate) {
        this.notificationSendingDate = notificationSendingDate;
    }

    public Boolean getNotificationShowed() {
        return notificationShowed;
    }

    public void setNotificationShowed(Boolean notificationShowed) {
        this.notificationShowed = notificationShowed;
    }


}
