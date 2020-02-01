package com.legato.music.legato;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewGroup;


import androidx.test.core.app.ActivityScenario;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import com.legato.music.views.activity.SoloRegistrationActivity;
import com.legato.music.views.fragments.SoloArtistBasicInfoFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class SoloRegistrationActivityTest extends FireBaseTest {

    private static String TAG = SoloArtistBasicInfoFragment.class.getName();

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void soloRegistrationActivityLoadTest(){
        ActivityScenario.launch(SoloRegistrationActivity.class);

        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void soloRegistrationActivityBasicInfoTest(){

    }

    @Test
    public void soloRegistrationActivityProfilePhotoTest(){

    }

    @Test
    public void soloRegistrationActivityGenresTest(){

    }

    @Test
    public void soloRegistrationActivitySkillsTest(){

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
