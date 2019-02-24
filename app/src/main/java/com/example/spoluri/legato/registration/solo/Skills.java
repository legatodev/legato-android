package com.example.spoluri.legato.registration.solo;

import java.io.Serializable;

class Skills implements Serializable {

    private String skill;
    private int skillLevel = 0;

    public Skills() {
    }

    public Skills(String skill, int skillLevel) {
        this.skill = skill;
        this.skillLevel = skillLevel;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public int getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(int skillLevel) {
        this.skillLevel = skillLevel;
    }
}
