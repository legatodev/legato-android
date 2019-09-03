package com.legato.music.registration.solo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.legato.music.Keys;
import com.legato.music.NearbyUsersActivity;
import com.legato.music.R;
import com.legato.music.registration.GenresFragment;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.User;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.types.FileUploadResult;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.firebase.wrappers.UserWrapper;
import io.reactivex.Completable;
import io.reactivex.Observer;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SoloRegistrationActivity extends AppCompatActivity implements SkillsFragment.FinishClickedListener{

    /**
     * The {@link androidx.core.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link androidx.core.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @BindView(R.id.soloViewPager)
    ViewPager mViewPager;

    private Fragment soloRegistrationTab;
    private Fragment skillsTab;
    private Fragment genresTab;

    private DisposableList disposableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solo_registration_tab);
        ButterKnife.bind(this);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }

        //Don't destroy the last two fragments.
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = findViewById(R.id.soloRegistrationTablayout);
        tabLayout.setupWithViewPager(mViewPager);

        soloRegistrationTab = new SoloArtistBasicInfoFragment();
        genresTab = new GenresFragment();
        skillsTab = new SkillsFragment(this);

        disposableList = new DisposableList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_solo_registration_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setVisibleTabCount() {
        int countTab = 1;

        if (((SoloArtistBasicInfoFragment)soloRegistrationTab).isInputValid()) {
            countTab = 2;
            if (((GenresFragment)genresTab).isInputValid()) {
                countTab = 3;
            }
        }

        mSectionsPagerAdapter.setCount(countTab);
    }

    public void submitData() {
        HashMap<String, String> profileInfo = ((SoloArtistBasicInfoFragment)soloRegistrationTab).extractData();
        profileInfo.put(Keys.genres, ((GenresFragment)genresTab).extractData());
        profileInfo.put(Keys.skills, ((SkillsFragment)skillsTab).extractData());

        User user = ChatSDK.currentUser();
        Iterator it = profileInfo.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            user.setMetaString((String)pair.getKey(), (String)pair.getValue());
            if (co.chatsdk.core.dao.Keys.AvatarURL.equals(pair.getKey())) {
                disposableList.add(pushProfilePic()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            //dismiss update progress bar
                        }));
            }
        }
    }

    private Completable pushProfilePic() {
            return Single.create((SingleOnSubscribe<User>) e -> {
                String url = ChatSDK.currentUser().getAvatarURL();
                if (url == null || url.isEmpty()) {
                    e.onSuccess(ChatSDK.currentUser());
                } else {
                    // Check to see if the avatar URL is local or remote
                    File avatar = new File(new URI(ChatSDK.currentUser().getAvatarURL()).getPath());
                    Bitmap bitmap = BitmapFactory.decodeFile(avatar.getPath());

                    if (new URL(ChatSDK.currentUser().getAvatarURL()).getHost() != null && bitmap != null && ChatSDK.upload() != null) {
                        // Upload the image
                        ChatSDK.upload().uploadImage(bitmap).subscribe(new Observer<FileUploadResult>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                            }

                            @Override
                            public void onNext(@NonNull FileUploadResult fileUploadResult) {
                                if (fileUploadResult.urlValid()) {
                                    ChatSDK.currentUser().setAvatarURL(fileUploadResult.url);
                                    ChatSDK.currentUser().update();
                                    ChatSDK.events().source().onNext(NetworkEvent.userMetaUpdated(ChatSDK.currentUser()));
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable ex) {
                                ChatSDK.logError(ex);
                                e.onSuccess(ChatSDK.currentUser());
                            }

                            @Override
                            public void onComplete() {
                                e.onSuccess(ChatSDK.currentUser());
                            }
                        });
                    } else {
                        e.onSuccess(ChatSDK.currentUser());
                    }
                }
            }).flatMapCompletable(user -> new UserWrapper(user).push()).subscribeOn(Schedulers.single());
    }

    @Override
    public void onFinish() {
        submitData();
        Intent intent = new Intent(this, NearbyUsersActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        soloRegistrationTab.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    class SectionsPagerAdapter extends FragmentPagerAdapter {

        private int tabCount;

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            tabCount = 1;
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return soloRegistrationTab;
                case 1:
                    return genresTab;
                case 2:
                    return skillsTab;
            }

            //TODO: should we return an error if position exceeds the number of tabs available.
            return soloRegistrationTab;
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        public void setCount(int count) {
            if (tabCount != count) {
                tabCount = count;
                notifyDataSetChanged();
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Solo Artist";
                case 1:
                    return "Genres";
                case 2:
                    return "Skill";
                default:
                    //TODO: throw an error perhaps?
                    return "";
            }
        }
    }
}
