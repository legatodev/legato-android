package com.legato.music.legato;

import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewGroup;


import androidx.test.core.app.ActivityScenario;

import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

import com.legato.music.R;
import static androidx.test.espresso.Espresso.onView;
import static org.hamcrest.Matchers.endsWith;

import com.legato.music.utils.Keys;
import com.legato.music.views.activity.SoloRegistrationActivity;
import com.legato.music.views.fragments.SoloArtistBasicInfoFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import co.chatsdk.core.session.ChatSDK;

@RunWith(AndroidJUnit4.class)
public class SoloRegistrationActivityTest extends FireBaseTest {

    private static String TAG = SoloArtistBasicInfoFragment.class.getName();

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    private static String VALID_DISPLAY_NAME = "";
    private static String VALID_FACEBOOK_ID = "";
    private static String VALID_INSTAGRAM_ID = "";
    private static String VALID_YOUTUBE_CHANNEL_ID = "";
    private static String VALID_DESCRIPTION = "";


    @Before
    public void preparePost(){
        VALID_DISPLAY_NAME = ChatSDK.currentUser().getName();
        VALID_FACEBOOK_ID = ChatSDK.currentUser().metaStringForKey(Keys.facebook);
        VALID_INSTAGRAM_ID = ChatSDK.currentUser().metaStringForKey(Keys.instagram);
        VALID_YOUTUBE_CHANNEL_ID = ChatSDK.currentUser().metaStringForKey(Keys.youtube_channel);
        VALID_DESCRIPTION = ChatSDK.currentUser().metaStringForKey(Keys.user_description);
    }

    @Test
    public void soloRegistrationActivityBasicInfoLoadTest(){
        ActivityScenario.launch(SoloRegistrationActivity.class);
        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void soloRegistrationActivityBasicInfoNameTest(){

        ActivityScenario.launch(SoloRegistrationActivity.class);

        try{
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test Display Name
        ViewInteraction soloDisplayNameTextInputEditText = onView(
                allOf(withId(R.id.soloDisplayNameTextInputEditText), withText(VALID_DISPLAY_NAME),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.soloDisplayNameTextInputLayout),
                                        0),
                                0),
                        isDisplayed()));
        soloDisplayNameTextInputEditText.check(matches(withText(VALID_DISPLAY_NAME)));
    }

    @Test
    public void soloRegistrationActivityBasicInfoFBTest() {

        ActivityScenario.launch(SoloRegistrationActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction facebookTextInputEditText = onView(
                allOf(withId(R.id.facebookTextInputEditText), withText(VALID_FACEBOOK_ID),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.facebookTextInputEditText),
                                        0),
                                0),
                        isDisplayed()));
        // Test Facebook ID
        if(VALID_FACEBOOK_ID.isEmpty()) {
            Log.d(TAG,"Facebook ID is empty");
            return;
        }
        facebookTextInputEditText.check(matches(withText(VALID_FACEBOOK_ID)));
    }

    @Test
    public void soloRegistrationActivityBasicInfoYoutubeTest() {

        ActivityScenario.launch(SoloRegistrationActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test Youtube Channel ID
        ViewInteraction youtubeTextInputEditText = onView(
                allOf(withId(R.id.youtubeTextInputEditText), withText(VALID_YOUTUBE_CHANNEL_ID),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.facebookTextInputEditText),
                                        0),
                                0),
                        isDisplayed()));
        if(VALID_YOUTUBE_CHANNEL_ID.isEmpty()) {
            Log.d(TAG,"Youtube Channel ID is empty");
            return;
        }
        youtubeTextInputEditText.check(matches(withText(VALID_YOUTUBE_CHANNEL_ID)));
    }

    @Test
    public void soloRegistrationActivityBasicInfoInstaTest() {

        ActivityScenario.launch(SoloRegistrationActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Test Instagram ID
        ViewInteraction instagramTextInputEditText = onView(
                allOf(withId(R.id.instagramTextInputEditText),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.facebookTextInputEditText),
                                        0),
                                0),
                        isDisplayed()));

        if(VALID_INSTAGRAM_ID.isEmpty()) {
            return;
        }
        instagramTextInputEditText.check(matches(withText(VALID_INSTAGRAM_ID)));
    }

    @Test
    public void soloRegistrationActivityBasicInfoDescriptionTest() {

        ActivityScenario.launch(SoloRegistrationActivity.class);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(VALID_DESCRIPTION.isEmpty()) {
            Log.d(TAG,"User Description ID is empty");
            return;
        }

        onView(withText(endsWith(VALID_DESCRIPTION))).check(matches(isDisplayed()));
    }

    @Test
    public void soloRegistrationActivityBasicInfoProfilePhotoTest(){

    }

    @Test
    public void soloRegistrationActivityBasicInfoGenresTest(){

    }

    @Test
    public void soloRegistrationActivityBasicInfoSkillsTest(){

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
