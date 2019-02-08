package com.example.spoluri.legato;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class NearbyUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView nearbyUserPhoto;
    private final TextView nearbyUserName;
    private final TextView nearbyUserDistance;
    private final TextView nearbyUserGenres;
    private final TextView nearbyUserSkills;

    private NearbyUser nearbyUser;
    private Context context;

    public NearbyUserHolder(Context context, View itemView) {
        super(itemView);

        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        this.nearbyUserPhoto = (ImageView) itemView.findViewById(R.id.nearbyUserPhotoImageView);
        this.nearbyUserName = (TextView) itemView.findViewById(R.id.nearbyUserNameTextView);
        this.nearbyUserDistance = (TextView) itemView.findViewById(R.id.nearbyUserDistanceTextView);
        this.nearbyUserGenres = (TextView) itemView.findViewById(R.id.nearbyUserGenresTextView);
        this.nearbyUserSkills = (TextView) itemView.findViewById(R.id.nearbyUserSkillsTextView);

        // 3. Set the "onClick" listener of the holder
        itemView.setOnClickListener(this);
    }

    public void bindNearbyUser(NearbyUser nearbyUser) {
        // 4. Bind the data to the ViewHolder
        this.nearbyUser = nearbyUser;
        this.nearbyUserPhoto.setImageURI(Uri.parse(nearbyUser.getPhotourl()));
        this.nearbyUserPhoto.invalidate();
        this.nearbyUserName.setText(nearbyUser.getUsername());
        this.nearbyUserDistance.setText(nearbyUser.getDistance() + " miles away");
        this.nearbyUserGenres.setText(nearbyUser.getGenres());
        this.nearbyUserSkills.setText(nearbyUser.getSkills());
    }

    @Override
    public void onClick(View v) {
            //Open profile activity
            //Intent intent = new Intent(this.context, SkillsFragment.class);
            //intent.putExtra("nearby_user", this.nearbyUser);
            //context.startActivity(intent);
    }
}
