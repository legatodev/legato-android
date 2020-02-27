package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.legato.music.models.NearbyUser;
import com.legato.music.models.UserProfileInfo;
import com.legato.music.repositories.BaseRepository;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.chatsdk.core.dao.User;
import io.reactivex.Completable;

public class UserProfileViewModel extends ViewModel {
    private BaseRepository baseRepository;
    private NearbyUser nearbyUser;

    private boolean startingChat;

    public UserProfileViewModel(BaseRepository baseRepository) {
        this.baseRepository = baseRepository;

        nearbyUser = baseRepository.getCurrentUser();

        startingChat = false;
    }

    public ArrayList<UserProfileInfo> getProfileInfo() {
        ArrayList<UserProfileInfo> userProfileInfos = new ArrayList<>();
        if (nearbyUser.getSkills() != null) {
            userProfileInfos.add(new UserProfileInfo("Skills", nearbyUser.getSkills()));
        }
        if (nearbyUser.getGenres() != null) {
            userProfileInfos.add(new UserProfileInfo("Genres", nearbyUser.getGenres()));
        }
        if (nearbyUser.getLookingfor() != null) {
            userProfileInfos.add(new UserProfileInfo("Looking for", nearbyUser.getLookingfor()));
        }

        return userProfileInfos;
    }

    public boolean getStartingSdkChat() {
        return startingChat;
    }

    public void setStartingSdkChat(boolean state) { startingChat = state; }

    public User getUser() {
        return nearbyUser.getUser();
    }

    public NearbyUser getUserByEntityId(String entityId) {
        User user = baseRepository.getUserByEntityId(entityId);
        nearbyUser.setUser(user);
        nearbyUser.setDistance(baseRepository.getDistanceFromCurrentUser(user));

        return nearbyUser;
    }

    public String getDistance() {
        return nearbyUser.getDistance();
    }

    public void setDistance(String distance) {
        nearbyUser.setDistance(distance);
    }

    public List<String> getYoutubeVideoIds() {
        return nearbyUser.getYoutubeSamples().getTrackIds();
    }

    public void setYoutubeVideoIds(String videoIds) {
        nearbyUser.getYoutubeSamples().setTrackIds(videoIds);
    }

    public List<String> getSpotifyTrackIds() {
        return nearbyUser.getSpotifySamples().getTrackIds();
    }

    public void setSpotifyTrackIds(String trackIds) {
        nearbyUser.getSpotifySamples().setTrackIds(trackIds);
    }

    public Completable addContact() {
        return baseRepository.addContact(nearbyUser.getUser());
    }

    public Completable deleteContact() {
        return baseRepository.deleteContact(nearbyUser.getUser());
    }

    public boolean isFriend() {
        return baseRepository.isFriend(nearbyUser.getUser());
    }

    public String getSpotifyAccessToken() {
        return baseRepository.getSpotifyAccessToken();
    }

    public void setSpotifyAccessToken(String spotifyAccessToken) {
        baseRepository.setSpotifyAccessToken(spotifyAccessToken);
    }

    public void logout(){
        baseRepository.destroyInstance();
    }
}
