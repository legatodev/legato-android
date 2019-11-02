package com.legato.music;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

//A Model in MVVM
class NearbyUser {

    @NonNull private String distance;
    @NonNull private User user;

    private String[] youtubeIds = {};

    public NearbyUser(User user, String distance) {
        this.distance = distance;
        this.user = user;
    }

    public NearbyUser(String userId, String distance) {
        this.distance = distance;
        this.user = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, userId);
    }

    public String getUsername() {
        return user.getName();
    }

    public void setUsername(String username) {
        this.user.setName(username);
    }

    public String getPhotoUrl() {
        return user.getAvatarURL();
    }

    public void setPhotoUrl(String photourl) {
        this.user.setAvatarURL(photourl);
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getGenres() {
        return user.metaStringForKey(Keys.genres);
    }

    public void setGenres(@Nullable String genres) {    this.user.setMetaString(Keys.genres, genres);   }

    public String getSkills() {
        return user.metaStringForKey(Keys.skills);
    }

    public void setSkills(@Nullable String skills) {
        this.user.setMetaString(Keys.skills, skills);
    }

    public String getLookingfor() {
        return this.user.metaStringForKey(Keys.lookingfor);
    }

    public void setLookingfor(@Nullable String lookingfor) {
        this.user.setMetaString(Keys.lookingfor, lookingfor);
    }

    public String getEntityID() { return this.user.getEntityID(); }

    public String getAvailability() { return this.user.getAvailability(); }

    public String getInstagram() { return this.user.metaStringForKey(Keys.instagram); }

    public String getFacebook() { return this.user.metaStringForKey(Keys.facebook); }

    public String getYoutubeChannel() { return this.user.metaStringForKey(Keys.youtube_channel); }

    public boolean isMe() { return user.isMe(); }

    public String getEmail() { return user.getEmail(); }

    public String getYoutube() { return user.metaStringForKey(Keys.youtube); }

    public String[] getYoutubeIds() {
        if(youtubeIds.length == 0)
            return new String[] { user.metaStringForKey(Keys.youtube) };
        else
            return youtubeIds;
    }

    public User getUser() { return user;}
}
