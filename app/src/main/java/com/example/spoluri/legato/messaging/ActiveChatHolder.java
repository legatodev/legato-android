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

import org.w3c.dom.Text;

public class ActiveChatHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final ImageView activeChatPhoto;
    private final TextView activeChatUserName;
    private final TextView activeChatLastMessage;
    private final TextView activeChatLastTime;

    private ActiveChat activeChat;
    private final Context context;

    public ActiveChatHolder(Context context, View itemView) {
        super(itemView);

        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        this.activeChatPhoto = itemView.findViewById(R.id.activeChatPhotoImageView);
        this.activeChatUserName = itemView.findViewById(R.id.activeChatNameTextView);
        this.activeChatLastMessage = itemView.findViewById(R.id.activeChatMessageTextView);
        this.activeChatLastTime = itemView.findViewById(R.id.activeChatMessageTimeTextView);

        // 3. Set the "onClick" listener of the holder
        itemView.setOnClickListener(this);
    }

    public void bindActiveChat(ActiveChat activeChat) {
        // 4. Bind the data to the ViewHolder
        this.activeChat = activeChat;
        displayProfilePic(R.drawable.pic_1, this.activeChatPhoto);
        this.activeChatUserName.setText(activeChat.getUserName());
        this.activeChatLastMessage.setText(activeChat.getLastMessage());
        this.activeChatLastTime.setText(activeChat.getLastMessageTime());
    }

    private void displayProfilePic(Integer imageId, ImageView profilePic) {
        // helper method to load the profile pic in a circular imageview
        Picasso.with(this.context)
                .load(imageId)
                .fit()
                .transform(new CircleTransform())
                .into(profilePic);
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
