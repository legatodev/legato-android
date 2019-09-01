package com.legato.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUserHolder> {

    private List<NearbyUser> nearbyUsers;
    @Nullable private List<NearbyUser> nearbyUsersFiltered;
    private final Context context;
    private final int itemResource;
    private DisposableList disposableList;

    public NearbyUsersAdapter(Context context, int itemResource) {
        this.disposableList = new DisposableList();
        this.context = context;
        this.itemResource = itemResource;
        this.nearbyUsers = new ArrayList<>();
    }

    public void addNearbyUserToRecyclerView(String userId, String distance) {
        User user = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, userId);
        Completable completable = ChatSDK.core().userOn(user);
        disposableList.add(completable.observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
            // User object has now been populated and is ready to use
            if (!user.isMe()) {
                NearbyUser nearbyUser = new NearbyUser(user, distance);
                this.nearbyUsers.add(nearbyUser);
                this.nearbyUsersFiltered = this.nearbyUsers;
                notifyItemInserted(this.nearbyUsersFiltered.size() - 1);
            }
        }, throwable -> {

        }));
    }

    @NonNull
    @Override
    public NearbyUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new NearbyUserHolder(this.context, view);
    }

    @Override
    public void onBindViewHolder(@NonNull NearbyUserHolder holder, int position) {
        if (this.nearbyUsersFiltered != null) {
            NearbyUser nearbyUser = this.nearbyUsersFiltered.get(position);
            holder.bindNearbyUser(nearbyUser);
        }
    }

    @Override
    public int getItemCount() {
        return this.nearbyUsersFiltered != null?this.nearbyUsersFiltered.size():0;
    }

    public void onFilter(Filters filters) {

        List<NearbyUser> filteredList = new ArrayList<>();
        for (NearbyUser row : nearbyUsers) {
            boolean addRow;
            if (filters.hasLookingfor()&& row.getLookingfor() != null) {
                addRow = row.getLookingfor().contentEquals(filters.getLookingfor());
            }
            else {
                addRow = true;
            }

            if (addRow) {
                if (filters.hasGenres() && row.getGenres() != null) {
                    addRow = row.getGenres().contains(filters.getGenres());
                }
                else {
                    addRow = true;
                }
            }

            if (addRow) {
                if (filters.hasSkills() && row.getSkills() != null) {
                    addRow = row.getSkills().contains(filters.getSkills());
                }
                else {
                    addRow = true;
                }
            }

            if (addRow) {
                filteredList.add(row);
            }
        }

        nearbyUsersFiltered = filteredList;
        notifyDataSetChanged();
    }

    public void onDestroy() {
        nearbyUsers.clear();
        disposableList.dispose();
    }
}
