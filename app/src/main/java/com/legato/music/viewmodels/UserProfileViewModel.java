package com.legato.music.viewmodels;

import androidx.lifecycle.ViewModel;

import com.legato.music.models.NearbyUser;
import com.legato.music.models.UserProfileInfo;
import com.legato.music.repositories.BaseRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import co.chatsdk.core.dao.User;
import io.reactivex.Completable;

public class UserProfileViewModel extends ViewModel {
    private BaseRepository baseRepository;
    private NearbyUser nearbyUser;

    private boolean startingChat;

    public UserProfileViewModel() {
        baseRepository = BaseRepository.getInstance();

        nearbyUser = baseRepository.getCurrentUser();

        startingChat = false;
    }

    public ArrayList<UserProfileInfo> getProfileInfo() {
        return new ArrayList<>(Arrays.asList(
            new UserProfileInfo("Skills", nearbyUser.getSkills()),
            new UserProfileInfo("Genres", nearbyUser.getGenres()),
            new UserProfileInfo("Looking for", nearbyUser.getLookingfor())
        ));
    }

    public boolean getStartingSdkChat() {
        return startingChat;
    }

    public void setStartingSdkChat(boolean state) { startingChat = state; }

    public User getUser() {
        return nearbyUser.getUser();
    }

    public NearbyUser getUserByEntityId(String entityId) {
        nearbyUser.setUser(baseRepository.getUserByEntityId(entityId));
        nearbyUser.setDistance(baseRepository.getDistanceFromCurrentUser());

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
}
