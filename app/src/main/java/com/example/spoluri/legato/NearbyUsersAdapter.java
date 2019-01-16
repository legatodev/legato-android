package com.example.spoluri.legato;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import java.util.List;

class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUsersHolder> {

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
    public NearbyUsersHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new NearbyUsersHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(NearbyUsersHolder holder, int position) {

        // 5. Use position to access the correct Bakery object
        NearbyUser bakery = this.nearbyUsers.get(position);

        // 6. Bind the bakery object to the holder
        holder.bindNearbyUser(bakery);
    }

    @Override
    public int getItemCount() {

        return this.nearbyUsers.size();
    }
}
