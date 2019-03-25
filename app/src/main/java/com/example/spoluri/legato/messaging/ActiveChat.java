package com.example.spoluri.legato.messaging;

class ActiveChat {

    private String userName;
    private String photoUrl;
    private String participants;
    private String lastMessage;
    private String lastMessageTime;

    public ActiveChat() {
    }

    public ActiveChat(String userName, String photoUrl, String participants, String lastMessage, String lastMessageTime) {
        this.userName = userName;
        this.photoUrl = photoUrl;
        this.participants = participants;
        this.lastMessage = lastMessage;
        this.lastMessageTime = lastMessageTime;
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

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }
}
