package com.legato.music.viewmodels;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.models.NearbyUser;
import com.legato.music.models.Skill;
import com.legato.music.repositories.BaseRepository;
import com.legato.music.views.adapters.SkillsAdapter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({TextUtils.class, Log.class})
public class SkillsViewModelTest {

    @Mock
    BaseRepository baseRepository;

    @Mock
    NearbyUser nearbyUser;

    @InjectMocks
    private SkillsViewModel skillsViewModel;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Handle empty strings for Textutils.isEmpty()
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(any(CharSequence.class))).thenAnswer(
                (Answer<Boolean>) invocation -> {
                    CharSequence a = (CharSequence) invocation.getArguments()[0];
                    return !(a != null && a.length() > 0);
                });
        // Handle null values for TextUtils.isEmpty()
        PowerMockito.when(TextUtils.isEmpty(any())).thenAnswer(
                ((Answer<Boolean>) invocation -> {
                    CharSequence a = (CharSequence) invocation.getArguments()[0];
                    return !(a != null && a.length() > 0);
                })
        );

        // Handle android.util.Log
        PowerMockito.mockStatic(android.util.Log.class);
        PowerMockito.when(Log.e(any(), any())).thenAnswer(
                (Answer<Integer>) invocation -> 0
        );

        nearbyUser = mock(NearbyUser.class);

        baseRepository = mock(BaseRepository.class);
        when(baseRepository.getCurrentUser()).thenReturn(nearbyUser);
    }

    @Test
    public void getInitialSkillsList() {
        skillsViewModel = new SkillsViewModel(baseRepository);

        // A newly generated view model starts off with a "Choose Skill" skill in its skills list.
        List<Skill> emptyList = Arrays.asList(
                new Skill("Choose Skill", 0, false));

        String expectedString = Arrays.deepToString(emptyList.toArray());
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertEquals(expectedString, actualString);
    }

    @Test
    public void getSkillsList() {
        List<Skill> testSkillsList = Arrays.asList(
                new Skill("Guitar", 0),
                new Skill("Piano", 2),
                new Skill("Bass", 4));

        skillsViewModel = new SkillsViewModel(baseRepository);

        for ( Skill s : testSkillsList ) {
            skillsViewModel.addSkill(s);
        }

        String expectedString = Arrays.deepToString(testSkillsList.toArray());
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertEquals(expectedString, actualString);
    }

    @Test
    public void getEmptySkillsList() {
        skillsViewModel = new SkillsViewModel(baseRepository);

        List<Skill> emptySkillsList = Arrays.asList(
                new Skill("Choose Skill", 0, false)
        );

        String expectedString = Arrays.deepToString(emptySkillsList.toArray());
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertEquals(expectedString, actualString);
    }

    @Test
    public void rejectAddingChooseSkill() {
        // NOTE:
        // 1: When a new view model is instantiated, the skills array list is empty.
        // 2: The getSkillsList() then returns a list with only a "Choose Skill" element in it
        //    if the skills array is empty.
        // 3: Therefore, to test the rejection of adding a "Choose Skill" skill object, we need
        //    to add a valid skill to the view model's skills array and the expectedList before
        //    attempting to add a "Choose Skill" skill object.

        Skill validSkill = new Skill("Guitar", 0, false);
        Skill chooseSkill = new Skill("Choose Skill", 0, false);

        skillsViewModel = new SkillsViewModel(baseRepository);
        skillsViewModel.addSkill(validSkill);

        List<Skill> expectedList = Collections.singletonList(validSkill);

        // Attempt to add the "Choose Skill" object
        skillsViewModel.addSkill(chooseSkill);

        String expectedString = Arrays.deepToString(expectedList.toArray());
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertEquals(expectedString, actualString);
    }

    @Test
    public void rejectAddingEmptyNameSkill() {
        Skill skill = new Skill("Guitar", 0, false);
        Skill emptyNameSkill = new Skill("", 0, false);

        skillsViewModel = new SkillsViewModel(baseRepository);

        // Add a valid skill to prevent getSkillsList from returning the "Choose Skill" object when the skillsList array is empty
        skillsViewModel.addSkill(skill);

        skillsViewModel.addSkill(emptyNameSkill);

        // A newly generated view model starts off with a "Choose Skill" skill in its skills list.
        List<Skill> emptyList = Collections.singletonList(skill);

        String expectedString = Arrays.deepToString(emptyList.toArray());
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertEquals(expectedString, actualString);
    }

    @Test
    public void getSkillsCount() {
        List<Skill> testSkillsList = Arrays.asList(
                new Skill("Guitar", 0),
                new Skill("Piano", 2),
                new Skill("Bass", 4));

        skillsViewModel = new SkillsViewModel(baseRepository);
        for (Skill s : testSkillsList ) {
            skillsViewModel.addSkill(s);
        }

        assertEquals(3, skillsViewModel.getSkillsCount());
    }

    @Test
    public void populateCurrentSkills() {
        String skillsList = "Guitar(Yes) - 5|Drums(No) - 2|Piano(Yes) - 2";

        skillsViewModel = new SkillsViewModel(baseRepository);

        when(nearbyUser.getSkills()).thenReturn(skillsList);

        String[] availableSkills = {"Guitar", "Piano"};
        skillsViewModel.populateCurrentSkills(availableSkills);

        String expectedString = "[Guitar(Yes) - 5, Piano(Yes) - 2]";
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertEquals(expectedString, actualString);
    }

    @Test
    public void addFirstSkill() {
        Skill skill = new Skill("Guitar", 1, false);

        skillsViewModel = new SkillsViewModel(baseRepository);
        skillsViewModel.addSkill(skill);

        // First skill should replace the "Choose Skill" when the skills list is empty
        String expectedString = Arrays.deepToString(Collections.singletonList(skill).toArray());
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertEquals(expectedString, actualString);
    }

    @Test
    public void addSecondSkill() {
        Skill skill = new Skill("Guitar", 1, false);
        Skill secSkill = new Skill("Piano", 5, true);

        skillsViewModel = new SkillsViewModel(baseRepository);
        skillsViewModel.addSkill(skill);
        skillsViewModel.addSkill(secSkill);

        // First skill should replace the "Choose Skill" when the skills list is empty
        String expectedString = String.format(
                "%s(%s) - %d, %s(%s) - %d",
                "Guitar", "No", 1,
                "Piano", "Yes", 5
        );
        String actualString = Arrays.deepToString(skillsViewModel.getSkillsList().toArray());

        assertNotEquals(expectedString, actualString);
    }

    @Test
    public void isInvalidSkillsArray() {
        skillsViewModel = new SkillsViewModel(baseRepository);

        skillsViewModel.addSkill(new Skill("Guitar", 5));
        skillsViewModel.addSkill(new Skill("Piano", 5));
        skillsViewModel.addSkill(new Skill ("Keyboard", 3));
        skillsViewModel.addSkill(new Skill("Saxophone", 1));
        skillsViewModel.addSkill(new Skill("Bass", 3));
        skillsViewModel.addSkill(new Skill("Cow Bell", 5));
        skillsViewModel.addSkill(new Skill("Drums", 1));

        assertFalse(skillsViewModel.isValidSkillsArray());
    }

    @Test
    public void isValidSkillsArray() {
        skillsViewModel = new SkillsViewModel(baseRepository);

        skillsViewModel.addSkill(new Skill("Guitar", 5));
        skillsViewModel.addSkill(new Skill("Piano", 5));
        skillsViewModel.addSkill(new Skill ("Keyboard", 3));
        skillsViewModel.addSkill(new Skill("Saxophone", 1));
        skillsViewModel.addSkill(new Skill("Bass", 3));
        skillsViewModel.addSkill(new Skill("Cow Bell", 5));

        assertTrue(skillsViewModel.isValidSkillsArray());
    }

    @Test
    public void extractDataNullRecyclerView() {
        @Nullable RecyclerView recyclerView = null;
        SkillsAdapter adapter = mock(SkillsAdapter.class);

        skillsViewModel = new SkillsViewModel(baseRepository);

        assertEquals("", skillsViewModel.extractData(recyclerView, adapter));
    }

    @Test
    public void extractDataNullSkillsAdapter() {
        RecyclerView recyclerView = mock(RecyclerView.class);
        @Nullable SkillsAdapter adapter = null;

        skillsViewModel = new SkillsViewModel(baseRepository);

        assertEquals("", skillsViewModel.extractData(recyclerView, adapter));
    }

    @Test
    public void extractValidData() {
        RecyclerView recyclerView = mock(RecyclerView.class);
        SkillsAdapter adapter = mock(SkillsAdapter.class);

        SkillsAdapter.SkillsHolder guitar = mock(SkillsAdapter.SkillsHolder.class);
        SkillsAdapter.SkillsHolder piano = mock(SkillsAdapter.SkillsHolder.class);

        when(guitar.getSkill()).thenReturn(new Skill("Guitar", 5));
        when(piano.getSkill()).thenReturn(new Skill("Piano", 3));

        when(adapter.getItemCount()).thenReturn(2);
        when(recyclerView
                .findViewHolderForAdapterPosition(0))
                .thenReturn(guitar);
        when(recyclerView
                .findViewHolderForAdapterPosition(1))
                .thenReturn(piano);

        assertEquals("Guitar(No) - 5|Piano(No) - 3|", skillsViewModel.extractData(recyclerView, adapter));
    }
}