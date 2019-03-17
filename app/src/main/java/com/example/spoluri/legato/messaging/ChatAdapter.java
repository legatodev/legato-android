package com.example.spoluri.legato.messaging;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spoluri.legato.R;

import java.util.List;

class ChatAdapter extends ArrayAdapter<Chat> {
    public ChatAdapter(Context context, int resource, List<Chat> objects) {
        super(context, resource, objects);
    }

    @Override
    @NonNull
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_message, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        TextView messageTextView = convertView.findViewById(R.id.messageTextView);
        TextView authorTextView = convertView.findViewById(R.id.nameTextView);

        photoImageView.setImageResource(R.drawable.pic_1);

        Chat message = getItem(position);
        if (message != null) {
            messageTextView.setText(message.getText());
            authorTextView.setText(message.getUserName());
        }

        return convertView;
    }
}
