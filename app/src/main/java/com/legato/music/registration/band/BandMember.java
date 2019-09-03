package com.legato.music.registration.band;

class BandMember {

    private String position;
    private String userEntityId;

    public BandMember(String position, String userEntityId) {
        this.position = position;
        this.userEntityId = userEntityId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getUserEntityId() {
        return userEntityId;
    }

    public void setUserEntityId(String userEntityId) {
        this.userEntityId = userEntityId;
    }
}
