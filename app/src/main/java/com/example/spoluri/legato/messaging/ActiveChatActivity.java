package com.example.spoluri.legato.messaging;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.spoluri.legato.R;

import co.chatsdk.core.session.ChatSDK;

public class ActiveChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_chats);

        Fragment activeChats = ChatSDK.ui().privateThreadsFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.activeChats, activeChats).commit();

    }
}
