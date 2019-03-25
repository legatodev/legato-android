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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ChatHolder extends RecyclerView.ViewHolder {

    private final ImageView chatPhoto;
    private final TextView chatUserName;
    private final TextView chatMessage;
    private final TextView chatTime;

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
        this.chatTime = itemView.findViewById(R.id.timeTextView);
    }

    public void bindChat(Chat chat) {
        // 4. Bind the data to the ViewHolder
        this.chat = chat;
        if (this.chatPhoto != null)
            displayProfilePic(R.drawable.pic_1, this.chatPhoto);
        if (this.chatUserName != null)
            this.chatUserName.setText(chat.getUserName());
        this.chatMessage.setText(chat.getText());

        if (chat.getTimeStamp() != null) {
            String time = convertTime(chat.getTimeStamp());
            this.chatTime.setText(time);
        }
    }

    public String convertTime(Object timeObject){
        long time = ((Number) timeObject).longValue();
        Date date = new Date(time);
        Format format = new SimpleDateFormat("MMM dd yyyy HH:mm");
        return format.format(date);
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
