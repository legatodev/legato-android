package com.legato.music.views.fragments;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.base.Joiner;
import com.legato.music.AppConstants;
import com.legato.music.R;
import com.legato.music.spotify.Player;
import com.legato.music.spotify.PlayerService;
import com.legato.music.spotify.SpotifySearchActivity;
import com.legato.music.utils.Keys;
import com.legato.music.utils.RequestCodes;
import com.legato.music.viewmodels.SoloArtistViewModel;
import com.legato.music.views.activity.SoloRegistrationActivity;
import com.legato.music.views.adapters.YoutubePlayerAdapter;
import com.legato.music.youtube.YoutubeActivity;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.ImageUtils;
import co.chatsdk.ui.chat.MediaSelector;
import co.chatsdk.ui.utils.ImagePickerUploader;
import co.chatsdk.ui.utils.ToastHelper;
import id.zelory.compressor.Compressor;
import io.reactivex.disposables.Disposable;
import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyCallback;
import kaaes.spotify.webapi.android.SpotifyError;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.client.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoloArtistBasicInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoloArtistBasicInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoloArtistBasicInfoFragment extends Fragment {
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
    @BindView(R.id.resetButton) FloatingActionButton resetButton;
    @BindView(R.id.soloArtistProfilePictureImageView) SimpleDraweeView soloArtistProfilePictureImageView;
    @BindView(R.id.soloArtistAddEditProfilePictureTextView) TextView soloArtistAddEditProfilePictureTextView;
    @BindView(R.id.youtubeRecyclerView) RecyclerView youtubeRecyclerView;

    @NonNull private SoloArtistViewModel soloArtistViewModel;

    @NonNull private YoutubePlayerAdapter youtubePlayerAdapter;

    @Nullable private Player mPlayerBoundService;

    private boolean mSpotifyPlayerBound = false;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPlayerBoundService = ((PlayerService.PlayerBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mPlayerBoundService = null;
        }
    };

    private MediaSelector mediaSelector;
    private String mAccessToken = "";

    public SoloArtistBasicInfoFragment() {
        mediaSelector = new MediaSelector();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_solo_artist_basic_info, container, false);
        ButterKnife.bind(this, view);

        // TODO: Switch getActivity() to requireActivity()
        soloArtistViewModel = ViewModelProviders.of(requireActivity()).get(SoloArtistViewModel.class);
        youtubePlayerAdapter = new YoutubePlayerAdapter(
                soloArtistViewModel.getYoutubeVideoIds(),
                this.getLifecycle());

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

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soloArtistViewModel.resetYoutubeVideoIds();
                InitializeYoutubeView();
            }
        });

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

        setTextView(instagramTextInputEditText, soloArtistViewModel.getInstagram());
        setTextView(facebookTextInputEditText, soloArtistViewModel.getFacebook());
        setTextView(youtubeTextInputEditText, soloArtistViewModel.getYoutubeChannel());

        String avatarUrl = soloArtistViewModel.getAvatarUrl();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            setImageURI(soloArtistProfilePictureImageView, avatarUrl);
            setTextView(soloArtistAddEditProfilePictureTextView, R.string.edit_profile_pic);
        } else {
            extractProfilePicFromFacebook();
        }

        soloArtistProfilePictureImageView.setOnClickListener(tempView -> {
            if (getActivity() != null) {
                ImagePickerUploader uploader = new ImagePickerUploader(MediaSelector.CropType.Circle);
                Disposable d = uploader.choosePhoto(getActivity()).subscribe((result, throwable) -> {
                    if (throwable == null) {
                        File file = new File(result.uri);
                        setImageURI(soloArtistProfilePictureImageView, Uri.fromFile(file));
                        setTextView(soloArtistAddEditProfilePictureTextView, R.string.edit_profile_pic);
                        soloArtistViewModel.setAvatarUrl(Uri.fromFile(file).toString());
                    } else {
                        ToastHelper.show(getActivity(), throwable.getLocalizedMessage());
                    }
                });
            }
        });

        youtubeRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                getActivity(),
                LinearLayoutManager.HORIZONTAL,
                false);
        youtubeRecyclerView.setLayoutManager(layoutManager);
        if (!soloArtistViewModel.getYoutubeVideoIds().isEmpty()) {
            InitializeYoutubeView();
        }

        if (!soloArtistViewModel.getSpotifyTrackIds().isEmpty()) {
            spotifyInitialize();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        validate();
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
        setImageURI(soloArtistProfilePictureImageView, soloArtistViewModel.getPhotoUrl());
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
                instagramTextInputEditText.getText().toString().trim() : "";
        basicInfo.put(Keys.instagram, instagram);

        String facebook = (facebookTextInputEditText.getText() != null) ?
                facebookTextInputEditText.getText().toString().trim() : "";
        basicInfo.put(Keys.facebook, facebook);

        String youtubeChannel = (youtubeTextInputEditText.getText() != null) ?
                youtubeTextInputEditText.getText().toString().trim() : "";
        basicInfo.put(Keys.youtube_channel, youtubeChannel);

        basicInfo.put(Keys.proximityalert, Boolean.toString(proximityAlertSwitch.isEnabled()));

        basicInfo.put(Keys.youtube, soloArtistViewModel.getYoutubeVideoIdsAsString());
        basicInfo.put(co.chatsdk.core.dao.Keys.AvatarURL, soloArtistViewModel.getAvatarUrl());

        return basicInfo;
    }

    @OnClick(R.id.addYoutubeButton)
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), YoutubeActivity.class);
        startActivityForResult(intent, RequestCodes.RC_YOUTUBE_SEARCH);
    }

    private void spotifyInitialize() {
        // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(getResources().getString(R.string.spotify_client_id), AuthenticationResponse.Type.TOKEN, AppConstants.SPOTIFY_REDIRECT_URI);

        builder.setShowDialog(true);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();

        if (getActivity() != null)
            AuthenticationClient.openLoginActivity(getActivity(), REQUEST_CODE, request);

        doBindService();
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
                        mAccessToken = response.getAccessToken();
                        showSpotifyTrack(soloArtistViewModel.getSpotifyTrackIds());
                        Log.d(TAG, "Access Token: " + mAccessToken);
                        break;

                    // Auth flow returned an error
                    case ERROR:
                        // Handle error response
                        Log.e(TAG, "Spotify authorization failed.");
                        break;

                    // Most likely auth flow was cancelled
                    default:
                        // Handle other cases
                }
            } else if (requestCode == RequestCodes.RC_YOUTUBE_SEARCH && resultCode == Activity.RESULT_OK) {
                soloArtistViewModel.addYoutubeVideoId(data.getStringExtra("youtube_video"));
                InitializeYoutubeView();
            } else if (requestCode == RequestCodes.RC_SPOTIFY_TRACK && resultCode == Activity.RESULT_OK) {
                soloArtistViewModel.addSpotifyTrackId(data.getStringExtra("spotify_track"));
            } else {
                try {
                    mediaSelector.handleResult(getActivity(), requestCode, resultCode, data);
                } catch (Exception e) {
                    ChatSDK.logError(e);
                }
            }
        }
    }

    //TODO: call when the spotify track adding button is clicked.
    private void launchSpotifySearch() {
        if (mAccessToken != null && !mAccessToken.isEmpty()) {
            Intent intent = SpotifySearchActivity.createIntent(getActivity());
            intent.putExtra(SpotifySearchActivity.EXTRA_TOKEN, mAccessToken);
            startActivityForResult(intent, RequestCodes.RC_SPOTIFY_TRACK);
        } else {
            ToastHelper.show(getContext(), "Spotify search not available");
        }
    }

    private void showSpotifyTrack(List<String> tracks) {
        if (mAccessToken != null) {
            SpotifyApi spotifyApi = new SpotifyApi();
            spotifyApi.setAccessToken(mAccessToken);
            SpotifyService spotifyService = spotifyApi.getService();

            for (String track: tracks) {
                String trackId = track.replace("spotify:track:", "");
                spotifyService.getTrack(trackId, new SpotifyCallback<Track>() {
                    @Override
                    public void success(Track track, Response response) {
                        Image image = track.album.images.get(0);
                        String trackName = track.name;
                        List<String> names = new ArrayList<>();
                        for (ArtistSimple i : track.artists) {
                            names.add(i.name);
                        }

                        Joiner joiner = Joiner.on(", ");
                        String artist = joiner.join(names);

                        //TODO: After designers specify where to add the spotify track make UI changes.
                        View spotifyView = null; //= layoutInflater.inflate(R.layout.view_spotify_track, mContainer, false);
                        if (spotifyView != null) {
                            ImageView albumCover = spotifyView.findViewById(R.id.track_album_cover);
                            Picasso.with(getContext()).load(image.url).into(albumCover);
                            TextView trackNameTextView = spotifyView.findViewById(R.id.track_title);
                            trackNameTextView.setText(trackName);
                            TextView artistTextView = spotifyView.findViewById(R.id.track_artist);
                            artistTextView.setText(artist);
                            //addView(spotifyView);
                            spotifyView.setOnClickListener(v -> {
                                if (mPlayerBoundService != null) {
                                    mPlayerBoundService.playTrack(track);
                                } else {
                                    Log.e(TAG, "Player not initialized");
                                }
                            });
                        }
                    }

                    @Override
                    public void failure(SpotifyError spotifyError) {
                        Log.e(TAG, spotifyError.getMessage());
                    }
                });
            }
        } else {
            Log.e(TAG,"No valid access token");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void InitializeYoutubeView() {
        List<String> youtubeVideoIds = new ArrayList<>();

        youtubeVideoIds = soloArtistViewModel.getYoutubeVideoIds();

        youtubePlayerAdapter = new YoutubePlayerAdapter(youtubeVideoIds, this.getLifecycle());
        youtubeRecyclerView.setAdapter(youtubePlayerAdapter);
    }

    private void doBindService() {
        if (getActivity() != null) {
            if (getActivity().bindService(
                    new Intent(getActivity(), Player.class),
                    mServiceConnection, Context.BIND_AUTO_CREATE)) {
                mSpotifyPlayerBound = true;
            }
        }
    }

    private void doUnbindService() {
        if (getActivity() != null) {
            if (mSpotifyPlayerBound) {
                getActivity().unbindService(mServiceConnection);
                mSpotifyPlayerBound = false;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        doUnbindService();
    }
}
