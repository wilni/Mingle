package com.example.mingle.matches;

public class Matches {
    private String userID, name, profilePicUrl;

    public Matches(String userId, String name, String profilePicUrl){
        this.userID = userId;
        this.name = name;
        this.profilePicUrl = profilePicUrl;

    }

    public String getUserID() { return userID; }

    public void setUserID(String userID) { this.userID = userID; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getProfilePicUrl() { return profilePicUrl; }

    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
}
