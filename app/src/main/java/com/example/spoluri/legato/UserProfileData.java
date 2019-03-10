package com.example.spoluri.legato;

import java.io.Serializable;

public class UserProfileData implements Serializable {
    private String username;
    private String skills;
    private String genres;
    private String youtube_video;
    private String photourl;
    private String lookingfor;

    UserProfileData(){}

    UserProfileData(String username, String skills, String genres, String youtube_video, String photourl, String lookingfor) {
        this.username = username;
        this.skills = skills;
        this.genres = genres;
        this.youtube_video = youtube_video;
        this.photourl = photourl;
        this.setLookingfor(lookingfor);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getYoutube_video() {
        return youtube_video;
    }

    public void setYoutube_video(String youtube_video) {
        this.youtube_video = youtube_video;
    }

    public String getPhotourl() {
        return photourl;
    }

    public void setPhotourl(String photourl) {
        this.photourl = photourl;
    }

    public String getLookingfor() {
        return lookingfor;
    }

    public void setLookingfor(String lookingfor) {
        this.lookingfor = lookingfor;
    }
}
