package com.legato.music.youtube;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.legato.music.R;
import com.google.api.services.youtube.model.SearchResult;
import com.squareup.picasso.Picasso;

import java.util.List;

import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;

class YtAdapter extends BaseAdapter {

    private final Activity mActivity;
    @Nullable private List<SearchResult> mVideoList;
    private final LayoutInflater mLayoutInflater;

    public YtAdapter(Activity iActivity) {
        mActivity = iActivity;
        mLayoutInflater = LayoutInflater.from(mActivity);
    }

    public void setVideoList(List<SearchResult> mVideoList) {
        this.mVideoList = mVideoList;
    }


    @Override
    public int getCount() {
        return (mVideoList==null)?0:mVideoList.size();
    }

    @Override
    public Object getItem(int i) {
        //TODO: Throw an error. Perhaps array out of bounds error.
        if (mVideoList!=null && mVideoList.size()>i)
            return mVideoList.get(i);

        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        @NonNull ViewHolder mHolder;
        if (view != null) {
            mHolder = (ViewHolder)view.getTag();
        } else {
            mHolder = new ViewHolder();
            view  = mLayoutInflater.inflate(R.layout.view_video_item, null);
            mHolder.mVideoThumbnail = view.findViewById(R.id.video_thumbnail_imv);
            mHolder.mVideoTitleTxv = view.findViewById(R.id.video_title_txv);
            mHolder.mVideoDescTxv = view.findViewById(R.id.video_desc_txv);
            view.setTag(mHolder);
        }

        // Set the data
        if (mVideoList != null) {
            SearchResult result = mVideoList.get(i);
            if (mHolder.mVideoTitleTxv != null)
                mHolder.mVideoTitleTxv.setText(result.getSnippet().getTitle());
            if (mHolder.mVideoDescTxv != null)
                mHolder.mVideoDescTxv.setText(result.getSnippet().getDescription());

            //Load images
            Picasso.with(mActivity).load(result.getSnippet().getThumbnails().getMedium().getUrl()).into(mHolder.mVideoThumbnail);
        }

        return view;
    }

    private class ViewHolder {
        @Nullable private TextView mVideoTitleTxv = null;
        @Nullable private TextView mVideoDescTxv = null;
        @Nullable private ImageView mVideoThumbnail = null;
    }
}
