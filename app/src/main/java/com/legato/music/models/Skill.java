package com.legato.music.models;

import java.util.Comparator;

public class Skill {

    private String skill;
    private int skillLevel;
    private boolean ownsInstrument;

    public Skill() {
        skill = "";
        skillLevel = 0;
        ownsInstrument = false;
    }

    public Skill(String skill, int skillLevel) {
        this.skill = skill;
        this.skillLevel = skillLevel;
    }

    public Skill(String skill, int skillLevel, boolean ownsInstrument) {
        this.skill = skill;
        this.skillLevel = skillLevel;
        this.ownsInstrument = ownsInstrument;
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

    public boolean getOwnsInstrument() {
        return ownsInstrument;
    }

    public void setOwnsInstrument(boolean ownsInstrument) {
        this.ownsInstrument = ownsInstrument;
    }

    public static Comparator<Skill> sortBySkillLevel = (u1, u2) -> {
        Integer u1SkillLevel = u1.getSkillLevel();
        Integer u2SkillLevel = u2.getSkillLevel();

        return u1SkillLevel > u2SkillLevel ? -1 :
               u1SkillLevel < u2SkillLevel ? 1 : 0;
    };
}
