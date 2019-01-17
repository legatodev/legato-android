package com.example.spoluri.legato.messaging;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spoluri.legato.R;

public class ActiveChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView activeChatPhoto;
    private final TextView activeChatUserName;

    private ActiveChat activeChat;
    private Context context;

    public ActiveChatHolder(Context context, View itemView) {
        super(itemView);

        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        this.activeChatPhoto = (ImageView) itemView.findViewById(R.id.activeChatPhotoImageView);
        this.activeChatUserName = (TextView) itemView.findViewById(R.id.activeChatNameTextView);

        // 3. Set the "onClick" listener of the holder
        itemView.setOnClickListener(this);
    }

    public void bindActiveChat(ActiveChat activeChat) {
        // 4. Bind the data to the ViewHolder
        this.activeChat = activeChat;
        if (activeChat.getPhotoUrl() != null) {
            this.activeChatPhoto.setImageURI(Uri.parse(activeChat.getPhotoUrl()));
        }
        this.activeChatUserName.setText(activeChat.getUserName());
    }

    @Override
    public void onClick(View v) {
        //Open profile activity
        Intent intent = new Intent(this.context, ChatActivity.class);
        intent.putExtra("participants", this.activeChat.getParticipants());
        intent.putExtra("chattingWith", this.activeChat.getUserName());
        context.startActivity(intent);
    }
}
