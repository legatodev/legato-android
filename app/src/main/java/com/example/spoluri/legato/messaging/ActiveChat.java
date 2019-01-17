package com.example.spoluri.legato.messaging;

class ActiveChat {

    private String userName;
    private String photoUrl;
    private String participants;

    public ActiveChat() {
    }

    public ActiveChat(String userName, String photoUrl, String participants) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.participants = participants;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
