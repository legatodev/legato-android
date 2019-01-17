package com.example.spoluri.legato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;

import java.util.List;

class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUserHolder> {

    private final List<NearbyUser> nearbyUsers;
    private Context context;
    private int itemResource;

    public NearbyUsersAdapter(Context context, int itemResource, List<NearbyUser> nearbyUsers) {
        this.nearbyUsers = nearbyUsers;
        this.context = context;
        this.itemResource = itemResource;
    }

    // 2. Override the onCreateViewHolder method
    @Override
    public NearbyUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new NearbyUserHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(NearbyUserHolder holder, int position) {

        // 5. Use position to access the correct NearbyUser object
        NearbyUser nearbyUser = this.nearbyUsers.get(position);

        // 6. Bind the nearbyUser object to the holder
        holder.bindNearbyUser(nearbyUser);
    }

    @Override
    public int getItemCount() {

        return this.nearbyUsers.size();
    }
}
