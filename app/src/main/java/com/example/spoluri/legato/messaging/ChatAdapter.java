package com.example.spoluri.legato.messaging;

import android.content.Context;

import androidx.annotation.NonNull;
import 	androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spoluri.legato.R;

import java.util.List;

class ChatAdapter extends RecyclerView.Adapter<ChatHolder> {
    private final List<Chat> chats;
    private final Context context;

    public ChatAdapter(Context context, List<Chat> chats) {
        this.context = context;
        this.chats = chats;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.chats.get(position).getUserName().equals("You"))
            return R.layout.my_message;
        else
            return R.layout.others_message;
    }

    // 2. Override the onCreateViewHolder method
    @NonNull
    @Override
    public ChatHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 3. Inflate the view and return the new ViewHolder
        View view = LayoutInflater.from(parent.getContext())
                .inflate(viewType, parent, false);

        return new ChatHolder(this.context, view);
    }

    // 4. Override the onBindViewHolder method
    @Override
    public void onBindViewHolder(@NonNull ChatHolder holder, int position) {

        // 5. Use position to access the correct Bakery object
        Chat chat = this.chats.get(position);

        // 6. Bind the bakery object to the holder
        holder.bindChat(chat);
    }

    @Override
    public int getItemCount() {
        return this.chats.size();
    }
}
