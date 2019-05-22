package com.example.spoluri.legato;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class AvatarActivity extends AppCompatActivity {

    private ImageView mProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        mProfilePic = findViewById(R.id.profile_image);

        GridView gridView = findViewById(R.id.avatarGridView);
        // Instance of ImageAdapter Class
        final AvatarImageAdapter avatarImageAdapter = new AvatarImageAdapter(this);
        gridView.setAdapter(avatarImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                displayProfilePic((Integer) avatarImageAdapter.getItem(position));
            }
        });
    }

    private void displayProfilePic(Integer imageId) {
            // helper method to load the profile pic in a circular imageview
            Picasso.with(AvatarActivity.this)
                    .load(imageId)
                    .fit()
                    .transform(new CircleTransform())
                    .into(mProfilePic);
    }
}
