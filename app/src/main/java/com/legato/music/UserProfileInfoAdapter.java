package com.legato.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class UserProfileInfoAdapter extends RecyclerView.Adapter<UserProfileInfoHolder> {

    private List<UserProfileInfo> userProfileInfoList;
    private final Context context;
    private final int itemResource;

    public UserProfileInfoAdapter(Context context, int itemResource) {
        this.context = context;
        this.itemResource = itemResource;
        this.userProfileInfoList = new ArrayList<>();
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
        UserProfileInfo userProfileInfo = this.userProfileInfoList.get(position);
        holder.bindUserProfileInfo(userProfileInfo);
    }

    @Override
    public int getItemCount() {
        return this.userProfileInfoList.size();
    }

    public void notifyData(ArrayList<UserProfileInfo> userProfileInfoList) {
        this.userProfileInfoList = userProfileInfoList;
        notifyDataSetChanged();
    }
}
