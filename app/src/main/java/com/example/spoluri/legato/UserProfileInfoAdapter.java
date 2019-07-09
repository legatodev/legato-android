package com.example.spoluri.legato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

class UserProfileInfoAdapter extends RecyclerView.Adapter<UserProfileInfoHolder> {

    private List<UserProfileInfo> userProfileInfos;
    private final Context context;
    private final int itemResource;

    public UserProfileInfoAdapter(Context context, int itemResource) {
        this.context = context;
        this.itemResource = itemResource;
        this.userProfileInfos = new ArrayList<>();
    }

    @NonNull
    @Override
    public UserProfileInfoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new UserProfileInfoHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileInfoHolder holder, int position) {
        UserProfileInfo userProfileInfo = this.userProfileInfos.get(position);
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
