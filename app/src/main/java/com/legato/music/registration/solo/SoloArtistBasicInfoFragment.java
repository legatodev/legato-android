package com.legato.music.registration.solo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import co.chatsdk.core.dao.User;
import id.zelory.compressor.Compressor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.legato.music.AppConstants;
import com.legato.music.Keys;
import com.legato.music.R;
import com.legato.music.youtube.YoutubeActivity;
import com.legato.music.RequestCodes;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import java.io.File;
import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.ImageUtils;
import co.chatsdk.ui.chat.MediaSelector;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoloArtistBasicInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoloArtistBasicInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoloArtistBasicInfoFragment extends Fragment implements View.OnClickListener {
    private boolean valid;

    @BindView(R.id.proximityAlertSwitch)
    @Nullable Switch proximitySwitch;
    @BindView(R.id.jamCheckBox)
    @Nullable CheckBox jamCheckBox;
    @BindView(R.id.collaborateCheckBox)
    @Nullable CheckBox collaborateCheckBox;
    @BindView(R.id.startBandCheckBox)
    @Nullable CheckBox startBandCheckBox;
    @BindView(R.id.searchRadiusValue)
    @Nullable TextView searchRadiusValue;
    @BindView(R.id.searchRadiusSeekBar)
    @Nullable SeekBar seekBarSearch;
    @BindView(R.id.soloDisplayNameTextInputEditText)
    @Nullable TextInputEditText soloDisplayNameTextInputEditText;
    @BindView(R.id.instagramTextInputEditText)
    @Nullable TextInputEditText instagramTextInputEditText;
    @BindView(R.id.facebookTextInputEditText)
    @Nullable TextInputEditText facebookTextInputEditText;
    @BindView(R.id.youtubeTextInputEditText)
    @Nullable TextInputEditText youtubeTextInputEditText;
    @BindView(R.id.addSampleButton1)
    @Nullable FloatingActionButton addSampleButton1;
    @BindView(R.id.soloArtistProfilePictureImageView)
    @Nullable SimpleDraweeView soloArtisitProfilePicImageView;
    @BindView(R.id.soloArtistAddEditProfilePictureTextView)
    @Nullable TextView soloArtistAddEditProfilePicTextView;

    private String mYoutubeVideo;
    @Nullable private View youtubeView;
    private LayoutInflater layoutInflater;
    private ViewGroup mContainer;
    private String avatarUrl;
    protected MediaSelector mediaSelector;

    public SoloArtistBasicInfoFragment() {
        valid = false;
        mYoutubeVideo = "";
        avatarUrl = "";
        mediaSelector = new MediaSelector();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solo_artist_basic_info, container, false);
        ButterKnife.bind(this, view);
        User user = ChatSDK.currentUser();
        layoutInflater = inflater;
        mContainer = container;

        Resources res = getResources();
        String[] rlookingfor = res.getStringArray(R.array.looking_for);
        String dblookingfor = user.metaStringForKey(Keys.lookingfor);

        if(dblookingfor != null) {
            if (dblookingfor.contains(rlookingfor[0]) && jamCheckBox != null)
                jamCheckBox.setChecked(true);

            if(dblookingfor.contains(rlookingfor[1]) && collaborateCheckBox != null)
                collaborateCheckBox.setChecked(true);

            if(dblookingfor.contains(rlookingfor[2]) && startBandCheckBox != null)
                startBandCheckBox.setChecked(true);
        }

        setOnCheckedChanged(jamCheckBox);
        setOnCheckedChanged(collaborateCheckBox);
        setOnCheckedChanged(startBandCheckBox);

        if (addSampleButton1 != null)
            addSampleButton1.setOnClickListener(this);

        if (seekBarSearch != null) {
            seekBarSearch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    setTextView(searchRadiusValue, progress + "mi");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    //write custom code to on start progress
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        String searchRadius = AppConstants.DEFAULT_SEARCH_RADIUS;
        if (user.metaStringForKey(Keys.searchradius) != null && !user.metaStringForKey(Keys.searchradius).isEmpty())
            searchRadius = user.metaStringForKey(Keys.searchradius);
        if (seekBarSearch != null) {
            seekBarSearch.setProgress(Integer.parseInt(searchRadius));
        }

        if (proximitySwitch != null) {
            proximitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    validate();
                }
            });
        }

        setTextView(soloDisplayNameTextInputEditText, user.getName());

        if (soloDisplayNameTextInputEditText != null) {
            soloDisplayNameTextInputEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    //Do nothing
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    validate();
                }
            });
        }

        setTextView(instagramTextInputEditText, user.metaStringForKey(Keys.instagram));
        setTextView(facebookTextInputEditText, user.metaStringForKey(Keys.facebook));
        setTextView(youtubeTextInputEditText, user.metaStringForKey(Keys.youtube_channel));

        if (user.getAvatarURL() != null && !user.getAvatarURL().isEmpty()) {
            setImageURI(soloArtisitProfilePicImageView, user.getAvatarURL());
            setTextView(soloArtistAddEditProfilePicTextView, R.string.edit_profile_pic);
            avatarUrl = user.getAvatarURL();
        }
        else {
            extractProfilePicFromFacebook();
        }

        if (soloArtisitProfilePicImageView != null) {
            soloArtisitProfilePicImageView.setOnClickListener(tempView -> {
                mediaSelector.startChooseImageActivity(getActivity(), MediaSelector.CropType.Circle, result -> {

                    try {
                        File compress = new Compressor(ChatSDK.shared().context())
                                .setMaxHeight(ChatSDK.config().imageMaxThumbnailDimension)
                                .setMaxWidth(ChatSDK.config().imageMaxThumbnailDimension)
                                .compressToFile(new File(result));

                        Bitmap bitmap = BitmapFactory.decodeFile(compress.getPath());

                        // Cache the file
                        File file = ImageUtils.compressImageToFile(ChatSDK.shared().context(), bitmap, ChatSDK.currentUserID(), ".png");
                        setImageURI(soloArtisitProfilePicImageView, Uri.fromFile(file));
                        setTextView(soloArtistAddEditProfilePicTextView, R.string.edit_profile_pic);
                        avatarUrl = Uri.fromFile(file).toString();
                    } catch (Exception e) {
                        e.printStackTrace();
                        ChatSDK.logError(e);
                    }
                });
            });
        }

        if (user.metaStringForKey(Keys.youtube) != null) {
            mYoutubeVideo = user.metaStringForKey(Keys.youtube);
            InitializeYoutubeView(view);
        }

        validate();

        return view;
    }

    private void setImageURI(@Nullable SimpleDraweeView view, String uri) {
        if (view != null) {
            view.setImageURI(uri);
        }
    }

    private void setImageURI(@Nullable SimpleDraweeView view, Uri uri) {
        if (view != null) {
            view.setImageURI(uri);
        }
    }

    private void setOnCheckedChanged(@Nullable CheckBox checkBox) {
        if (checkBox != null) {
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    validate();
                }
            });
        }
    }

    private void setTextView(@Nullable TextView view, String text) {
        if (view != null) { view.setText(text); }
    }

    private void setTextView(@Nullable TextView view, int resid) {
        if (view != null) { view.setText(resid); }
    }

    private void extractProfilePicFromFacebook() {
        String facebookUserId = "";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        // find the Facebook profile and get the user's id
        for(UserInfo profile : firebaseUser.getProviderData()) {
            // check if the provider id matches "facebook.com"
            if(FacebookAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                facebookUserId = profile.getUid();
            }
        }
        String photoUrl = "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
        setImageURI(soloArtisitProfilePicImageView, photoUrl);
        setTextView(soloArtistAddEditProfilePicTextView, R.string.edit_profile_pic);
        avatarUrl = photoUrl;
    }


    public boolean isInputValid() {
        return valid;
    }

    private void validate() {
        valid = (isCheckBoxChecked(jamCheckBox) || isCheckBoxChecked(collaborateCheckBox) || isCheckBoxChecked(startBandCheckBox));
        if (soloDisplayNameTextInputEditText != null) {
            valid = valid && !soloDisplayNameTextInputEditText.getText().toString().trim().isEmpty();
        }

        ((SoloRegistrationActivity) getActivity()).setVisibleTabCount();
    }

    private boolean isCheckBoxChecked(@Nullable CheckBox checkbox) {
        if (checkbox != null) {
            return checkbox.isChecked();
        }

        return false;
    }

    public HashMap<String, String> extractData() {
        Resources res = getResources();
        HashMap<String, String> basicInfo = new HashMap<>();
        if (soloDisplayNameTextInputEditText != null)
            basicInfo.put(co.chatsdk.core.dao.Keys.Name, soloDisplayNameTextInputEditText.getText().toString().trim());

        String[] lookingForArray = res.getStringArray(R.array.looking_for);
        String lookingfor = isCheckBoxChecked(jamCheckBox)?lookingForArray[0]+"|":"";
        lookingfor += isCheckBoxChecked(collaborateCheckBox)?lookingForArray[1]+"|":"";
        lookingfor += isCheckBoxChecked(startBandCheckBox)?lookingForArray[2]+"|":"";

        basicInfo.put(Keys.lookingfor, lookingfor);
        if (seekBarSearch != null)
            basicInfo.put(Keys.searchradius, Integer.toString(seekBarSearch.getProgress()));
        if (instagramTextInputEditText != null)
            basicInfo.put(Keys.instagram, instagramTextInputEditText.getText().toString().trim());
        if (facebookTextInputEditText != null)
            basicInfo.put(Keys.facebook, facebookTextInputEditText.getText().toString().trim());
        if (youtubeTextInputEditText != null)
            basicInfo.put(Keys.youtube_channel, youtubeTextInputEditText.getText().toString().trim());
        if (proximitySwitch != null) {
            basicInfo.put(Keys.proximityalert, Boolean.toString(proximitySwitch.isEnabled()));
        }

        basicInfo.put(Keys.youtube, mYoutubeVideo);
        basicInfo.put(co.chatsdk.core.dao.Keys.AvatarURL, avatarUrl);

        return basicInfo;
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), YoutubeActivity.class);
        startActivityForResult(intent, RequestCodes.RC_YOUTUBE_SEARCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.RC_YOUTUBE_SEARCH && resultCode == Activity.RESULT_OK && data != null) {
            mYoutubeVideo = data.getStringExtra("youtube_video");
            InitializeYoutubeView(getView());
        }
        else {
            try {
                mediaSelector.handleResult(getActivity(), requestCode, resultCode, data);
            } catch (Exception e) {
                ChatSDK.logError(e);
            }
        }
    }

    private void InitializeYoutubeView(View view) {
        LinearLayout galleryHorizontalScrollViewLayout = view.findViewById(R.id.galleryHorizontalScrollViewLayout1);
        youtubeView = layoutInflater.inflate(R.layout.youtube_frame_layout, mContainer, false);
        if (youtubeView != null) {
            galleryHorizontalScrollViewLayout.addView(youtubeView);
            YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.youtube_frame_layout, youTubePlayerFragment);
            transaction.addToBackStack(null);
            transaction.commit();

            youTubePlayerFragment.initialize(getResources().getString(R.string.google_api_key), new YouTubePlayer.OnInitializedListener() {

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                    if (!wasRestored) {
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        player.cueVideo(mYoutubeVideo);
                    }
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                    // YouTube error
                }
            });
        }
    }
}
