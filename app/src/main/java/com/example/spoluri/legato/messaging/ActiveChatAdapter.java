package com.example.spoluri.legato.messaging;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spoluri.legato.R;

import java.util.List;

public class ActiveChatAdapter extends ArrayAdapter<ActiveChatCreator> {
    public ActiveChatAdapter(Context context, int resource, List<ActiveChatCreator> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_activechat, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.activeChatPhotoImageView);
        TextView authorTextView = convertView.findViewById(R.id.activeChatNameTextView);

        ActiveChatCreator activeChat = getItem(position);

        boolean isPhoto = activeChat.getPhotoUrl() != null;
        if (isPhoto) {
            photoImageView.setVisibility(View.VISIBLE);
        } else {
            photoImageView.setVisibility(View.GONE);
        }
        authorTextView.setText(activeChat.getUserId());

        return convertView;
    }
}
