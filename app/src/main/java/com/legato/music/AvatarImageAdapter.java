package com.legato.music;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

class AvatarImageAdapter extends BaseAdapter {
    private final Context mContext;

    // Keep all Images in array
    private final Integer[] mThumbIds = {
            R.drawable.electric_guitar, R.drawable.piano,
            R.drawable.electric_guitar, R.drawable.piano,
            R.drawable.electric_guitar, R.drawable.piano
    };

    // Constructor
    public AvatarImageAdapter(Context context){
        mContext = context;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setLayoutParams(new GridView.LayoutParams(GridView.AUTO_FIT,400));
        return imageView;
    }

}
