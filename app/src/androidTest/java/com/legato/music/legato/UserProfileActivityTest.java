package com.legato.music.legato;


import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewGroup;


import androidx.fragment.app.testing.FragmentScenario;

import androidx.test.espresso.ViewInteraction;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import com.legato.music.R;
import com.legato.music.utils.Keys;
import com.legato.music.views.fragments.UserProfileFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.chatsdk.core.session.ChatSDK;

/*
TODO: Added display profile check. Compare the file name as assertion criteria.
 */
@RunWith(AndroidJUnit4.class)
public class UserProfileActivityTest extends FireBaseTest {

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    private static String VALID_USERNAME = "";
    private static String VALID_SKILLS_LIST = "";
    private static String VALID_GENRES_LIST = "";

    @Before
    public void preparePost(){
        VALID_USERNAME = ChatSDK.currentUser().getName();
        VALID_SKILLS_LIST = ChatSDK.currentUser().metaStringForKey(Keys.skills);
        VALID_GENRES_LIST = ChatSDK.currentUser().metaStringForKey(Keys.genres);
    }
    /*
     * This is basic test to check if UserProfile Fragment loads.
     * Assertion Criteria: Throws exception if Fragment does not load
     */
    @Test
    public void userProfileActivityLoadTest() {
        FragmentScenario.launchInContainer(UserProfileFragment.class);

        try{
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void userProfileActivityDisplayNameTest() {
        FragmentScenario.launchInContainer(UserProfileFragment.class);

        try{
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.profileUserNameTextView),
                        childAtPosition(
                                allOf(withId(R.id.profileInfoLayout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.RelativeLayout.class),
                                                0)),
                                1),
                        isDisplayed()));
        textView5.check(matches(withText(VALID_USERNAME)));

    }

    /*
     * Below Test checks User's Skill List
     */
    @Test
    public void userProfileActivitySkillsTest() {
        FragmentScenario.launchInContainer(UserProfileFragment.class);

        try{
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Skills list check. If isDisplay then it passes.
        onView(allOf(withId(R.id.contentTextView), withText(VALID_SKILLS_LIST),
                        childAtPosition(
                                childAtPosition(withId(R.id.profileInfoRecyclerView),0),
                                1),
                        isDisplayed()));

    }

    /*
     * Below Test checks User's Genres List
     */
    @Test
    public void userProfileActivityGenresListTest() {
        FragmentScenario.launchInContainer(UserProfileFragment.class);

        try{
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Genres list check. If isDisplay then it passes.
        onView(allOf(withId(R.id.contentTextView), withText(VALID_GENRES_LIST),
                childAtPosition(
                        childAtPosition(withId(R.id.profileInfoRecyclerView),0),
                        1),
                isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
