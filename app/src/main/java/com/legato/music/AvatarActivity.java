package com.legato.music;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class AvatarActivity extends AppCompatActivity {

    private ImageView mProfilePic;
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        mProfilePic = findViewById(R.id.profile_image);

        mGridView = findViewById(R.id.avatarGridView);
        // Instance of ImageAdapter Class
        final AvatarImageAdapter avatarImageAdapter = new AvatarImageAdapter(this);
        mGridView.setAdapter(avatarImageAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                displayProfilePic((Integer) avatarImageAdapter.getItem(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void displayProfilePic(Integer imageId) {
            // helper method to load the profile pic in a circular imageview
            Picasso.with(AvatarActivity.this)
                    .load(imageId)
                    .fit()
                    .into(mProfilePic);
    }

}
