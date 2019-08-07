package com.legato.music.registration.solo;

import java.io.Serializable;

class Skill implements Serializable {

    private String skill;
    private int skillLevel = 0;

    public Skill() {
    }

    public Skill(String skill, int skillLevel) {
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
