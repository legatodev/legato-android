package com.example.spoluri.legato;

import java.io.Serializable;

class NearbyUser implements Serializable {

    private String userName;
    private String photoUrl;
    private String distance;
    private String genres;
    private String skills;

    public NearbyUser() {
    }

    public NearbyUser(String userName, String photoUrl, String distance, String genres, String skills) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.distance = distance;
        this.genres = genres;
        this.skills = skills;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {        this.genres = genres;    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
