package com.example.spoluri.legato.messaging;

class Chat {

    private String text;
    private String userId;
    private String photoUrl;

    public Chat() {
    }

    public Chat(String text, String userId, String photoUrl) {
        this.text = text;
        this.userId = userId;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
