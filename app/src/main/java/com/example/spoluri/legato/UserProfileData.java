package com.example.spoluri.legato;

public class UserProfileData {
    private String userName;
    private String skills;
    private String genres;
    private String youtube_video;

    UserProfileData(){}

    UserProfileData(String userName, String skills, String genres, String youtube_video) {
        this.userName = userName;
        this.skills = skills;
        this.genres = genres;
        this.youtube_video = youtube_video;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
