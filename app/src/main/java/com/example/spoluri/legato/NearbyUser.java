package com.example.spoluri.legato;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;

class NearbyUser {

    private String distance;
    private User user;

    public NearbyUser() {
    }

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

    public String getPhotourl() {
        return user.getAvatarURL();
    }

    public void setPhotourl(String photourl) {
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

    public void setGenres(String genres) {    this.user.setMetaString(Keys.genres, genres);   }

    public String getSkills() {
        return user.metaStringForKey(Keys.skills);
    }

    public void setSkills(String skills) {
        this.user.setMetaString(Keys.skills, skills);
    }

    public String getLookingfor() {
        return this.user.metaStringForKey(Keys.lookingfor);
    }

    public void setLookingfor(String lookingfor) {
        this.user.setMetaString(Keys.lookingfor, lookingfor);
    }
}
