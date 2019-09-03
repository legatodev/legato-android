package com.legato.music.registration.band;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;

import java.util.Arrays;

import io.reactivex.annotations.Nullable;

public class BandMembersHolder extends RecyclerView.ViewHolder {

    private final Spinner mPositionSpinner;
    private final SearchView mBandMemberSearchView;

    private final String[] mSkillsArray;
    @Nullable private BandMember mBandMember;

    public BandMembersHolder(Context context, View itemView) {
        super(itemView);

        // 1. Set the context
        this.mPositionSpinner = itemView.findViewById(R.id.positionSpinner);
        this.mBandMemberSearchView = itemView.findViewById(R.id.bandMemberSearchView);

        // Creating ArrayAdapter using the string array and default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.skills_array, android.R.layout.simple_spinner_item);
        mSkillsArray = itemView.getResources().getStringArray(R.array.skills_array);

        // Specify layout to be used when list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Applying the adapter to our spinner
        mPositionSpinner.setAdapter(adapter);
    }

    public void bindBandMember(BandMember bandMember) {
        // 4. Bind the data to the ViewHolder
        this.mBandMember = bandMember;
        //What to do with the searchview
        this.mPositionSpinner.setSelection(Arrays.asList(mSkillsArray).indexOf(bandMember.getPosition()));
    }
}
