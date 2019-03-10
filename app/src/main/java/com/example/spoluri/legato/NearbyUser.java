package com.example.spoluri.legato;

import java.io.Serializable;

class NearbyUser implements Serializable {

    private UserProfileData userProfileData;
    private String distance;

    public NearbyUser() {
    }

    public NearbyUser(UserProfileData userProfileData, String distance) {
        this.userProfileData = userProfileData;
        this.distance = distance;
    }

    public String getUsername() {
        return userProfileData.getUsername();
    }

    public void setUsername(String username) {
        this.userProfileData.setUsername(username);
    }

    public String getPhotourl() {
        return userProfileData.getPhotourl();
    }

    public void setPhotourl(String photourl) {
        this.userProfileData.setUsername(photourl);
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGenres() {
        return userProfileData.getGenres();
    }

    public void setGenres(String genres) {    this.userProfileData.setGenres(genres);    }

    public String getSkills() {
        return userProfileData.getSkills();
    }

    public void setSkills(String skills) {
        this.userProfileData.setSkills(skills);
    }

    public String getLookingfor() {
        return this.userProfileData.getLookingfor();
    }

    public void setLookingfor(String lookingfor) {
        this.userProfileData.setLookingfor(lookingfor);
    }
}
