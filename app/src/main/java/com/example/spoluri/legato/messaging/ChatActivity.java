package com.example.spoluri.legato.messaging;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.spoluri.legato.AppConstants;
import com.example.spoluri.legato.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUserId = AppConstants.ANONYMOUS;

    private DatabaseReference mMessagesDatabaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // Initialize references to views
        mMessageListView = findViewById(R.id.messageListView);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);

        // Enable Send button when there's text to send
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setEnabled(true);
                } else {
                    mSendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        mMessageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});

        Intent intent = getIntent();
        final String participants = intent.getStringExtra("participants");
        final String chattingWith = intent.getStringExtra("chattingWith");

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

        if (participants != null) {
            mMessagesDatabaseReference = firebaseDatabase.getReference().child("messages").child(participants);

            // Send button sends a message and clears the EditText
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Chat chatMessage = new Chat(mMessageEditText.getText().toString(), mUserId, null);
                    mMessagesDatabaseReference.push().setValue(chatMessage);
                    // Clear input box
                    mMessageEditText.setText("");
                }
            });

            // Initialize message ListView and its adapter
            List<Chat> messagesList = new ArrayList<>();
            final ChatAdapter mChatAdapter = new ChatAdapter(this, R.layout.item_message, messagesList);
            mMessageListView.setAdapter(mChatAdapter);

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Chat chatMessage = dataSnapshot.getValue(Chat.class);
                    chatMessage.setUserName(chatMessage.getUserName().equals(mUserId) ? "You" : chattingWith);
                    mChatAdapter.add(chatMessage);
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
            mMessagesDatabaseReference.addChildEventListener(childEventListener);
        }
    }

}
