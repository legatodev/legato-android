package com.example.spoluri.legato;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

class NearbyUsersAdapter extends RecyclerView.Adapter<NearbyUserHolder> implements Filterable {

    private final List<NearbyUser> nearbyUsers;
    private List<NearbyUser> nearbyUsersFiltered;
    private Context context;
    private int itemResource;

    public NearbyUsersAdapter(Context context, int itemResource, List<NearbyUser> nearbyUsers) {
        this.nearbyUsers = nearbyUsers;
        this.nearbyUsersFiltered = nearbyUsers;
        this.context = context;
        this.itemResource = itemResource;
    }

    // 2. Override the onCreateViewHolder method
    @Override
    public NearbyUserHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new NearbyUserHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(NearbyUserHolder holder, int position) {

        // 5. Use position to access the correct NearbyUser object
        NearbyUser nearbyUser = this.nearbyUsersFiltered.get(position);

        // 6. Bind the nearbyUser object to the holder
        holder.bindNearbyUser(nearbyUser);
    }

    @Override
    public int getItemCount() {

        return this.nearbyUsersFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    nearbyUsersFiltered = nearbyUsers;
                } else {
                    List<NearbyUser> filteredList = new ArrayList<>();
                    for (NearbyUser row : nearbyUsers) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getSkills().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getGenres().toLowerCase().contains(charString.toLowerCase()) ||
                                row.getLookingfor().contentEquals(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    nearbyUsersFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = nearbyUsersFiltered;

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                nearbyUsersFiltered = (ArrayList<NearbyUser>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
