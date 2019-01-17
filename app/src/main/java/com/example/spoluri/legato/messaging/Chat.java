package com.example.spoluri.legato.messaging;

class Chat {

    private String text;
    private String userName;
    private String photoUrl;

    public Chat() {
    }

    public Chat(String text, String userName, String photoUrl) {
        this.text = text;
        this.userName = userName;
        this.photoUrl = photoUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
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
}
