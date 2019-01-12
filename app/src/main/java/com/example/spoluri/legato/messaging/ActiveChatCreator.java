package com.example.spoluri.legato.messaging;

class ActiveChatCreator {

    private String userId;
    private String photoUrl;
    private String participants;

    public ActiveChatCreator() {
    }

    public ActiveChatCreator(String userId, String photoUrl, String participants) {
        this.userId = userId;
        this.photoUrl = photoUrl;
        this.participants = participants;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getParticipants() {
        return participants;
    }

    public void setParticipants(String participants) {
        this.participants = participants;
    }
}
