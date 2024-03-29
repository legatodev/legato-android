package com.legato.music.views.adapters.holders;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.android.youtube.player.YouTubeIntents;
import com.legato.music.R;
import com.legato.music.views.activity.UserProfileActivity;
import com.legato.music.models.NearbyUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.chatsdk.core.dao.Keys;
import co.chatsdk.ui.utils.AvailabilityHelper;
import io.reactivex.annotations.Nullable;

public class NearbyUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.nearbyUserPhotoImageView)
    SimpleDraweeView nearbyUserPhoto;

    @BindView(R.id.nearbyUserNameTextView)
    @Nullable TextView nearbyUserName;

    @BindView(R.id.nearbyUserDistanceTextView)
    @Nullable TextView nearbyUserDistance;

    @BindView(R.id.nearbyUserGenresTextView)
    @Nullable TextView nearbyUserGenres;

    @BindView(R.id.nearbyUserSkillsTextView)
    @Nullable TextView nearbyUserSkills;

    @BindView(R.id.nearbyUserInstagramView)
    @Nullable ImageView nearbyUserInstagramView;

    @BindView(R.id.nearbyUserFacebookView)
    @Nullable ImageView nearbyUserFacebookView;

    @BindView(R.id.nearbyUserYoutubeView)
    @Nullable ImageView nearbyUserYoutubeView;

    @BindView(R.id.nearbyUserAvailability)
    @Nullable ImageView nearbyUserAvailabilityImageView;

    private @Nullable NearbyUser nearbyUser;
    private final Context context;

    public NearbyUserHolder(Context context, View itemView) {
        super(itemView);
        this.context = context;
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        initializeView();
    }

    private void initializeView() {
        if (this.nearbyUserPhoto != null)
            this.nearbyUserPhoto.setImageURI("");
        setTextView(this.nearbyUserName, "");
        setTextView(this.nearbyUserGenres, "");
        setTextView(this.nearbyUserDistance, "");
        setImageViewVisibility(this.nearbyUserFacebookView, View.INVISIBLE);
        setImageViewVisibility(this.nearbyUserInstagramView, View.INVISIBLE);
        setImageViewVisibility(this.nearbyUserYoutubeView, View.INVISIBLE);
        setImageViewVisibility(this.nearbyUserAvailabilityImageView, View.INVISIBLE);
    }

    private void setTextView (@Nullable TextView view, String text) {
        if (view != null) { view.setText(text); }
    }

    private void setImageViewVisibility(@Nullable ImageView view, int visibility) {
        if (view != null) { view.setVisibility(visibility); }
    }

    private void setInstagramOnClick(String instagram) {
        if (this.nearbyUserInstagramView != null && this.nearbyUser != null) {
            if (!TextUtils.isEmpty(instagram)) {
                this.nearbyUserInstagramView.setVisibility(View.VISIBLE);
                this.nearbyUserInstagramView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Uri uri = Uri.parse("http://instagram.com/_u/" + instagram);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setPackage("com.instagram.android");
                        try {
                            context.startActivity(intent);
                        } catch (ActivityNotFoundException e) {
                            context.startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("http://instagram.com/" + instagram)));
                        }
                    }
                });
            }
            else
                this.nearbyUserInstagramView.setVisibility(View.INVISIBLE);
        }
    }

    private void setFacebookOnClick(String facebook) {
        if (this.nearbyUserFacebookView != null && this.nearbyUser != null) {
            if (!TextUtils.isEmpty(facebook)) {
                this.nearbyUserFacebookView.setVisibility(View.VISIBLE);
                this.nearbyUserFacebookView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        try {
                            context.getPackageManager()
                                    .getPackageInfo("com.facebook.katana", 0); //Checks if FB is even installed.
                            intent.setData(Uri.parse("fb://page/" + facebook));
                        } catch (Exception e) {
                            intent.setData(Uri.parse("https://www.facebook.com/" + facebook)); //catches and opens a url to the desired page
                        }
                        context.startActivity(intent);
                    }
                });
            }
            else
                this.nearbyUserFacebookView.setVisibility(View.INVISIBLE);
        }
    }

    private void setYoutubeOnClick(String youtube_channel) {
        if (this.nearbyUserYoutubeView != null && this.nearbyUser != null) {
            if (!TextUtils.isEmpty(youtube_channel)) {
                this.nearbyUserYoutubeView.setVisibility(View.VISIBLE);
                this.nearbyUserYoutubeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = YouTubeIntents.createUserIntent(v.getContext(), youtube_channel);
                        v.getContext().startActivity(intent);
                    }
                });
            } else
                this.nearbyUserYoutubeView.setVisibility(View.INVISIBLE);
        }
    }

    String getTrimString(String dbString,int trimLength){
        StringBuilder trimString = new StringBuilder();
        int strLength = dbString.length();
        if(strLength > trimLength){
            String moreString = "...";
            trimString.append(dbString.substring(0, trimLength-3));
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
        final int TRIM_NAME_LENGTH = 42;

        if (this.nearbyUser != null) {
            if (!TextUtils.isEmpty(this.nearbyUser.getAvatarUrl())) {
                this.nearbyUserPhoto.setImageURI(this.nearbyUser.getAvatarUrl());
            } else {
                ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
                int color = generator.getColor(nearbyUser.getEmail());

                TextDrawable drawable = TextDrawable.builder()
                        .buildRoundRect(nearbyUser.getUsername().substring(0,1), color, 30);

                this.nearbyUserPhoto.setImageDrawable(drawable);
            }

            if (this.nearbyUser.getUsername() != null)
                setTextView(this.nearbyUserName, getTrimString(this.nearbyUser.getUsername(), TRIM_NAME_LENGTH));
            if (this.nearbyUser.getDistance() != null)
               setTextView(this.nearbyUserDistance, this.nearbyUser.getDistance() + " mi");
            if (this.nearbyUser.getGenres() != null)
                setTextView(this.nearbyUserGenres, getTrimString(this.nearbyUser.getGenres(),TRIM_STRING_LENGTH));
            if (this.nearbyUser.getSkills() != null)
                setTextView(this.nearbyUserSkills, getTrimString(this.nearbyUser.getSkills(),TRIM_STRING_LENGTH));

            this.setInstagramOnClick(this.nearbyUser.getInstagram());
            this.setFacebookOnClick(this.nearbyUser.getFacebookPageId());
            this.setYoutubeOnClick(this.nearbyUser.getYoutubeChannel());

            if (nearbyUserAvailabilityImageView != null) {
                String availability = nearbyUser.getAvailability();
                if (availability != null) {
                    nearbyUserAvailabilityImageView.setImageResource(AvailabilityHelper.imageResourceIdForAvailability(availability));
                    setViewVisibility(nearbyUserAvailabilityImageView, true);
                } else {
                    setViewVisibility(nearbyUserAvailabilityImageView, false);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (nearbyUser != null) {
            Intent intent = new Intent(this.context, UserProfileActivity.class);
            intent.putExtra(Keys.PushKeyUserEntityID, nearbyUser.getEntityID());
            intent.putExtra(com.legato.music.utils.Keys.distance, nearbyUser.getDistance());
            context.startActivity(intent);
        }
    }
}
