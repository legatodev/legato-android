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
import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.ImageUtils;
import co.chatsdk.ui.chat.MediaSelector;
import co.chatsdk.ui.utils.ToastHelper;
import id.zelory.compressor.Compressor;
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

    private boolean valid;
    @BindView(R.id.proximityAlertSwitch)
    @Nullable Switch proximitySwitch;
    @BindView(R.id.jamCheckBox)
    @Nullable CheckBox jamCheckBox;
    @BindView(R.id.collaborateCheckBox)
    @Nullable CheckBox collaborateCheckBox;
    @BindView(R.id.startBandCheckBox)
    @Nullable CheckBox startBandCheckBox;
    @BindView(R.id.soloDisplayNameTextInputEditText)
    @Nullable TextInputEditText soloDisplayNameTextInputEditText;
    @BindView(R.id.instagramTextInputEditText)
    @Nullable TextInputEditText instagramTextInputEditText;
    @BindView(R.id.facebookTextInputEditText)
    @Nullable TextInputEditText facebookTextInputEditText;
    @BindView(R.id.youtubeTextInputEditText)
    @Nullable TextInputEditText youtubeTextInputEditText;
    @BindView(R.id.addYoutubeButton)
    @Nullable FloatingActionButton addSampleButton;
    @BindView(R.id.resetButton)
    @Nullable FloatingActionButton resetButton;
    @BindView(R.id.soloArtistProfilePictureImageView)
    @Nullable SimpleDraweeView soloArtisitProfilePicImageView;
    @BindView(R.id.soloArtistAddEditProfilePictureTextView)
    @Nullable TextView soloArtistAddEditProfilePicTextView;

    @BindView(R.id.youtubeRecyclerView)
    @Nullable
    protected RecyclerView youtubeRecyclerView;

    @Nullable
    private YoutubePlayerAdapter youtubePlayerAdapter;

    @Nullable
    private SoloArtistViewModel soloArtistViewModel;

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

    protected MediaSelector mediaSelector;
    private String mAccessToken = "";
    User user = ChatSDK.currentUser();

    public SoloArtistBasicInfoFragment() {
        valid = false;
        mediaSelector = new MediaSelector();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solo_artist_basic_info, container, false);
        ButterKnife.bind(this, view);

        soloArtistViewModel = ViewModelProviders.of(getActivity()).get(SoloArtistViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (soloArtistViewModel != null) {

            Resources res = getResources();
            String[] rlookingfor = res.getStringArray(R.array.looking_for);
            String dblookingfor = soloArtistViewModel.getLookingFor();

            if (dblookingfor != null) {
                if (dblookingfor.contains(rlookingfor[0]) && jamCheckBox != null)
                    jamCheckBox.setChecked(true);

                if (dblookingfor.contains(rlookingfor[1]) && collaborateCheckBox != null)
                    collaborateCheckBox.setChecked(true);

                if (dblookingfor.contains(rlookingfor[2]) && startBandCheckBox != null)
                    startBandCheckBox.setChecked(true);
            }

            setOnCheckedChanged(jamCheckBox);
            setOnCheckedChanged(collaborateCheckBox);
            setOnCheckedChanged(startBandCheckBox);

            if (resetButton != null)
                resetButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (soloArtistViewModel != null) {
                            soloArtistViewModel.resetYoutubeVideoIds();
                            InitializeYoutubeView();
                        }
                    }
                });

            if (proximitySwitch != null) {
                proximitySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> validate());
            }

            setTextView(soloDisplayNameTextInputEditText, soloArtistViewModel.getUser().getName());

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

            setTextView(instagramTextInputEditText, soloArtistViewModel.getInstagram());
            setTextView(facebookTextInputEditText, soloArtistViewModel.getFacebook());
            setTextView(youtubeTextInputEditText, soloArtistViewModel.getYoutubeChannel());

            String avatarUrl = soloArtistViewModel.getAvatarUrl();
            if (avatarUrl != null && !avatarUrl.isEmpty()) {
                setImageURI(soloArtisitProfilePicImageView, avatarUrl);
                setTextView(soloArtistAddEditProfilePicTextView, R.string.edit_profile_pic);
            } else {
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

                            if (soloArtistViewModel != null) {
                                soloArtistViewModel.setAvatarUrl(Uri.fromFile(file).toString());
                            }
                        } catch (Exception e) {
                            ChatSDK.logError(e);
                        }
                    });
                });
            }

            if (youtubeRecyclerView != null) {
                youtubeRecyclerView.setHasFixedSize(true);

                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(
                        getActivity(),
                        LinearLayoutManager.HORIZONTAL,
                        false);
                youtubeRecyclerView.setLayoutManager(layoutManager);

                if (!soloArtistViewModel.getYoutubeVideoIds().isEmpty()) {
                    InitializeYoutubeView();
                }
            }

            if (user.metaStringForKey(Keys.spotify_track) != null) {
                spotifyInitialize();
            }

            validate();
        }
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
        if (soloArtisitProfilePicImageView == null ||
                soloArtistAddEditProfilePicTextView == null ||
                soloArtistViewModel == null) {
            return;
        }

        setImageURI(soloArtisitProfilePicImageView, soloArtistViewModel.getPhotoUrl());
        setTextView(soloArtistAddEditProfilePicTextView, R.string.edit_profile_pic);
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
        if (instagramTextInputEditText != null)
            basicInfo.put(Keys.instagram, instagramTextInputEditText.getText().toString().trim());
        if (facebookTextInputEditText != null)
            basicInfo.put(Keys.facebook, facebookTextInputEditText.getText().toString().trim());
        if (youtubeTextInputEditText != null)
            basicInfo.put(Keys.youtube_channel, youtubeTextInputEditText.getText().toString().trim());
        if (proximitySwitch != null) {
            basicInfo.put(Keys.proximityalert, Boolean.toString(proximitySwitch.isEnabled()));
        }

        if (soloArtistViewModel != null) {
            basicInfo.put(Keys.youtube, soloArtistViewModel.getYoutubeVideoIdsAsString());
            basicInfo.put(co.chatsdk.core.dao.Keys.AvatarURL, soloArtistViewModel.getAvatarUrl());
        }

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
                        showSpotifyTrack(user.metaStringForKey(Keys.spotify_track));
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
            } else if (requestCode == RequestCodes.RC_YOUTUBE_SEARCH && resultCode == Activity.RESULT_OK && data != null) {
                if (soloArtistViewModel != null) {
                    soloArtistViewModel.addYoutubeVideoId(data.getStringExtra("youtube_video"));
                    InitializeYoutubeView();
                }
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
            startActivity(intent);
        } else {
            ToastHelper.show(getContext(), "Spotify search not available");
        }
    }

    private void showSpotifyTrack(String track) {
        if (mAccessToken != null) {
            SpotifyApi spotifyApi = new SpotifyApi();
            spotifyApi.setAccessToken(mAccessToken);
            SpotifyService spotifyService = spotifyApi.getService();

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
        } else {
            Log.e(TAG,"No valid access token");
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void InitializeYoutubeView() {
        if (youtubeRecyclerView != null) {
            List<String> youtubeVideoIds = new ArrayList<>();

            if (soloArtistViewModel != null) {
                youtubeVideoIds = soloArtistViewModel.getYoutubeVideoIds();
            }

            youtubePlayerAdapter = new YoutubePlayerAdapter(youtubeVideoIds, this.getLifecycle());
            youtubeRecyclerView.setAdapter(youtubePlayerAdapter);
        }
    }

    private void doBindService() {
        if (getActivity().bindService(
                new Intent(getActivity(), Player.class),
                mServiceConnection, Context.BIND_AUTO_CREATE)) {
            mSpotifyPlayerBound = true;
        }
    }

    private void doUnbindService() {
        if (mSpotifyPlayerBound) {
            getActivity().unbindService(mServiceConnection);
            mSpotifyPlayerBound = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        doUnbindService();
    }
}
