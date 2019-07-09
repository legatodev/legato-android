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

class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUserHolder> {

    private List<NearbyUser> nearbyUsers;
    private List<NearbyUser> nearbyUsersFiltered;
    private final Context context;
    private final int itemResource;

    public NearbyUsersAdapter(Context context, int itemResource) {
        this.context = context;
        this.itemResource = itemResource;
        this.nearbyUsers = new ArrayList<>();
        Iterator it = GeofireHelper.getInstance().getNearbyUsers().entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            User user = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, (String)pair.getKey());
            Completable completable = ChatSDK.core().userOn(user);
            completable.subscribe(() -> {
                // User object has now been populated and is ready to use
                if (!user.isMe()) {
                    this.nearbyUsers.add(new NearbyUser(user, (String) pair.getValue()));
                }
            }, throwable -> {

            });
        }

        this.nearbyUsersFiltered = this.nearbyUsers;
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
        NearbyUser nearbyUser = this.nearbyUsersFiltered.get(position);
        holder.bindNearbyUser(nearbyUser);
    }

    @Override
    public int getItemCount() {
        return this.nearbyUsersFiltered.size();
    }

    public void onFilter(Filters filters) {

        List<NearbyUser> filteredList = new ArrayList<>();
        for (NearbyUser row : nearbyUsers) {
            boolean addRow = false;
            if (filters.hasLookingfor()) {
                addRow = row.getLookingfor().contentEquals(filters.getLookingfor());
            }

            if (addRow) {
                if (filters.hasGenres()) {
                    addRow = row.getGenres().contains(filters.getGenres());
                }
            }

            if (addRow) {
                if (filters.hasSkills()) {
                    addRow = row.getSkills().contains(filters.getSkills());
                }
            }

            if (addRow) {
                filteredList.add(row);
            }

            /*If nothing is selected from filter*/
            if(!filters.hasLookingfor() &&
                    !filters.hasGenres() &&
                    !filters.hasSkills()){
                filteredList.add(row);
            }
        }

        nearbyUsersFiltered = filteredList;
        notifyDataSetChanged();
    }
}
