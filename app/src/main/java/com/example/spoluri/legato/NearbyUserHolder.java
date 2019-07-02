package com.example.spoluri.legato;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import 	androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.ui.utils.AvailabilityHelper;

public class NearbyUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.nearbyUserPhotoImageView)
    SimpleDraweeView nearbyUserPhoto;

    @BindView(R.id.nearbyUserNameTextView)
    TextView nearbyUserName;

    @BindView(R.id.nearbyUserDistanceTextView)
    TextView nearbyUserDistance;

    @BindView(R.id.nearbyUserGenresTextView)
    TextView nearbyUserGenres;

    @BindView(R.id.nearbyUserSkillsTextView)
    TextView nearbyUserSkills;

    @BindView(R.id.nearbyUserInstagramView)
    ImageView nearbyUserInstagram;

    @BindView(R.id.nearbyUserFacebookView)
    ImageView nearbyUserFacebook;

    @BindView(R.id.nearbyUserYoutubeView)
    ImageView nearbyUserYoutube;

    @BindView(R.id.nearbyUserAvailability)
    ImageView nearbyUserAvailabilityImageView;

    private NearbyUser nearbyUser;
    private final Context context;

    public NearbyUserHolder(Context context, View itemView) {
        super(itemView);
        // 1. Set the context
        this.context = context;

        // 2. Set up the UI widgets of the holder
        ButterKnife.bind(this, itemView);

        // 3. Set the "onClick" listener of the holder
        itemView.setOnClickListener(this);

        setSocialMediaOnClick();
    }

    private void setSocialMediaOnClick() {
        setInstagramOnClick();
        setFacebookOnClick();
        setYoutubeOnClick();
    }

    private void setInstagramOnClick() {
        nearbyUserInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = nearbyUser.getEntityID();
                Uri uri = Uri.parse("http://instagram.com/_u/oliviaculpo");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.instagram.android");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/oliviaculpo")));
                }
            }
        });
    }

    private void setFacebookOnClick() {
        nearbyUserFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = nearbyUser.getEntityID();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    context.getPackageManager()
                            .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                    intent.setData(Uri.parse("fb://profile/10157324862933120"));
                } catch (Exception e) {
                   intent.setData(Uri.parse("https://www.facebook.com/icemansar")); //catches and opens a url to the desired page
                }
                context.startActivity(intent);
            }
        });
    }

    private void setYoutubeOnClick() {
        nearbyUserYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = nearbyUser.getEntityID();
            }
        });
    }

    public void bindNearbyUser(NearbyUser nearbyUser) {

        // 4. Bind the data to the ViewHolder
        this.nearbyUser = nearbyUser;

        if (nearbyUser != null) {
            this.nearbyUserPhoto.setImageURI(nearbyUser.getPhotourl());
            this.nearbyUserName.setText(nearbyUser.getUsername());
            this.nearbyUserDistance.setText(nearbyUser.getDistance() + " mi");
            this.nearbyUserGenres.setText(nearbyUser.getGenres());
            this.nearbyUserSkills.setText(nearbyUser.getSkills());

            String availability = nearbyUser.getAvailability();
            // Availability
            if (availability != null && nearbyUserAvailabilityImageView != null) {
                nearbyUserAvailabilityImageView.setImageResource(AvailabilityHelper.imageResourceIdForAvailability(availability));
            }
        }
    }

    @Override
    public void onClick(View v) {
        //Open profile activity
        Intent intent = new Intent(this.context, UserProfileActivity.class);
        intent.putExtra(Keys.USER_ENTITY_ID, nearbyUser.getEntityID());
        context.startActivity(intent);
    }
}
