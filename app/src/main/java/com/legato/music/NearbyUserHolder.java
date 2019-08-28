package com.legato.music;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.legato.music.R;
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
        initializeView();
    }

    private void initializeView() {
        this.nearbyUserPhoto.setImageURI("");
        this.nearbyUserName.setText("");
        this.nearbyUserGenres.setText("");
        this.nearbyUserDistance.setText("");
        this.nearbyUserDistance.setText("");
        this.nearbyUserFacebookView.setVisibility(View.INVISIBLE);
        this.nearbyUserInstagramView.setVisibility(View.INVISIBLE);
        this.nearbyUserYoutubeView.setVisibility(View.INVISIBLE);
        this.nearbyUserAvailabilityImageView.setVisibility(View.INVISIBLE);
    }

    private void setInstagramOnClick(String instagram) {
        this.nearbyUserInstagramView.setVisibility(View.VISIBLE);
        this.nearbyUserInstagramView.setOnClickListener(new View.OnClickListener() {
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
        this.nearbyUserFacebookView.setVisibility(View.VISIBLE);
        this.nearbyUserFacebookView.setOnClickListener(new View.OnClickListener() {
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
        this.nearbyUserYoutubeView.setVisibility(View.VISIBLE);
        this.nearbyUserYoutubeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = YouTubeIntents.createUserIntent(v.getContext(), youtube_channel);
                v.getContext().startActivity(intent);
            }
        });
    }

    String getTrimString(String dbString,int trimLength){
        StringBuilder trimString = new StringBuilder("");
        int strLength = dbString.length();
        String[] genre = dbString.split("\\|", -1);
        if(strLength > trimLength){
            String moreString = " ...";
            for(int i=0;i<genre.length;i++){
                int tempLength = trimString.length() + genre[i].length();
                if(tempLength>trimLength)
                    break;
                else
                    trimString.append(genre[i]+"|");
            }
            trimString.append(moreString);
            return trimString.toString();
        }
        return dbString;
    }

    protected void setViewVisibility(View view, int visibility) {
        if (view != null) view.setVisibility(visibility);
    }

    protected void setViewVisibility(View view, boolean visible) {
        setViewVisibility(view, visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void bindNearbyUser(NearbyUser nearbyUser) {
        this.nearbyUser = nearbyUser;
        initializeView();
        final int TRIM_STRING_LENGTH = 25;

        if (this.nearbyUser != null) {
            if (this.nearbyUser.getPhotourl() != null)
                this.nearbyUserPhoto.setImageURI(this.nearbyUser.getPhotourl());
            if (this.nearbyUser.getUsername() != null)
                this.nearbyUserName.setText(this.nearbyUser.getUsername());
            if (this.nearbyUser.getDistance() != null)
                this.nearbyUserDistance.setText(this.nearbyUser.getDistance() + " mi");
            if (this.nearbyUser.getGenres() != null)
                this.nearbyUserGenres.setText(getTrimString(this.nearbyUser.getGenres(),TRIM_STRING_LENGTH));
            if (this.nearbyUser.getSkills() != null)
                this.nearbyUserSkills.setText(getTrimString(this.nearbyUser.getSkills(),TRIM_STRING_LENGTH));
            if (this.nearbyUser.getInstagram() != null && !this.nearbyUser.getInstagram().isEmpty())
                this.setInstagramOnClick(this.nearbyUser.getInstagram());
            else
                this.nearbyUserInstagramView.setVisibility(View.INVISIBLE);

            if (this.nearbyUser.getFacebook() != null && !this.nearbyUser.getFacebook().isEmpty())
                this.setFacebookOnClick(this.nearbyUser.getFacebook());
            else
                this.nearbyUserFacebookView.setVisibility(View.INVISIBLE);

            if (this.nearbyUser.getYoutubeChannel() != null && !this.nearbyUser.getYoutubeChannel().isEmpty())
                this.setYoutubeOnClick(this.nearbyUser.getYoutubeChannel());
            else
                this.nearbyUserYoutubeView.setVisibility(View.INVISIBLE);

            String availability = nearbyUser.getAvailability();
            if (availability != null && nearbyUserAvailabilityImageView != null) {
                nearbyUserAvailabilityImageView.setImageResource(AvailabilityHelper.imageResourceIdForAvailability(availability));
                setViewVisibility(nearbyUserAvailabilityImageView, true);
            }else {
                setViewVisibility(nearbyUserAvailabilityImageView, false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this.context, UserProfileActivity.class);
        intent.putExtra(Keys.USER_ENTITY_ID, nearbyUser.getEntityID());
        intent.putExtra("distance", nearbyUser.getDistance());
        context.startActivity(intent);
    }
}
