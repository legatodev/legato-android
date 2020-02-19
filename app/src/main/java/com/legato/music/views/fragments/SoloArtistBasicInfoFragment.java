package com.legato.music.views.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.legato.music.AppConstants;
import com.legato.music.R;
import com.legato.music.spotify.Player;
import com.legato.music.spotify.PlayerService;
import com.legato.music.spotify.SpotifySearchActivity;
import com.legato.music.utils.Conversions;
import com.legato.music.utils.Keys;
import com.legato.music.utils.RequestCodes;
import com.legato.music.viewmodels.SoloArtistViewModel;
import com.legato.music.views.activity.SoloRegistrationActivity;
import com.legato.music.views.adapters.MediaPlayerAdapter;
import com.legato.music.youtube.YoutubeActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import co.chatsdk.ui.chat.MediaSelector;
import co.chatsdk.ui.utils.ToastHelper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoloArtistBasicInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoloArtistBasicInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoloArtistBasicInfoFragment extends Fragment implements MediaPlayerAdapter.TrackRemovedListener {
    private static final String TAG = SoloArtistBasicInfoFragment.class.getSimpleName();
    private static final int REQUEST_CODE = 1337;

    @BindView(R.id.proximityAlertSwitch) Switch proximityAlertSwitch;
    @BindView(R.id.jamCheckBox) CheckBox jamCheckBox;
    @BindView(R.id.collaborateCheckBox) CheckBox collaborateCheckBox;
    @BindView(R.id.startBandCheckBox) CheckBox startBandCheckBox;
    @BindView(R.id.soloDisplayNameTextInputEditText) TextInputEditText soloDisplayNameTextInputEditText;
    @BindView(R.id.userDescription) TextInputEditText userDescriptionTextInputEditText;
    @BindView(R.id.instagramTextInputEditText) TextInputEditText instagramTextInputEditText;
    @BindView(R.id.facebookTextInputEditText) TextInputEditText facebookTextInputEditText;
    @BindView(R.id.youtubeTextInputEditText) TextInputEditText youtubeTextInputEditText;
    @BindView(R.id.addYoutubeButton) FloatingActionButton addSampleButton;
    @BindView(R.id.soloArtistProfilePictureImageView) SimpleDraweeView soloArtistProfilePictureImageView;
    @BindView(R.id.soloArtistAddEditProfilePictureTextView) TextView soloArtistAddEditProfilePictureTextView;
    @BindView(R.id.mediaRecyclerView) RecyclerView mediaRecyclerView;
    @BindView(R.id.galleryLayout) View galleryView;
    @BindView(R.id.soloArtistProgressBar) ProgressBar progressBar;
    @BindView(R.id.instagramValidImageView) ImageView instagramValidImageView;
    @BindView(R.id.facebookPageValidImageView) ImageView facebookPageValidImageView;
    @BindView(R.id.youtubeChannelValidImageView) ImageView youtubeChannelValidImageView;

    @NonNull private SoloArtistViewModel soloArtistViewModel;

    @Nullable private MediaPlayerAdapter mediaPlayerAdapter;

    private boolean mSearchSpotifyTrack = false;
    private String mAvatartUrl = "";
    private DisposableList disposableList = new DisposableList();

    List<String> trackIds = new ArrayList<>();

    private MediaSelector mediaSelector;

    private Drawable fail_icon;
    private Drawable pass_icon;

    public SoloArtistBasicInfoFragment() {
        mediaSelector = new MediaSelector();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solo_artist_basic_info, container, false);
        ButterKnife.bind(this, view);

        soloArtistViewModel = ViewModelProviders.of(requireActivity()).get(SoloArtistViewModel.class);
        observeViewModel();

        fail_icon = getResources().getDrawable(R.drawable.ic_cancel_red_24dp);
        pass_icon = getResources().getDrawable(R.drawable.ic_check_circle_green_24dp);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Resources res = getResources();
        String[] rlookingfor = res.getStringArray(R.array.looking_for);
        String dblookingfor = soloArtistViewModel.getLookingFor();
        if (dblookingfor != null) {
            if (dblookingfor.contains(rlookingfor[0]))
                jamCheckBox.setChecked(true);

            if (dblookingfor.contains(rlookingfor[1]))
                collaborateCheckBox.setChecked(true);

            if (dblookingfor.contains(rlookingfor[2]))
                startBandCheckBox.setChecked(true);
        }

        setOnCheckedChanged(jamCheckBox);
        setOnCheckedChanged(collaborateCheckBox);
        setOnCheckedChanged(startBandCheckBox);

        proximityAlertSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> validate());
        setTextView(soloDisplayNameTextInputEditText, soloArtistViewModel.getUser().getName());
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

        userDescriptionTextInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                soloArtistViewModel.setDescription(s.toString());
            }
        });
        setTextView(userDescriptionTextInputEditText, soloArtistViewModel.getDescription());

        instagramTextInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                soloArtistViewModel.setInstagram(s.toString());
            }
        });
        setTextView(instagramTextInputEditText, soloArtistViewModel.getInstagram());

        facebookTextInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                soloArtistViewModel.queryFacebookPageId(s.toString());
            }
        });
        setTextView(facebookTextInputEditText, soloArtistViewModel.getFacebook());

        youtubeTextInputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                soloArtistViewModel.setYoutubeChannel(s.toString());
            }
        });
        setTextView(youtubeTextInputEditText, soloArtistViewModel.getYoutubeChannel());

        String avatarUrl = soloArtistViewModel.getAvatarUrl();
        if (!TextUtils.isEmpty(avatarUrl)) {
            setImageURI(soloArtistProfilePictureImageView, avatarUrl);
            setTextView(soloArtistAddEditProfilePictureTextView, R.string.edit_profile_pic);
        } else {
            extractProfilePicFromFacebook();
        }

        soloArtistProfilePictureImageView.setOnClickListener(tempView -> {
            if (getActivity() != null) {
                disposableList.add(mediaSelector.startChooseImageActivity(getActivity(), MediaSelector.CropType.Circle)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((result, throwable) -> {
                            if (throwable == null) {
                                File file = (File)result;
                                setImageURI(soloArtistProfilePictureImageView, Uri.fromFile(file));
                                setTextView(soloArtistAddEditProfilePictureTextView, R.string.edit_profile_pic);
                                mAvatartUrl = Uri.fromFile(file).toString();
                            } else {
                                ToastHelper.show(getActivity(), throwable.getLocalizedMessage());
                            }
                        }));
            }
        });


        mediaRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false);
        mediaRecyclerView.setLayoutManager(layoutManager);
        if (!soloArtistViewModel.getYoutubeVideoIds().isEmpty() || !soloArtistViewModel.getSpotifyTrackIds().isEmpty()) {
            InitializeMediaView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        validate();
        if (mediaPlayerAdapter != null)
            mediaPlayerAdapter.doBindService();
        else
            Log.e(TAG, "MediaPlayer Adapter is null");
    }

    private void setImageURI(@Nullable SimpleDraweeView view, @Nullable String uri) {
        if (view != null && uri != null) {
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
        setImageURI(soloArtistProfilePictureImageView, soloArtistViewModel.getAvatarUrl());
        setTextView(soloArtistAddEditProfilePictureTextView, R.string.edit_profile_pic);
    }

    public boolean isInputValid() {
        boolean valid = false;

        if (this.getView() != null) {
            String displayName = (soloDisplayNameTextInputEditText.getText() != null) ?
                    soloDisplayNameTextInputEditText.getText().toString().trim() : "";

            valid = (isCheckBoxChecked(jamCheckBox) ||
                    isCheckBoxChecked(collaborateCheckBox) ||
                    isCheckBoxChecked(startBandCheckBox));
            valid = valid && !displayName.isEmpty();
        }

        return valid;
    }

    private void validate() {
        if (getActivity() != null)
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

        String displayName = (soloDisplayNameTextInputEditText.getText() != null) ?
                soloDisplayNameTextInputEditText.getText().toString().trim() : "";
        basicInfo.put(co.chatsdk.core.dao.Keys.Name, displayName);

        String[] lookingForArray = res.getStringArray(R.array.looking_for);
        String lookingfor = isCheckBoxChecked(jamCheckBox)?lookingForArray[0]+"|":"";
        lookingfor += isCheckBoxChecked(collaborateCheckBox)?lookingForArray[1]+"|":"";
        lookingfor += isCheckBoxChecked(startBandCheckBox)?lookingForArray[2]+"|":"";

        basicInfo.put(Keys.lookingfor, lookingfor);

        String instagram = (instagramTextInputEditText.getText() != null) ?
                Conversions.getConvertedSocialMediaHandle(
                        instagramTextInputEditText.getText().toString().trim()) : "";
        basicInfo.put(Keys.instagram, instagram);

        String facebook = (facebookTextInputEditText.getText() != null) ?
                Conversions.getConvertedSocialMediaHandle(
                        facebookTextInputEditText.getText().toString().trim()) : "";
        basicInfo.put(Keys.facebook, facebook);

        String youtubeChannel = (youtubeTextInputEditText.getText() != null) ?
                Conversions.getConvertedSocialMediaHandle(
                        youtubeTextInputEditText.getText().toString().trim()) : "";
        basicInfo.put(Keys.youtube_channel, youtubeChannel);

        basicInfo.put(Keys.proximityalert, Boolean.toString(proximityAlertSwitch.isEnabled()));

        basicInfo.put(Keys.youtube, soloArtistViewModel.getYoutubeVideoIdsAsString());
        basicInfo.put(co.chatsdk.core.dao.Keys.AvatarURL, mAvatartUrl);

        return basicInfo;
    }

    @OnClick(R.id.addYoutubeButton)
    public void onClick(View view) {
        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View promptView = layoutInflater.inflate(R.layout.fragment_media_dialog, null);
        final AlertDialog chooseMediaDialog = new AlertDialog.Builder(getActivity()).create();
        chooseMediaDialog.setView(promptView);
        ImageView addYoutube = promptView.findViewById(R.id.addYoutubeImageView);
        addYoutube.setOnClickListener(v -> {
            chooseMediaDialog.dismiss();
            Intent intent = new Intent(getActivity(), YoutubeActivity.class);
            startActivityForResult(intent, RequestCodes.RC_YOUTUBE_SEARCH);
        });

        ImageView addSpotify = promptView.findViewById(R.id.addSpotifyImageView);
        addSpotify.setOnClickListener(v -> {
            chooseMediaDialog.dismiss();
            launchSpotifySearch();
        });
        chooseMediaDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (requestCode == REQUEST_CODE) {
                AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

                switch (response.getType()) {
                    // Response was successful and contains auth token
                    case TOKEN:
                        // Handle successful response
                        soloArtistViewModel.setSpotifyAccessToken(response.getAccessToken());
                        if (mSearchSpotifyTrack)
                        {
                            Intent intent = SpotifySearchActivity.createIntent(getActivity());
                            intent.putExtra(SpotifySearchActivity.EXTRA_TOKEN, soloArtistViewModel.getSpotifyAccessToken());
                            startActivityForResult(intent, RequestCodes.RC_SPOTIFY_TRACK);
                            mSearchSpotifyTrack = false;
                        } else {
                            addSpotifyTracks();
                        }
                        Log.d(TAG, "Access Token: " + soloArtistViewModel.getSpotifyAccessToken());
                        break;

                    // Auth flow returned an error
                    case ERROR:
                        // Handle error response
                        String errorString = getResources().getText(R.string.spotify_auth_failed)+response.getError();
                        Log.e(TAG, errorString);
                        Toast.makeText(getContext(), errorString, Toast.LENGTH_LONG).show();

                        // Most likely auth flow was cancelled
                    default:
                        // Handle other cases
                        mediaPlayerAdapter = new MediaPlayerAdapter(
                                getContext(),
                                trackIds,
                                this.getLifecycle(),
                                true,
                                this);
                        mediaRecyclerView.setAdapter(mediaPlayerAdapter);
                        galleryView.setVisibility(View.VISIBLE);
                }
            } else if (requestCode == RequestCodes.RC_YOUTUBE_SEARCH && resultCode == Activity.RESULT_OK) {
                soloArtistViewModel.addTrackId(data.getStringExtra("youtube_video"));
                InitializeMediaView();
            } else if (requestCode == RequestCodes.RC_SPOTIFY_TRACK && resultCode == Activity.RESULT_OK) {
                soloArtistViewModel.addTrackId(data.getStringExtra("spotify_track"));
                InitializeMediaView();
            } else {
                try {
                    mediaSelector.handleResult(getActivity(), requestCode, resultCode, data);
                } catch (Exception e) {
                    ChatSDK.logError(e);
                }
            }
        }
    }

    private void addSpotifyTracks() {
        String spotifyAccessToken = soloArtistViewModel.getSpotifyAccessToken();
        if (!spotifyAccessToken.isEmpty()) {
            trackIds.addAll(soloArtistViewModel.getSpotifyTrackIds());
            SpotifyApi spotifyApi = new SpotifyApi();
            spotifyApi.setAccessToken(spotifyAccessToken);
            SpotifyService spotifyService = spotifyApi.getService();
            mediaPlayerAdapter = new MediaPlayerAdapter(
                    getContext(),
                    trackIds,
                    this.getLifecycle(),
                    spotifyService,
                    true,
                    this);
        } else {
            mediaPlayerAdapter = new MediaPlayerAdapter(
                    getContext(),
                    trackIds,
                    this.getLifecycle(),
                    true,
                    this);
        }

        mediaRecyclerView.setAdapter(mediaPlayerAdapter);
        galleryView.setVisibility(View.VISIBLE);
    }

    private void launchSpotifySearch() {
        String accessToken = soloArtistViewModel.getSpotifyAccessToken();
        mSearchSpotifyTrack = true;
        if (TextUtils.isEmpty(accessToken)) {
            spotifyInitialize();
        } else {
            Intent intent = SpotifySearchActivity.createIntent(getActivity());
            intent.putExtra(SpotifySearchActivity.EXTRA_TOKEN, accessToken);
            startActivityForResult(intent, RequestCodes.RC_SPOTIFY_TRACK);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void InitializeMediaView() {
        if (mediaRecyclerView != null) {
            galleryView.setVisibility(View.GONE);
            trackIds.clear();
            trackIds.addAll(soloArtistViewModel.getYoutubeVideoIds());
            if (!soloArtistViewModel.getSpotifyTrackIds().isEmpty()) {
                spotifyInitialize();
            } else {
                mediaPlayerAdapter = new MediaPlayerAdapter(
                        getContext(),
                        trackIds,
                        this.getLifecycle(),
                        true,
                        this);
                mediaRecyclerView.setAdapter(mediaPlayerAdapter);
                galleryView.setVisibility(View.VISIBLE);
            }
        }
    }

    private void spotifyInitialize() {
        if (soloArtistViewModel.getSpotifyAccessToken().isEmpty()) {
            // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
            AuthenticationRequest.Builder builder =
                    new AuthenticationRequest.Builder(getResources().getString(R.string.spotify_client_id), AuthenticationResponse.Type.TOKEN, AppConstants.SPOTIFY_REDIRECT_URI);

            builder.setShowDialog(true);
            builder.setScopes(new String[]{"streaming"});
            AuthenticationRequest request = builder.build();
            AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);
        } else {
            addSpotifyTracks();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        disposableList.dispose();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayerAdapter != null)
            mediaPlayerAdapter.doUnbindService();
        else
            Log.e(TAG, "MediaPlayer Adapter is null");
    }

    @Override
    public void onTrackRemoved(View v, int position) {
        String trackId = trackIds.get(position);
        soloArtistViewModel.removeTrackId(trackId);
        InitializeMediaView();
    }

    private void observeViewModel() {
        soloArtistViewModel.getIsQueryingApi().observe(this, (searching) -> {
            if (searching) {
                progressBar.setVisibility(View.VISIBLE);
            }
            else {
                progressBar.setVisibility(View.GONE);
                // Instagram Logic
                if (TextUtils.isEmpty(soloArtistViewModel.getInstagram())) {
                    instagramValidImageView.setImageDrawable(fail_icon);

                    if(!TextUtils.isEmpty(instagramTextInputEditText.getText().toString())) {
                        instagramValidImageView.setVisibility(View.VISIBLE);
                    }
                    else {
                        instagramValidImageView.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    instagramValidImageView.setImageDrawable(pass_icon);
                    instagramValidImageView.setVisibility(View.VISIBLE);
                }

                // Facebook logic
                if (TextUtils.isEmpty(soloArtistViewModel.getFacebookPageId())) {
                    facebookPageValidImageView.setImageDrawable(fail_icon);

                    if(!TextUtils.isEmpty(facebookTextInputEditText.getText().toString())) {
                        facebookPageValidImageView.setVisibility(View.VISIBLE);
                    }
                    else {
                        facebookPageValidImageView.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    facebookPageValidImageView.setImageDrawable(pass_icon);
                    facebookPageValidImageView.setVisibility(View.VISIBLE);
                }

                // Youtube channel logic
                if (TextUtils.isEmpty(soloArtistViewModel.getYoutubeChannel())) {
                    youtubeChannelValidImageView.setImageDrawable(fail_icon);

                    if(!TextUtils.isEmpty(youtubeTextInputEditText.getText().toString())) {
                        youtubeChannelValidImageView.setVisibility(View.VISIBLE);
                    }
                    else {
                        youtubeChannelValidImageView.setVisibility(View.INVISIBLE);
                    }
                }
                else {
                    youtubeChannelValidImageView.setImageDrawable(pass_icon);
                    youtubeChannelValidImageView.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
