package com.example.spoluri.legato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import io.reactivex.Completable;

class UserProfileInfoAdapter extends RecyclerView.Adapter<UserProfileInfoHolder> {

    private List<UserProfileInfo> userProfileInfos;
    private final Context context;
    private final int itemResource;

    public UserProfileInfoAdapter(Context context, int itemResource) {
        this.context = context;
        this.itemResource = itemResource;
        this.userProfileInfos = new ArrayList<>();
    }

    // 2. Override the onCreateViewHolder method
    @NonNull
    @Override
    public UserProfileInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new UserProfileInfoHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull UserProfileInfoHolder holder, int position) {

        // 5. Use position to access the correct NearbyUser object
        UserProfileInfo userProfileInfo = this.userProfileInfos.get(position);

        // 6. Bind the nearbyUser object to the holder
        holder.bindUserProfileInfo(userProfileInfo);
    }

    @Override
    public int getItemCount() {
        return this.userProfileInfos.size();
    }

    public void notifyData(ArrayList<UserProfileInfo> userProfileInfos) {
        this.userProfileInfos = userProfileInfos;
        notifyDataSetChanged();
    }
}
