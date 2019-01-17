package com.example.spoluri.legato.messaging;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v7.widget.RecyclerView;

import com.example.spoluri.legato.R;

import java.util.List;

class ActiveChatAdapter extends RecyclerView.Adapter<ActiveChatHolder> {
    private final List<ActiveChat> activeChats;
    private Context context;
    private int itemResource;

    public ActiveChatAdapter(Context context, int itemResource, List<ActiveChat> activeChats) {

        this.activeChats = activeChats;
        this.context = context;
        this.itemResource = itemResource;
    }

    // 2. Override the onCreateViewHolder method
    @Override
    public ActiveChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(this.itemResource, parent, false);

        return new ActiveChatHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(ActiveChatHolder holder, int position) {

        // 5. Use position to access the correct Bakery object
        ActiveChat activeChat = this.activeChats.get(position);

        // 6. Bind the bakery object to the holder
        holder.bindActiveChat(activeChat);
    }

    @Override
    public int getItemCount() {

        return this.activeChats.size();
    }
}
