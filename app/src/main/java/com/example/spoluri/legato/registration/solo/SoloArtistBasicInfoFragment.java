package com.example.spoluri.legato.registration.solo;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import id.zelory.compressor.Compressor;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.spoluri.legato.AppConstants;
import com.example.spoluri.legato.Keys;
import com.example.spoluri.legato.R;
import com.example.spoluri.legato.youtube.YoutubeActivity;
import com.example.spoluri.legato.RequestCodes;
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private boolean valid;

    @BindView(R.id.proximityAlertSwitch)
    Switch proximitySwitch;
    @BindView(R.id.jamCheckBox)
    CheckBox jamCheckBox;
    @BindView(R.id.collaborateCheckBox)
    CheckBox collaborateCheckBox;
    @BindView(R.id.startBandCheckBox)
    CheckBox startBandCheckBox;
    @BindView(R.id.proximityRadiusValue)
    TextView proximityRadiusValue;
    @BindView(R.id.proximityRadiusSeekBar)
    SeekBar seekBarProximity;
    @BindView(R.id.searchRadiusValue)
    TextView searchRadiusValue;
    @BindView(R.id.searchRadiusSeekBar)
    SeekBar seekBarSearch;
    @BindView(R.id.soloDisplayNameTextInputEditText)
    TextInputEditText soloDisplayNameTextInputEditText;
    @BindView(R.id.instagramTextInputEditText)
    TextInputEditText instagramTextInputEditText;
    @BindView(R.id.facebookTextInputEditText)
    TextInputEditText facebookTextInputEditText;
    @BindView(R.id.youtubeTextInputEditText)
    TextInputEditText youtubeTextInputEditText;
    @BindView(R.id.addSampleButton1)
    FloatingActionButton addSampleButton1;
    @BindView(R.id.soloArtistProfilePictureImageView)
    SimpleDraweeView soloArtisitProfilePicImageView;

    private String mYoutubeVideo;
    private View youtubeView;
    private LayoutInflater layoutInflater;
    private ViewGroup mContainer;
    private String avatarUrl;
    protected MediaSelector mediaSelector;

    public SoloArtistBasicInfoFragment() {
        valid = false;
        mYoutubeVideo = "";
        mediaSelector = new MediaSelector();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solo_artist_basic_info, container, false);
        ButterKnife.bind(this, view);

        layoutInflater = inflater;
        mContainer = container;

        jamCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validate();
            }
        });

        collaborateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validate();
            }
        });

        startBandCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validate();
            }
        });

        addSampleButton1.setOnClickListener(this);

        seekBarProximity.setEnabled(false);
        seekBarProximity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                proximityRadiusValue.setText(progress + "ft");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        seekBarSearch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                searchRadiusValue.setText(progress + "mi");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                //write custom code to on start progress
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        proximitySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    seekBarProximity.setEnabled(true);
                }
                else {
                    seekBarProximity.setEnabled(false);
                }

                validate();
            }
        });

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

        soloArtisitProfilePicImageView.setOnClickListener(tempView -> {
                mediaSelector.startChooseImageActivity(getActivity(), MediaSelector.CropType.Circle,result -> {

                    try {
                        File compress = new Compressor(ChatSDK.shared().context())
                                .setMaxHeight(ChatSDK.config().imageMaxThumbnailDimension)
                                .setMaxWidth(ChatSDK.config().imageMaxThumbnailDimension)
                                .compressToFile(new File(result));

                        Bitmap bitmap = BitmapFactory.decodeFile(compress.getPath());

                        // Cache the file
                        File file = ImageUtils.compressImageToFile(ChatSDK.shared().context(), bitmap, ChatSDK.currentUserID(), ".png");
                        soloArtisitProfilePicImageView.setImageURI(Uri.fromFile(file));
                        avatarUrl = Uri.fromFile(file).toString();
                    }
                    catch (Exception e) {
                        ChatSDK.logError(e);
                    }
                });
        });

        return view;
    }

    private void validate() {
        valid = (jamCheckBox.isChecked() || collaborateCheckBox.isChecked() || startBandCheckBox.isChecked()) &&
                !soloDisplayNameTextInputEditText.getText().toString().trim().isEmpty();
        if (valid) {
            ((SoloRegistrationActivity) getActivity()).setVisibleTabCount(2);
        } else {
            ((SoloRegistrationActivity)getActivity()).setVisibleTabCount(1);
        }
    }

    public HashMap<String, String> extractData() {
        HashMap<String, String> basicInfo = new HashMap<>();
        basicInfo.put(co.chatsdk.core.dao.Keys.Name, soloDisplayNameTextInputEditText.getText().toString().trim());
        String lookingfor = jamCheckBox.isChecked()?"Jam Session":"" +
                (collaborateCheckBox.isChecked()?"Ccllaboration":"") +
                (startBandCheckBox.isChecked()?"Start a Band":"");
        basicInfo.put(Keys.lookingfor, lookingfor);
        basicInfo.put(Keys.searchradius, Integer.toString(seekBarSearch.getProgress()));
        basicInfo.put(Keys.instagram, instagramTextInputEditText.getText().toString().trim());
        basicInfo.put(Keys.facebook, facebookTextInputEditText.getText().toString().trim());
        basicInfo.put(Keys.youtube_channel, youtubeTextInputEditText.getText().toString().trim());
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
            InitializeYoutubeView();
        }
        else {
            try {
                mediaSelector.handleResult(getActivity(), requestCode, resultCode, data);
            } catch (Exception e) {
                ChatSDK.logError(e);
            }
        }
    }

    private void InitializeYoutubeView() {
        LinearLayout galleryHorizontalScrollViewLayout = getView().findViewById(R.id.galleryHorizontalScrollViewLayout1);
        youtubeView = layoutInflater.inflate(R.layout.youtube_frame_layout, mContainer, false);
        galleryHorizontalScrollViewLayout.addView(youtubeView);
        YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.youtube_frame_layout, youTubePlayerFragment);
        transaction.addToBackStack(null);
        transaction.commit();

        youTubePlayerFragment.initialize(AppConstants.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {

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
