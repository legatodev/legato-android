package com.legato.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legato.music.utils.GeofireHelper;
import com.legato.music.utils.Keys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
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
    @Nullable private Filters mfilters;

    public NearbyUsersAdapter(Context context, int itemResource) {
        this.disposableList = new DisposableList();
        this.context = context;
        this.itemResource = itemResource;
        this.nearbyUsers = new ArrayList<>();
        this.mfilters = null;
    }

    @Nullable
    public Filters getFilters() {
        return mfilters;
    }

    public void setFilters(Filters filters) {
        this.mfilters = filters;
    }

    /*
        TODO: Below look will have to be improved if nearbyUser list grows bigger
        Maybe hashmap will be better???
         */
    private Boolean isUserInNearbyUserList(User user){

        if(this.nearbyUsers.size()==0)
            return false;

        for(NearbyUser u: this.nearbyUsers){
            if(u.getEntityID().equals(user.getEntityID())){
                return true;
            }
        }
        return false;
    }

    public void addNearbyUserToRecyclerView(String userId, String distance) {
        User user = ChatSDK.db().fetchOrCreateEntityWithEntityID(User.class, userId);
        Completable completable = ChatSDK.core().userOn(user);
        disposableList.add(completable.observeOn(AndroidSchedulers.mainThread()).subscribe(() -> {
            // User object has now been populated and is ready to use
            if (!user.isMe()) {
                if(!isUserInNearbyUserList(user)){
                    NearbyUser nearbyUser = new NearbyUser(user, distance);
                    this.nearbyUsers.add(nearbyUser);
                }
                onFilter(mfilters);
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

    public void onFilter(@Nullable Filters filters) {

        if(filters == null){
            /*
            This should be called when app loads for first time.
             */
            this.nearbyUsersFiltered = this.nearbyUsers;
            notifyItemInserted(this.nearbyUsersFiltered.size() - 1);
            return;
        }

        List<NearbyUser> filteredList = new ArrayList<>();
        /*
        1] GetSearchRadius.
        2] If radius is different than previous , update User.MetaKey and get new list based on updated radius.
        3] This should update nearbyUsers list
         */
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

        if (filters.hasSortBy()) {
            Collections.sort(nearbyUsersFiltered, NearbyUser.sortByDistance);
        }

        notifyDataSetChanged();
    }

    public void onDestroy() {
        nearbyUsers.clear();
        disposableList.dispose();
    }
}
