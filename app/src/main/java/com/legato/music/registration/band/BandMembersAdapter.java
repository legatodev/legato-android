package com.legato.music.registration.band;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

class BandMembersAdapter extends RecyclerView.Adapter<BandMembersHolder>{

    private final List<BandMember> bandMembers;
    private final Context context;
    private final int itemResource;

    public BandMembersAdapter(Context context, int itemResource, List<BandMember> bandMembers) {
        this.bandMembers = bandMembers;
        this.context = context;
        this.itemResource = itemResource;
    }

    // 2. Override the onCreateViewHolder method
    @NonNull
    @Override
    public BandMembersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new BandMembersHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull BandMembersHolder holder, int position) {

        // 5. Use position to access the correct NearbyUser object
        BandMember bandMember = this.bandMembers.get(position);

        // 6. Bind the nearbyUser object to the holder
        holder.bindBandMember(bandMember);
    }

    @Override
    public int getItemCount() {

        return this.bandMembers.size();
    }
}
