package com.example.spoluri.legato.registration.solo;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

class SkillsAdapter extends RecyclerView.Adapter<SkillsHolder>{

    private final List<Skills> skills;
    private final Context context;
    private final int itemResource;

    public SkillsAdapter(Context context, int itemResource, List<Skills> skills) {
        this.skills = skills;
        this.context = context;
        this.itemResource = itemResource;
    }

    // 2. Override the onCreateViewHolder method
    @NonNull
    @Override
    public SkillsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);
        return new SkillsHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull SkillsHolder holder, int position) {

        // 5. Use position to access the correct NearbyUser object
        Skills skill = this.skills.get(position);

        // 6. Bind the nearbyUser object to the holder
        holder.bindSkill(skill);
    }

    @Override
    public int getItemCount() {

        return this.skills.size();
    }
}
