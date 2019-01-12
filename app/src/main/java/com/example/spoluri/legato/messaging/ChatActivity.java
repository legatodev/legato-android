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
import android.widget.ProgressBar;

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

class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private static final int DEFAULT_MSG_LENGTH_LIMIT = 1000;

    private ListView mMessageListView;
    private ChatAdapter mChatAdapter;
    private ProgressBar mProgressBar;
    private EditText mMessageEditText;
    private Button mSendButton;

    private String mUserId = AppConstants.ANONYMOUS;

    private DatabaseReference mMessagesDatabaseReference;
    private String chattingWith = AppConstants.ANONYMOUS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String participants = intent.getStringExtra("participants");
        chattingWith = intent.getStringExtra("chattingWith");

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = firebaseDatabase.getReference().child("messages").child(participants);
        // Initialize references to views
        mProgressBar = findViewById(R.id.progressBar);
        mMessageListView = findViewById(R.id.messageListView);
        mMessageEditText = findViewById(R.id.messageEditText);
        mSendButton = findViewById(R.id.sendButton);

        // Initialize message ListView and its adapter
        List<ChatCreator> messagesList = new ArrayList<>();
        mChatAdapter = new ChatAdapter(this, R.layout.item_message, messagesList);
        mMessageListView.setAdapter(mChatAdapter);

        // Initialize progress bar
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

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

        // Send button sends a message and clears the EditText
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatCreator chatMessage = new ChatCreator(mMessageEditText.getText().toString(), mUserId, null);
                mMessagesDatabaseReference.push().setValue(chatMessage);
                // Clear input box
                mMessageEditText.setText("");
            }
        });

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                ChatCreator chatMessage = dataSnapshot.getValue(ChatCreator.class);
                chatMessage.setUserId(chatMessage.getUserId().equals(mUserId)?"You":chattingWith);
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
