package com.legato.music.registration.solo;

class Skill {

    private String skill;
    private int skillLevel;

    public Skill() {
        skill = "";
        skillLevel = 0;
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
