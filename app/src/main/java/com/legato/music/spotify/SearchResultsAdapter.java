package com.legato.music.spotify;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Joiner;
import com.legato.music.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Track;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private final List<Track> mItems = new ArrayList<>();
    private final Context mContext;
    private final TrackSelectedListener mListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final TextView title;
        public final TextView subtitle;
        public final ImageView image;
        public final Button addTrackButton;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.entity_title);
            subtitle = (TextView) itemView.findViewById(R.id.entity_subtitle);
            image = (ImageView) itemView.findViewById(R.id.entity_image);
            addTrackButton = (Button) itemView.findViewById(R.id.add_track_button);
            addTrackButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onTrackSelected(v, mItems.get(getAdapterPosition()));
                }
            });
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notifyItemChanged(getLayoutPosition());
            mListener.onTrackPlay(v, mItems.get(getAdapterPosition()));
        }
    }

    public interface TrackSelectedListener {
        void onTrackSelected(View itemView, Track item);
        void onTrackPlay(View itemView, Track item);
    }

    public SearchResultsAdapter(Context context, TrackSelectedListener listener) {
        mContext = context;
        mListener = listener;
    }

    public void clearData() {
        mItems.clear();
    }

    public void addData(List<Track> items) {
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spotify_search, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Track item = mItems.get(position);

        holder.title.setText(item.name);

        List<String> names = new ArrayList<>();
        for (ArtistSimple i : item.artists) {
            names.add(i.name);
        }
        Joiner joiner = Joiner.on(", ");
        holder.subtitle.setText(joiner.join(names));

        Image image = item.album.images.get(0);
        if (image != null) {
            Picasso.with(mContext).load(image.url).into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }
}
