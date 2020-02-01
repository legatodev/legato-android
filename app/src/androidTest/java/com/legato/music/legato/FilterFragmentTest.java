package com.legato.music.legato;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.fragment.app.testing.FragmentScenario;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;

import com.legato.music.views.activity.NearbyUsersActivity;
import com.legato.music.views.fragments.FilterDialogFragment;
import com.legato.music.views.fragments.SoloArtistBasicInfoFragment;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class FilterFragmentTest extends FireBaseTest {

    @Rule
    public GrantPermissionRule mGrantPermissionRule = GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void filterFragmentLoadTest(){
        ActivityScenario.launch(NearbyUsersActivity.class);
        try{
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        FragmentScenario.launchInContainer(FilterDialogFragment.class);

    }

    @Test
    public void filterFragmentSortbyDistanceTest(){
        
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
