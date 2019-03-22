package com.example.spoluri.legato.messaging;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spoluri.legato.CircleTransform;
import com.example.spoluri.legato.R;
import com.squareup.picasso.Picasso;

public class ChatHolder extends RecyclerView.ViewHolder {

    private final ImageView chatPhoto;
    private final TextView chatUserName;
    private final TextView chatMessage;

    private Chat chat;
    private final Context context;

    public ChatHolder(Context context, View itemView) {
        super(itemView);

        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        this.chatPhoto = itemView.findViewById(R.id.photoImageView);
        this.chatUserName = itemView.findViewById(R.id.nameTextView);
        this.chatMessage = itemView.findViewById(R.id.messageTextView);
    }

    public void bindChat(Chat chat) {
        // 4. Bind the data to the ViewHolder
        this.chat = chat;
        if (this.chatPhoto != null)
            displayProfilePic(R.drawable.pic_1, this.chatPhoto);
        if (this.chatUserName != null)
            this.chatUserName.setText(chat.getUserName());
        this.chatMessage.setText(chat.getText());
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
