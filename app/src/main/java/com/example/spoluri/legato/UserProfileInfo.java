package com.example.spoluri.legato;

class UserProfileInfo {

    private String title;
    private String data;

    public UserProfileInfo() {
    }

    public UserProfileInfo(String title, String data) {
        this.title = title;
        this.data = data;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
