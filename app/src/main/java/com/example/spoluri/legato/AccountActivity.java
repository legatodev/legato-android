package com.example.spoluri.legato;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spoluri.legato.authentication.LoginActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class AccountActivity extends AppCompatActivity {
    public static final String ANONYMOUS = "anonymous";

    TextView id;
    TextView infoLabel;
    TextView info;

    private String mUserId = ANONYMOUS;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        id = (TextView) findViewById(R.id.id);
        infoLabel = (TextView) findViewById(R.id.info_label);
        info = (TextView) findViewById(R.id.info);

        mUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("userprofiledata");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onLogout(View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            launchLoginActivity();
                        }
                    }
                });
    }

    public void onYoutube(View view) {
        launchYoutubeActivity();
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void launchYoutubeActivity() {
        Intent intent = new Intent(this, YoutubeActivity.class);
        startActivityForResult(intent, RequestCodes.YOUTUBE_VIDEO_FLOW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RequestCodes.YOUTUBE_VIDEO_FLOW && resultCode == RESULT_OK && data != null) {
            Map<String, Object> youtube = new HashMap<String, Object>();
            // put() method
            youtube.put("youtube_video", data.getStringExtra("youtube_video"));
            mMessagesDatabaseReference.child(mUserId).updateChildren(youtube);
        }
    }

    public void onMessengerLaunch(View view) {
        Intent intent = new Intent(this, MessengerActivity.class);
        startActivity(intent);
    }
}
