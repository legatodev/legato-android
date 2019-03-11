package com.example.spoluri.legato;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

public class AvatarActivity extends AppCompatActivity {

    private ImageView mProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);

        mProfilePic = (ImageView) findViewById(R.id.profile_image);

        GridView gridView = (GridView) findViewById(R.id.avatarGridView);
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
            Transformation transformation = new RoundedTransformationBuilder()
                    .cornerRadiusDp(1)
                    .oval(true)
                    .build();
            Picasso.with(AvatarActivity.this)
                    .load(imageId)
                    .transform(transformation)
                    .into(mProfilePic);
    }
}
