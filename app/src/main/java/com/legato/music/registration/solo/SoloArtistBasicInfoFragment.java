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
import com.legato.music.R;
import com.legato.music.views.adapters.YoutubePlayerAdapter;
import com.legato.music.utils.Keys;
import com.legato.music.utils.RequestCodes;
import com.legato.music.viewmodels.SoloArtistViewModel;
import com.legato.music.youtube.YoutubeActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.ImageUtils;
import co.chatsdk.ui.chat.MediaSelector;
import id.zelory.compressor.Compressor;

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
    @BindView(R.id.soloDisplayNameTextInputEditText)
    @Nullable TextInputEditText soloDisplayNameTextInputEditText;
    @BindView(R.id.instagramTextInputEditText)
    @Nullable TextInputEditText instagramTextInputEditText;
    @BindView(R.id.facebookTextInputEditText)
    @Nullable TextInputEditText facebookTextInputEditText;
    @BindView(R.id.youtubeTextInputEditText)
    @Nullable TextInputEditText youtubeTextInputEditText;
    @BindView(R.id.addSampleButton)
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

    protected MediaSelector mediaSelector;

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

            if (addSampleButton != null)
                addSampleButton.setOnClickListener(this);

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
                            e.printStackTrace();
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

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), YoutubeActivity.class);
        startActivityForResult(intent, RequestCodes.RC_YOUTUBE_SEARCH);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.RC_YOUTUBE_SEARCH && resultCode == Activity.RESULT_OK && data != null) {
            if (soloArtistViewModel != null) {
                soloArtistViewModel.addYoutubeVideoId(data.getStringExtra("youtube_video"));
                InitializeYoutubeView();
            }
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
        if (youtubeRecyclerView != null) {
            List<String> youtubeVideoIds = new ArrayList<>();

            if (soloArtistViewModel != null) {
                youtubeVideoIds = soloArtistViewModel.getYoutubeVideoIds();
            }

            youtubePlayerAdapter = new YoutubePlayerAdapter(youtubeVideoIds, this.getLifecycle());
            youtubeRecyclerView.setAdapter(youtubePlayerAdapter);
        }
    }
}
