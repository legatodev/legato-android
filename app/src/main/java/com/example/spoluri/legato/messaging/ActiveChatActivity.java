package com.example.spoluri.legato.messaging;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.spoluri.legato.AppConstants;
import com.example.spoluri.legato.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ActiveChatActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
    private static final String TAG = "ActiveChatActivity";

    private ListView mActiveChatListView;
    private ActiveChatAdapter mActiveChatAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private String mUserId = AppConstants.ANONYMOUS;
    private String mChattingWithUserName = AppConstants.ANONYMOUS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_chats);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference messagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        // Initialize references to views
        mActiveChatListView = findViewById(R.id.activeChatListView);
        mActiveChatListView.setOnItemClickListener(this);

        // Initialize message ListView and its adapter
        List<ActiveChatCreator> activeChatList = new ArrayList<>();
        mActiveChatAdapter = new ActiveChatAdapter(this, R.layout.item_message, activeChatList);
        mActiveChatListView.setAdapter(mActiveChatAdapter);

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final String participants = dataSnapshot.getKey();
                if (participants.contains(mUserId)) {
                    SetChattingWithUserName(participants);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        };
        messagesDatabaseReference.addChildEventListener(childEventListener);
    }

    private void SetChattingWithUserName(final String participants) {
        String[] users = participants.split("_");
        String chattingWith = users[0].equals(mUserId)?users[1]:users[0];
        DatabaseReference reference = mFirebaseDatabase.getReference().child("userprofiledata").child(chattingWith).child("username");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mChattingWithUserName = dataSnapshot.getValue(String.class);
                ActiveChatCreator activeChat = new ActiveChatCreator(mChattingWithUserName, null, participants);
                mActiveChatAdapter.add(activeChat);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ActiveChatCreator chatCreator = mActiveChatAdapter.getItem(i);
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("participants", chatCreator.getParticipants());
        intent.putExtra("chattingWith", mChattingWithUserName);
        startActivity(intent);
    }
}
