package com.example.spoluri.legato.messaging;

class Chat {

    private String text;
    private String userId;
    private String userName;
    private Object timeStamp;

    public Chat() {
    }

    public Chat(String text, String userId, Object timestamp) {
        this.text = text;
        this.userId = userId;
        this.timeStamp = timestamp;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }
}
