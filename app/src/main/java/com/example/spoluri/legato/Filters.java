package com.example.spoluri.legato;

import android.text.TextUtils;

public class Filters {

    private String lookingfor = null;
    private String genres = null;
    private String skills = null;

    public Filters() {}

    public boolean hasLookingfor() {
        return !(TextUtils.isEmpty(lookingfor));
    }
    public boolean hasGenres() {
        return !(TextUtils.isEmpty(genres));
    }
    public boolean hasSkills() {
        return !(TextUtils.isEmpty(skills));
    }
    public String getLookingfor() {
        return lookingfor;
    }

    public void setLookingfor(String lookingfor) {
        this.lookingfor = lookingfor;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }
}
