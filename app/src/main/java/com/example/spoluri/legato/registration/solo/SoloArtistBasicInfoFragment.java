package com.example.spoluri.legato.registration.solo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.spoluri.legato.Keys;
import com.example.spoluri.legato.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SoloArtistBasicInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SoloArtistBasicInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SoloArtistBasicInfoFragment extends Fragment {
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

    public SoloArtistBasicInfoFragment() {
        // Required empty public constructor
        valid = false;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SoloArtistBasicInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SoloArtistBasicInfoFragment newInstance(String param1, String param2) {
        SoloArtistBasicInfoFragment fragment = new SoloArtistBasicInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_solo_artist_basic_info, container, false);
        ButterKnife.bind(this, view);

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
                validate();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
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

        return basicInfo;
    }
}
