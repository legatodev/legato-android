package com.legato.music.models;

import com.legato.music.utils.Keys;

import java.util.Comparator;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

//A Model in MVVM
public class NearbyUser {

    @NonNull private String distance;
    @NonNull
    private User user;

    @NonNull private String[] youtubeVideoIds;

    public NearbyUser(User user, String distance) {
        this.distance = distance;
        this.user = user;
        youtubeVideoIds = new String[] {};
    }

    public NearbyUser(String userId, String distance) {
        this.distance = distance;
        this.user = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, userId);
        youtubeVideoIds = new String[] {};
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

    public void setPhotoUrl(String photoUrl) {
        this.user.setAvatarURL(photoUrl);
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

    public String[] getYoutubeVideoIds() {
        if (youtubeVideoIds.length > 0)
            return youtubeVideoIds;
        else
            return new String[] { getYoutube() };
    }

    public User getUser() { return user;}

    public static Comparator<NearbyUser> sortByDistance = new Comparator<NearbyUser>() {
        public int compare(NearbyUser u1, NearbyUser u2){
                    return Double.parseDouble(u1.getDistance()) < Double.parseDouble(u2.getDistance())? -1
                    : Double.parseDouble(u1.getDistance()) > Double.parseDouble(u2.getDistance())? 1
                    :0;
        }
    };


}
