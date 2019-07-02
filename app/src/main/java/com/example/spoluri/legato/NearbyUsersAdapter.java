package com.example.spoluri.legato;

import android.content.Context;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import 	androidx.recyclerview.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.chatsdk.core.dao.User;
import co.chatsdk.core.session.ChatSDK;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;

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

    // 2. Override the onCreateViewHolder method
    @NonNull
    @Override
    public NearbyUserHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new NearbyUserHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull NearbyUserHolder holder, int position) {

        // 5. Use position to access the correct NearbyUser object
        NearbyUser nearbyUser = this.nearbyUsersFiltered.get(position);

        // 6. Bind the nearbyUser object to the holder
        holder.bindNearbyUser(nearbyUser);
    }

    @Override
    public int getItemCount() {
        return this.nearbyUsersFiltered.size();
    }

    public void onFilter(Filters filters) {

        List<NearbyUser> filteredList = new ArrayList<>();
        for (NearbyUser row : nearbyUsers) {
            if (filters.hasLookingfor()) {
                if (row.getLookingfor().contentEquals(filters.getLookingfor())) {
                    filteredList.add(row);
                }
            }
            if (filters.hasGenres()) {
                if (row.getGenres().contains(filters.getGenres())) {
                    filteredList.add(row);
                }
            }
            if (filters.hasSkills()) {
                if (row.getSkills().contains(filters.getSkills())) {
                    filteredList.add(row);
                }
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
