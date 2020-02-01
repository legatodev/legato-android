package com.legato.music.legato;

import android.view.View;
import android.view.ViewParent;
import android.view.ViewGroup;


import androidx.fragment.app.testing.FragmentScenario;

import androidx.test.espresso.ViewInteraction;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import com.legato.music.R;
import com.legato.music.views.fragments.UserProfileFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class LogoutTest extends FireBaseTest {

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    /*
     * In order to test logout first open UserProfileActivity.
     * Click logout.
     * Assertion Criteria:
     * 1] Logout click throws exception.
     * 2] ChatSDK user object should be null.
     */
    @Test
    public void logoutTest() {
        FragmentScenario.launchInContainer(UserProfileFragment.class);

        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(R.id.logoutButton), withText("Logout"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.profileScrollView),
                                        0),
                                5)));
        appCompatButton4.perform(scrollTo(), click());

        //TODO: Add second assertion i.e. to check ChatSDK user. it should be null at this point

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
