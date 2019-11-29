package com.legato.music.models;

import com.legato.music.utils.Keys;

import org.apache.commons.lang3.ObjectUtils;

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

    @NonNull private ArrayList<String> youtubeVideoIds;

    public NearbyUser(User user, String distance) {
        setDistance(distance);
        setUser(user);

        updateYoutubeVideoIds();
    }

    public NearbyUser(String userId, String distance) {
        setDistance(distance);
        setUser(userId);

        updateYoutubeVideoIds();
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
        if (distance != null)
            this.distance = distance;
    }

    public String getGenres() {
        return user.metaStringForKey(Keys.genres);
    }

    public void setGenres(@Nullable String genres) {
        this.user.setMetaString(Keys.genres, genres);
    }

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

    public String getAvatarUrl() { return user.getAvatarURL(); }

    public void setAvatarUrl(String avatarUrl) { user.setAvatarURL(avatarUrl); }

    public String getInstagram() { return this.user.metaStringForKey(Keys.instagram); }

    public String getFacebook() { return this.user.metaStringForKey(Keys.facebook); }

    public String getYoutubeChannel() {
        return this.user.metaStringForKey(Keys.youtube_channel);
    }

    public String getSpotifyTrack() { return user.metaStringForKey(Keys.spotify_track); }

    public boolean isMe() { return user.isMe(); }

    public String getEmail() { return user.getEmail(); }

    public String getYoutube() { return user.metaStringForKey(Keys.youtube); }

    public boolean addYoutubeVideoId(String newVideoId) {
        boolean bSuccess = false;

        if (youtubeVideoIds.size() < 6) {
            if (!youtubeVideoIds.contains(newVideoId)) {
                youtubeVideoIds.add(newVideoId);
            }
            bSuccess = true;
        }

        return bSuccess;
    }

    public boolean removeYoutubeVideoId(int position) {
        boolean bSuccess = false;

        if (!youtubeVideoIds.isEmpty()) {
            youtubeVideoIds.remove(position);

            bSuccess = true;
        }

        return bSuccess;
    }

    public List<String> getYoutubeVideoIds() {
        if (youtubeVideoIds.isEmpty()) {
            setYoutubeVideoIds(user.metaStringForKey(Keys.youtube));
        }

        if (!youtubeVideoIds.isEmpty() && !youtubeVideoIds.get(0).isEmpty()) {
            return youtubeVideoIds;
        }

        youtubeVideoIds = new ArrayList<>();

        return youtubeVideoIds;
    }

    public String getYoutubeVideoIdsAsString() {
        return sanitizeVideoIdsString(getYoutubeVideoIds().toString());
    }

    public void setYoutubeVideoIds(String videoIds) {
        videoIds = sanitizeVideoIdsString(videoIds);

        if (videoIds.contains(",")) {
            youtubeVideoIds = new ArrayList<>(Arrays.asList(videoIds.split(",")));
        }
        else {
            youtubeVideoIds = new ArrayList<>(Arrays.asList(videoIds));
        }
    }

    private void updateYoutubeVideoIds() {
        youtubeVideoIds = new ArrayList<>();

        if (this.user != null)
            setYoutubeVideoIds(this.user.metaStringForKey(Keys.youtube));
    }

    public void resetYoutubeVideoIds() {
        user.setMetaString(Keys.youtube, "");
        youtubeVideoIds = new ArrayList<>();
    }

    private String sanitizeVideoIdsString(String videoIds) {
        /*
            This function is used to clean up strings within the ChatSdk that could have added
            space characters between elements, such as a List<>.toString() call,
            and brackets from json arrays that could be returned with comma separated values
            e.g., "[videoId1,videoId2,videoId3]"
         */
        if(videoIds == null)
            return " ";

        return videoIds
                .replace(" ", "")
                .replace("[","")
                .replace("]","");
    }

    public User getUser() { return user;}

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


}
