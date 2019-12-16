package com.legato.music.views.adapters;

import android.content.Context;
import android.util.Log;
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

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.DisposableList;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;


public class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUserHolder> {

    public static final String TAG = NearbyUsersAdapter.class.getSimpleName();

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
        this.mNearbyUsers = new ArrayList<>();
    }

    public void setFilters(Filters filters) {

        this.mfilters = filters;
    }

    public void updateNearbyUsers(List<NearbyUser> nearbyusers){

        if(mNearbyUsers != null)
            mNearbyUsers.clear();

        for(NearbyUser nearbyUser: nearbyusers){
            Completable completable = ChatSDK.core().userOn(nearbyUser.getUser());
            disposableList.add(completable.observeOn(AndroidSchedulers.mainThread())
                                          .subscribe(() -> {
                                            /* User object has now been populated and is ready to use.
                                               ChatSDK populates User Meta information is a separate thread.
                                               So its necessary to wait for User object to be completely Ready.
                                               Hence ChatSDK.core().userOn.
                                             */
                                            if(mfilters != null)
                                                onFilter(nearbyUser);
                                            else{
                                                /*
                                                This will be executed when Filter object has not been created yet.
                                                E.g.: When app loads for the first time
                                                 */
                                                if(mNearbyUsers != null)
                                                    mNearbyUsers.add(nearbyUser);
                                                notifyDataSetChanged();
                                            }
                                          }, throwable -> {
                                                Log.e(TAG,"Exception occured in ChatSDK core at time populating meta field for User obj");
                                             }
                                          ));
        }
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

    private void onFilter(NearbyUser row) {

        if(mNearbyUsers == null || mfilters == null) {
            Log.e(TAG,"Exit. NearbyUser list or selected filter is Null.");
            return;
        }

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
                mNearbyUsers.add(row);
            }

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
