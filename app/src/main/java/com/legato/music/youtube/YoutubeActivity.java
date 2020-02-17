package com.legato.music.youtube;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import com.legato.music.R;
import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.annotations.Nullable;

public class YoutubeActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, ServerResponseListener, SearchView.OnQueryTextListener {

    @BindView(R.id.yt_video_lsv)
    @Nullable ListView mYtVideoLsv;

    @Nullable private YtAdapter mYtAdapter = null;
    @Nullable private ServiceTask mYtServiceTask = null;
    @BindView(R.id.youtubeProgressBar)
    @Nullable ProgressBar mLoadingDialog = null;

    @BindView(R.id.youtube_search_view)
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        ButterKnife.bind(this);

        initializeViews();
    }

    /**
     * Method that initializes views from the activity's content layout
     */
    private void initializeViews() {
        // Setup search field
        searchView.setOnQueryTextListener(this);

        if (mYtVideoLsv != null)
            mYtVideoLsv.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (mYtAdapter != null) {
            SearchResult result = (SearchResult) mYtAdapter.getItem(i);
            Intent resultIntent = new Intent();
            resultIntent.putExtra("youtube_video", result.getId().getVideoId());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    @Override
    public void prepareRequest(Object... objects) {
        // Parse the response based upon type of request
        Integer reqCode = (Integer) objects[0];

        if(reqCode==null || reqCode == 0)
            throw new NullPointerException("Request Code's value is Invalid.");
        String dialogMsg = null;
        switch (reqCode)
        {
            case SEARCH_VIDEO:
                dialogMsg = SEARCH_VIDEO_MSG;
                break;
        }

        showProgressBar(mLoadingDialog);
    }

    private void showProgressBar(@Nullable ProgressBar progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.GONE) {
            progressBar.setVisibility(View.VISIBLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public void goBackground(Object... objects) {

    }

    @Override
    public void completedRequest(Object... objects) {
        // Dismiss the dialog
        removeProgressBar(mLoadingDialog);

        // Parse the response based upon type of request
        Integer reqCode = (Integer) objects[0];

        if(reqCode==null || reqCode == 0)
            throw new NullPointerException("Request Code's value is Invalid.");

        switch (reqCode) {
            case SEARCH_VIDEO:

                if (mYtAdapter == null) {
                    mYtAdapter = new YtAdapter(this);
                    mYtAdapter.setVideoList((List<SearchResult>) objects[1]);
                    if (mYtVideoLsv != null)
                        mYtVideoLsv.setAdapter(mYtAdapter);
                } else {
                    mYtAdapter.setVideoList((List<SearchResult>) objects[1]);
                    mYtAdapter.notifyDataSetChanged();
                }

                break;
        }
    }

    private void removeProgressBar(@Nullable ProgressBar progressBar) {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        final String keyWord = query;
        if (keyWord.length() > 0) {

            // Service to search video
            mYtServiceTask = new ServiceTask(SEARCH_VIDEO, getResources().getString(R.string.google_api_key));
            mYtServiceTask.setServerResponseListener(this);
            mYtServiceTask.execute(new String[]{keyWord});
        }

        searchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
