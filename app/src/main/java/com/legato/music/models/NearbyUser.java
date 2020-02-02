package com.legato.music.models;

import android.text.TextUtils;

import com.legato.music.utils.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import co.chatsdk.core.dao.User;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

//A Model in MVVM
public class NearbyUser {

    @NonNull private String distance = "0";
    @NonNull private User user = new User();
    private MusicSamples youtubeSamples;
    private MusicSamples spotifySamples;

    public NearbyUser(User user, String distance) {
        setDistance(distance);
        setUser(user);

        updateYoutubeVideoIds();
        updateSpotifyTrackIds();
    }

    private void updateYoutubeVideoIds() {
        youtubeSamples = new MusicSamples(Keys.youtube);
    }

    private void updateSpotifyTrackIds() {
        spotifySamples = new MusicSamples(Keys.spotify_track);
    }

    public String getUsername() {
        return user.getName();
    }

    public void setUsername(String username) {
        this.user.setName(username);
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        if (distance != null)
            this.distance = distance;
    }

    public @Nullable String getGenres() {
        String genres = user.metaStringForKey(Keys.genres);
        return (genres == null) ? "" : genres;
    }

    public void setGenres(@Nullable String genres) {
        this.user.setMetaString(Keys.genres, genres);
    }

    public @Nullable String getSkills() {
        String skills = user.metaStringForKey(Keys.skills);
        return (skills == null) ? "" : skills;
    }

    public void setSkills(@Nullable String skills) {
        this.user.setMetaString(Keys.skills, skills);
    }

    public String getLookingfor() {
        String lookingFor = this.user.metaStringForKey(Keys.lookingfor);
        return (lookingFor == null) ? "" : lookingFor;
    }

    public void setLookingfor(@Nullable String lookingfor) {
        this.user.setMetaString(Keys.lookingfor, lookingfor);
    }

    public String getEntityID() {
        String entityID = this.user.getEntityID();
        return (entityID == null) ? "" : entityID;
    }

    public String getAvailability() {
        String availability = this.user.getAvailability();
        return (availability == null) ? "" : availability;
    }

    public String getAvatarUrl() {
        String avatarUrl = user.getAvatarURL();
        return (avatarUrl == null) ? "" : avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) { user.setAvatarURL(avatarUrl); }

    public String getDescription() {
        String description = this.user.metaStringForKey(Keys.user_description);
        return (description == null) ? "" : description;
    }

    public void setDescription(String newDescription) { this.user.setMetaString(Keys.user_description, newDescription); }

    public String getInstagram() {
        String instagram = this.user.metaStringForKey(Keys.instagram);
        return (instagram == null) ? "" : instagram;
    }

    public String getFacebook() {
        String facebook = this.user.metaStringForKey(Keys.facebook);
        return (facebook == null) ? "" : facebook;
    }

    public void setFacebookPageId(String pageId) {
        this.user.setMetaString(Keys.facebook_page_id, pageId);
    }

    public String getFacebookPageId() {
        String pageId = this.user.metaStringForKey(Keys.facebook_page_id);
        return (pageId == null) ? "" : pageId;
    }

    public String getYoutubeChannel() {
        String youtubeChannel = this.user.metaStringForKey(Keys.youtube_channel);
        return (youtubeChannel == null) ? "" : youtubeChannel;
    }

    public boolean isMe() { return user.isMe(); }

    public String getEmail() {
        String email = user.getEmail();
        return (email == null) ? "" : email;
    }

    public String getYoutube() {
        String youtube = user.metaStringForKey(Keys.youtube);
        return (youtube == null) ? "" : youtube;
    }

    public String getSpotify() {
        String spotify = user.metaStringForKey(Keys.spotify_track);
        return (spotify == null) ? "" : spotify;
    }

    public MusicSamples getYoutubeSamples() { return youtubeSamples; }

    public MusicSamples getSpotifySamples() { return spotifySamples; }

    public User getUser() { return user;}

    public void setUser(User user) {
        if (user != null)
            this.user = user;
    }

    public static Comparator<NearbyUser> sortByDistance = (u1, u2) -> {
        Double u1Dist = Double.parseDouble(u1.getDistance());
        Double u2Dist = Double.parseDouble(u2.getDistance());

        return u1Dist < u2Dist ? -1 :
                u1Dist > u2Dist ? 1 : 0;
    };

    public class MusicSamples {
        @androidx.annotation.NonNull
        private ArrayList<String> trackIds;
        @androidx.annotation.NonNull
        private String key;

        MusicSamples(@androidx.annotation.NonNull String key) {
            this.key = key;
            trackIds = new ArrayList<>();
            setTrackIds(user.metaStringForKey(this.key));
        }

        public boolean addTrackId(String newTrackId) {
            boolean bSuccess = false;

            if (!TextUtils.isEmpty(newTrackId) && trackIds.size() < 6) {
                if (!trackIds.contains(newTrackId)) {
                    trackIds.add(newTrackId);
                }
                bSuccess = true;
            }

            return bSuccess;
        }

        public boolean removeTrackId(String trackId) {
            boolean bSuccess = false;

            if (!TextUtils.isEmpty(trackId) && !trackIds.isEmpty()) {
                trackIds.remove(trackId);

                bSuccess = true;
            }

            return bSuccess;
        }

        public List<String> getTrackIds() {
            if (!trackIds.isEmpty() && !trackIds.get(0).isEmpty()) {
                return trackIds;
            }

            trackIds.clear();

            return trackIds;
        }

        public String getTrackIdsAsString() {
            return sanitizeTrackIdsString(getTrackIds().toString());
        }

        public void setTrackIds(String trackIdsString) {
            trackIdsString = sanitizeTrackIdsString(trackIdsString);

            trackIds = new ArrayList<>(Arrays.asList(trackIdsString.split(",")));
        }

        public void resetTrackIds() {
            user.setMetaString(key, "");
            trackIds.clear();
        }

        private String sanitizeTrackIdsString(String trackIds) {
        /*
            This function is used to clean up strings within the ChatSdk that could have added
            space characters between elements, such as a List<>.toString() call,
            and brackets from json arrays that could be returned with comma separated values
            e.g., "[videoId1,videoId2,videoId3]"
         */
            if (trackIds != null) {
                trackIds =  trackIds
                        .replace(" ", "")
                        .replace("[", "")
                        .replace("]", "");
            } else {
                trackIds = "";
            }

            return trackIds;
        }
    }
}
