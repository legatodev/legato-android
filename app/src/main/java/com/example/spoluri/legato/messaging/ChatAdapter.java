package com.example.spoluri.legato.messaging;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.spoluri.legato.AvatarActivity;
import com.example.spoluri.legato.CircleTransform;
import com.example.spoluri.legato.R;
import com.squareup.picasso.Picasso;

import java.util.List;

class ChatAdapter extends ArrayAdapter<Chat> {
    private Context context;

    public ChatAdapter(Context context, int resource, List<Chat> objects) {
        super(context, resource, objects);
        this.context = context;
    }

    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        Chat message = getItem(position);
        if (message != null) {
            if (convertView == null) {
                if (message.getUserName() == "You") {
                    convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.my_message, parent, false);
                } else {
                    convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.others_message, parent, false);
                    ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
                    displayProfilePic(R.drawable.pic_1, photoImageView);
                    TextView authorTextView = convertView.findViewById(R.id.nameTextView);
                    authorTextView.setText(message.getUserName());
                }
            }

            TextView messageTextView = convertView.findViewById(R.id.messageTextView);
            messageTextView.setText(message.getText());
        }

        return convertView;
    }

    private void displayProfilePic(Integer imageId, ImageView profilePic) {
        // helper method to load the profile pic in a circular imageview
        Picasso.with(this.context)
                .load(imageId)
                .fit()
                .transform(new CircleTransform())
                .into(profilePic);
    }
}
