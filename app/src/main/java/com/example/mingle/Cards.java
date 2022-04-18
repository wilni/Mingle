package com.example.mingle;

public class Cards {
    private String userID;
    private String name;
    private String profilePicUrl;

    public Cards(String userId, String name, String profilePicUrl){
        this.userID = userId;
        this.name = name;
        this.profilePicUrl = profilePicUrl;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePicUrl() { return profilePicUrl; }

    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }


}
