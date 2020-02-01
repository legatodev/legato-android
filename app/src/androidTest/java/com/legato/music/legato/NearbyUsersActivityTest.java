package com.legato.music.legato;

import android.view.View;
import android.view.ViewParent;
import android.view.ViewGroup;


import androidx.test.core.app.ActivityScenario;

import androidx.test.espresso.ViewInteraction;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import androidx.test.rule.GrantPermissionRule;

import com.legato.music.views.activity.NearbyUsersActivity;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import com.legato.music.R;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NearbyUsersActivityTest extends FireBaseTest {

    private static String VALID_NEARBY_USER_NAME = "Test User";

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    /*
        Below test checks if after successful login
        if it redirects to NearbyUsersActivity for returning user
        If exception is generated then it will be considered as failed test case.
    */
    @Test
    public void nearbyUsersActivityLoadTest() {
        ActivityScenario.launch(NearbyUsersActivity.class);
    }

    /*
    Below test checks if after successful login
    if there is atleast one NearbyUserCard
    */
    @Test
    public void nearbyUsersActivityNearbyUserCardTest() {
        ActivityScenario.launch(NearbyUsersActivity.class);

        //Following assertion checks if there is atleast 1 nearbyUser card.
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction textView = onView(
                allOf(withId(R.id.nearbyUserNameTextView), withText(VALID_NEARBY_USER_NAME),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.nearbyUserCardView),
                                        0),
                                1),
                        isDisplayed()));
        textView.check(matches(withText(VALID_NEARBY_USER_NAME)));
    }

    /*
     * Below test validates transition from nearbyUseractivity to
     * UserprofileActivity. If the transition fails there will be exception and test fails.
     */
    @Test
    public void nearbyUsersActivityTransitionUserProfileTest() {
        ActivityScenario.launch(NearbyUsersActivity.class);

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.buttonSettings),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.filterBar),0),
                                0),
                        isDisplayed()));
        //If below click fails test will fail.
        appCompatImageView.perform(click());
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
