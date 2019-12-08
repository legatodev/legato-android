package com.legato.music.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.legato.music.models.Filters;
import com.legato.music.views.adapters.holders.NearbyUserHolder;
import com.legato.music.models.NearbyUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.chatsdk.core.utils.DisposableList;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUserHolder> {

    @Nullable private List<NearbyUser> mNearbyUsers;
    private final Context context;
    private final int itemResource;
    private DisposableList disposableList;
    @Nullable private Filters mfilters;

    public NearbyUsersAdapter(Context context, int itemResource) {
        this.disposableList = new DisposableList();
        this.context = context;
        this.itemResource = itemResource;
        this.mfilters = null;
    }

    public void setFilters(Filters filters) {

        this.mfilters = filters;
    }

    public void updateNearbyUsers(List<NearbyUser> nearbyusers){

        mNearbyUsers = (ArrayList<NearbyUser>)((ArrayList<NearbyUser>)nearbyusers).clone();

        Completable completable = Completable.complete();
        disposableList.add(completable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    onFilter();
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
        if (this.mNearbyUsers != null) {
            NearbyUser nearbyUser = this.mNearbyUsers.get(position);
            holder.bindNearbyUser(nearbyUser);
        }
    }

    @Override
    public int getItemCount() {
        return this.mNearbyUsers != null?this.mNearbyUsers.size():0;
    }

    private void onFilter() {

        if(mNearbyUsers == null)
            return;

        if(mfilters == null){
        /*
        This should be called when app loads for first time.
         */
            notifyItemInserted(this.mNearbyUsers.size() - 1);
            return;
        }

        List<NearbyUser> filteredList = new ArrayList<>();

        for (NearbyUser row : mNearbyUsers) {
            boolean addRow;
            if (mfilters.hasLookingfor() && row.getLookingfor() != null) {
                addRow = row.getLookingfor().contains(mfilters.getLookingfor());
            }
            else {
                addRow = true;
            }

            if (addRow) {
                if (mfilters.hasGenres() && row.getGenres() != null) {
                    addRow = row.getGenres().contains(mfilters.getGenres());
                }
                else {
                    addRow = true;
                }
            }

            if (addRow) {
                if (mfilters.hasSkills() && row.getSkills() != null) {
                    addRow = row.getSkills().contains(mfilters.getSkills());
                }
                else {
                    addRow = true;
                }
            }

            if (addRow) {
                filteredList.add(row);
            }
        }

        mNearbyUsers = filteredList;

        if (mfilters.hasSortBy()) {
            Collections.sort(mNearbyUsers, NearbyUser.sortByDistance);
        }

        notifyDataSetChanged();
    }

    public void onDestroy() {
        if(mNearbyUsers != null)
            mNearbyUsers.clear();
        disposableList.dispose();
    }
}
