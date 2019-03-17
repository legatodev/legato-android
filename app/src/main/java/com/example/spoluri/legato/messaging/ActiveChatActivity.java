package com.example.spoluri.legato.messaging;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;

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

public class ActiveChatActivity extends AppCompatActivity {
    private static final String TAG = "ActiveChatActivity";

    private RecyclerView mActiveChatListView;
    private ActiveChatAdapter mActiveChatAdapter;

    private FirebaseDatabase mFirebaseDatabase;
    private String mUserId = AppConstants.ANONYMOUS;
    private List<ActiveChat> activeChatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_chats);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference messagesDatabaseReference = mFirebaseDatabase.getReference().child("messages");
        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize references to views
        mActiveChatListView = findViewById(R.id.activeChatListView);

        // Initialize message ListView and its adapter
        activeChatList = new ArrayList<>();
        mActiveChatAdapter = new ActiveChatAdapter(this, R.layout.item_activechat, activeChatList);
        mActiveChatListView.setAdapter(mActiveChatAdapter);

        // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // 7. Set the LayoutManager
        mActiveChatListView.setLayoutManager(layoutManager);

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
                String chattingWithUserName = dataSnapshot.getValue(String.class);
                ActiveChat activeChat = new ActiveChat(chattingWithUserName, null, participants);
                //add activeChat to the list of items that activechatadapter holds.
                activeChatList.add(activeChat);
                mActiveChatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        });
    }
}
