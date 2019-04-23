package com.example.spoluri.legato.messaging;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import 	androidx.recyclerview.widget.RecyclerView;

import com.example.spoluri.legato.AppConstants;
import com.example.spoluri.legato.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ActiveChatActivity extends AppCompatActivity {
    private static final String TAG = "ActiveChatActivity";

    private RecyclerView mActiveChatRecyclerView;
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
        mActiveChatRecyclerView = findViewById(R.id.activeChatRecyclerView);

        activeChatList = new ArrayList<>();
        mActiveChatAdapter = new ActiveChatAdapter(this, R.layout.item_activechat, activeChatList);
        mActiveChatRecyclerView.setAdapter(mActiveChatAdapter);

        // 4. Initialize ItemAnimator, LayoutManager and ItemDecorators
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        // 7. Set the LayoutManager
        mActiveChatRecyclerView.setLayoutManager(layoutManager);

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
                final String chattingWithUserName = dataSnapshot.getValue(String.class);
                DatabaseReference lastReference = mFirebaseDatabase.getReference().child("messages").child(participants);
                Query lastQuery = lastReference.limitToLast(1);
                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Object> chatHashMap = (Map<String, Object>) dataSnapshot.getValue();
                        Map<String, Object> chat = (Map<String, Object>) chatHashMap.get(chatHashMap.keySet().toArray()[0]);
                        ActiveChat activeChat = new ActiveChat(chattingWithUserName,
                                null,
                                participants,
                                (String)chat.get("text"),
                                convertTime(chat.get("timeStamp")));
                        //add activeChat to the list of items that activechatadapter holds.
                        activeChatList.add(activeChat);
                        mActiveChatAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Handle possible errors.
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, databaseError.toException());
            }
        });
    }

    public String convertTime(Object timeObject){
        if (timeObject != null) {
            long time = ((Number) timeObject).longValue();
            Date date = new Date(time);
            Format format = new SimpleDateFormat("MMM dd yyyy HH:mm");
            return format.format(date);
        }

        return null;
    }
}
