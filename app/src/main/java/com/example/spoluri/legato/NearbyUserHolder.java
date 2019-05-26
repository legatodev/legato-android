package com.example.spoluri.legato;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import 	androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NearbyUserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.nearbyUserPhotoImageView)
    ImageView nearbyUserPhoto;

    @BindView(R.id.nearbyUserNameTextView)
    TextView nearbyUserName;

    @BindView(R.id.nearbyUserDistanceTextView)
    TextView nearbyUserDistance;

    @BindView(R.id.nearbyUserGenresTextView)
    TextView nearbyUserGenres;

    @BindView(R.id.nearbyUserSkillsTextView)
    TextView nearbyUserSkills;

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
    }

    private void LoadImageFromWebOperations(String url) {
        new AsyncTask<String, Void, Drawable>() {

            @Override
            protected Drawable doInBackground(String... strings) {
                if (strings == null)
                    throw new NullPointerException("Parameters to the async task can never be null");

                try {
                    InputStream is = (InputStream) new URL(strings[0]).getContent();
                    return Drawable.createFromStream(is, "src name");
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Drawable drawable) {
                super.onPostExecute(drawable);
                nearbyUserPhoto.setImageDrawable(drawable);
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, url);
    }

    public void bindNearbyUser(NearbyUser nearbyUser) {

        // 4. Bind the data to the ViewHolder
        this.nearbyUser = nearbyUser;

        LoadImageFromWebOperations(nearbyUser.getPhotourl());
        this.nearbyUserPhoto.invalidate();
        this.nearbyUserName.setText(nearbyUser.getUsername());
        this.nearbyUserDistance.setText(nearbyUser.getDistance() + " miles away");
        this.nearbyUserGenres.setText(nearbyUser.getGenres());
        this.nearbyUserSkills.setText(nearbyUser.getSkills());
    }

    @Override
    public void onClick(View v) {
        //Open profile activity
        Intent intent = new Intent(this.context, UserProfileActivity.class);
        context.startActivity(intent);
    }
}
