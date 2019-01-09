package com.example.spoluri.legato;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NearbyUsersAdapter extends ArrayAdapter<NearbyUsersCreator> {
    public NearbyUsersAdapter(Context context, int resource, List<NearbyUsersCreator> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_nearbyuser, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.nearbyUserPhotoImageView);
        TextView nearbyUserNameTextView = convertView.findViewById(R.id.nearbyUserNameTextView);
        TextView nearbyUserDistanceTextView = convertView.findViewById(R.id.nearbyUserDistanceTextView);

        NearbyUsersCreator nearbyUser = getItem(position);

        boolean isPhoto = nearbyUser.getPhotoUrl() != null;
        if (isPhoto) {
            photoImageView.setVisibility(View.VISIBLE);
        } else {
            photoImageView.setVisibility(View.GONE);
        }
        nearbyUserNameTextView.setText(nearbyUser.getUserName());
        nearbyUserDistanceTextView.setText(nearbyUser.getDistance());

        return convertView;
    }
}
