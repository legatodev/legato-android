package com.example.spoluri.legato;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.youtube.player.YouTubeIntents;

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
    ImageView nearbyUserInstagramView;

    @BindView(R.id.nearbyUserFacebookView)
    ImageView nearbyUserFacebookView;

    @BindView(R.id.nearbyUserYoutubeView)
    ImageView nearbyUserYoutubeView;

    @BindView(R.id.nearbyUserAvailability)
    ImageView nearbyUserAvailabilityImageView;

    private NearbyUser nearbyUser;
    private final Context context;

    public NearbyUserHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);

        nearbyUserFacebookView.setVisibility(View.INVISIBLE);
        nearbyUserInstagramView.setVisibility(View.INVISIBLE);
        nearbyUserYoutubeView.setVisibility(View.INVISIBLE);
    }

    private void setInstagramOnClick(String instagram) {
        nearbyUserInstagramView.setVisibility(View.VISIBLE);
        nearbyUserInstagramView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = nearbyUser.getEntityID();
                Uri uri = Uri.parse("http://instagram.com/_u/"+instagram);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.instagram.android");
                try {
                    context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://instagram.com/"+instagram)));
                }
            }
        });
    }

    private void setFacebookOnClick(String facebook) {
        nearbyUserFacebookView.setVisibility(View.VISIBLE);
        nearbyUserFacebookView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = nearbyUser.getEntityID();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                try {
                    context.getPackageManager()
                            .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                    intent.setData(Uri.parse("fb://page/"+facebook));
                } catch (Exception e) {
                   intent.setData(Uri.parse("https://www.facebook.com/"+facebook)); //catches and opens a url to the desired page
                }
                context.startActivity(intent);
            }
        });
    }

    private void setYoutubeOnClick(String youtube_channel) {
        nearbyUserYoutubeView.setVisibility(View.VISIBLE);
        nearbyUserYoutubeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = YouTubeIntents.createUserIntent(v.getContext(), youtube_channel);
                v.getContext().startActivity(intent);
            }
        });
    }

    public void bindNearbyUser(NearbyUser nearbyUser) {
        this.nearbyUser = nearbyUser;

        if (nearbyUser != null) {
            this.nearbyUserPhoto.setImageURI(nearbyUser.getPhotourl());
            this.nearbyUserName.setText(nearbyUser.getUsername());
            this.nearbyUserDistance.setText(nearbyUser.getDistance() + " mi");
            this.nearbyUserGenres.setText(nearbyUser.getGenres());
            this.nearbyUserSkills.setText(nearbyUser.getSkills());
            if (nearbyUser.getInstagram() != null && !nearbyUser.getInstagram().isEmpty())
                this.setInstagramOnClick(nearbyUser.getInstagram());
            if (nearbyUser.getFacebook() != null && !nearbyUser.getFacebook().isEmpty())
                this.setFacebookOnClick(nearbyUser.getFacebook());
            if (nearbyUser.getYoutubeChannel() != null && !nearbyUser.getYoutubeChannel().isEmpty())
                this.setYoutubeOnClick(nearbyUser.getYoutubeChannel());

            String availability = nearbyUser.getAvailability();
            if (availability != null && nearbyUserAvailabilityImageView != null) {
                nearbyUserAvailabilityImageView.setImageResource(AvailabilityHelper.imageResourceIdForAvailability(availability));
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.context, UserProfileActivity.class);
        intent.putExtra(Keys.USER_ENTITY_ID, nearbyUser.getEntityID());
        context.startActivity(intent);
    }
}
