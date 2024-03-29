package com.legato.music.models;

import android.text.TextUtils;

import com.legato.music.utils.Conversions;
import com.legato.music.utils.Keys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
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

    public NearbyUser(String userId, String distance) {
        setDistance(distance);
        setUser(userId);

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
        return user.metaStringForKey(Keys.genres);
    }

    public void setGenres(@Nullable String genres) {
        this.user.setMetaString(Keys.genres, genres);
    }

    public @Nullable String getSkills() {
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

    public String getAvatarUrl() { return user.getAvatarURL(); }

    public void setAvatarUrl(String avatarUrl) { user.setAvatarURL(avatarUrl); }

    public String getDescription() { return this.user.metaStringForKey(Keys.user_description); }

    public void setDescription(String newDescription) { this.user.setMetaString(Keys.user_description, newDescription); }

    public void setInstagram(String handle) {
        this.user.setMetaString(
                Keys.instagram,
                Conversions.getConvertedSocialMediaHandle(handle));
    }

    public String getInstagram() {
        return Conversions.getConvertedSocialMediaHandle(
                this.user.metaStringForKey(Keys.instagram));
    }

    public String getFacebook() {
        return Conversions.getConvertedSocialMediaHandle(
                this.user.metaStringForKey(Keys.facebook));
    }

    public void setFacebookPageId(String pageId) {
        this.user.setMetaString(Keys.facebook_page_id, pageId);
    }

    public String getFacebookPageId() { return this.user.metaStringForKey(Keys.facebook_page_id); }

    public void setYoutubeChannel(String channelName) {
        this.user.setMetaString(
                Keys.youtube_channel,
                Conversions.getConvertedSocialMediaHandle(channelName));
    }

    public String getYoutubeChannel() {
        return Conversions.getConvertedSocialMediaHandle(
                this.user.metaStringForKey(Keys.youtube_channel));
    }

    public boolean isMe() { return user.isMe(); }

    public String getEmail() { return user.getEmail(); }

    public String getYoutube() { return user.metaStringForKey(Keys.youtube); }
    public String getSpotify() { return user.metaStringForKey(Keys.spotify_track); }

    public MusicSamples getYoutubeSamples() { return youtubeSamples; }
    public MusicSamples getSpotifySamples() { return spotifySamples; }

    public User getUser() { return user;}

    //TODO: Why chatsdk being accessed directly?
    public void setUser(String userId) {
        this.user = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, userId);
    }

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
