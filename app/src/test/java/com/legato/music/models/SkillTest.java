package com.legato.music.models;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class SkillTest {

    @Test
    public void getSkill_Blank() {
        // TODO: This should throw an exception
        Skill blankSkill = new Skill("", 1);
        assertEquals("", blankSkill.getSkill());
    }

    @Test
    public void setSkill_BlankText() {
        // TODO: This should throw an exception
        Skill guitarSkill = new Skill();
        guitarSkill.setSkill("");
        assertEquals("", guitarSkill.getSkill());
    }

    @Test
    public void getSkillLevel_Initialized() {
        Skill zeroSkill = new Skill();
        assertEquals(0, zeroSkill.getSkillLevel());
    }

    @Test
    public void sortBySkillLevel() {
        ArrayList<Skill> skillList = new ArrayList<>();
        skillList.addAll(
            Arrays.asList(
                new Skill("Guitar", 1),
                new Skill("Piano", 3),
                new Skill("Bass", 2)
            )
        );

        Collections.sort(skillList, Skill.sortBySkillLevel);

        assertEquals(3, skillList.get(0).getSkillLevel());
        assertEquals(2, skillList.get(1).getSkillLevel());
        assertEquals(1, skillList.get(2).getSkillLevel());
    }
}