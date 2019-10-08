package com.legato.music;

import android.text.TextUtils;

import io.reactivex.annotations.Nullable;

public class Filters {

    @Nullable private String lookingfor = null;
    @Nullable private String genres = null;
    @Nullable private String skills = null;
    private String searchRadius;

    public Filters() {
        searchRadius = AppConstants.DEFAULT_SEARCH_RADIUS;
    }

    public static Filters getDefault() {
        Filters filters = new Filters();
        return filters;
    }

    public boolean hasLookingfor() {
        return !TextUtils.isEmpty(lookingfor);
    }

    public boolean hasGenres() {
        return !TextUtils.isEmpty(genres);
    }

    public boolean hasSkills() {
        return !TextUtils.isEmpty(skills);
    }

    public @Nullable String getLookingfor() {
        return lookingfor;
    }

    public void setLookingfor(@Nullable String lookingfor) {
        this.lookingfor = lookingfor;
    }

    public @Nullable String getGenres() {
        return genres;
    }

    public void setGenres(@Nullable String genres) {
        this.genres = genres;
    }

    public @Nullable String getSkills() { return skills; }

    public void setSkills(@Nullable String skills) {
        this.skills = skills;
    }

    public String getSearchRadius() { return searchRadius; }

    public void setSearchRadius(String searchRadius) { this.searchRadius = searchRadius;}
}
