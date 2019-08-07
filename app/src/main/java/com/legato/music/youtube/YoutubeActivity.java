package com.legato.music.youtube;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.legato.music.R;
import com.google.api.services.youtube.model.SearchResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YoutubeActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, ServerResponseListener {

    @BindView(R.id.yt_video_edt)
    EditText mYtVideoEdt;

    @BindView(R.id.yt_video_btn)
    Button mYtVideoBtn;

    @BindView(R.id.yt_video_lsv)
    ListView mYtVideoLsv;

    private YtAdapter mYtAdapter = null;
    private ServiceTask mYtServiceTask = null;
    private ProgressDialog mLoadingDialog = null;

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
        mYtVideoBtn.setOnClickListener(this);
        mYtVideoLsv.setOnItemClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yt_video_btn:
                final String keyWord = mYtVideoEdt.getText().toString().trim();
                if (keyWord.length() > 0) {

                    // Service to search video
                    mYtServiceTask = new ServiceTask(SEARCH_VIDEO);
                    mYtServiceTask.setServerResponseListener(this);
                    mYtServiceTask.execute(new String[]{keyWord});
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        SearchResult result = (SearchResult) mYtAdapter.getItem(i);
        Intent resultIntent = new Intent();
        resultIntent.putExtra("youtube_video", result.getId().getVideoId());
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
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

        if (mLoadingDialog != null && !mLoadingDialog.isShowing())
            mLoadingDialog = ProgressDialog.show(this, DIALOG_TITLE, dialogMsg, true, true);
    }

    @Override
    public void goBackground(Object... objects) {

    }

    @Override
    public void completedRequest(Object... objects) {
        // Dismiss the dialog
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();

        // Parse the response based upon type of request
        Integer reqCode = (Integer) objects[0];

        if(reqCode==null || reqCode == 0)
            throw new NullPointerException("Request Code's value is Invalid.");

        switch (reqCode) {
            case SEARCH_VIDEO:

                if (mYtAdapter == null) {
                    mYtAdapter = new YtAdapter(this);
                    mYtAdapter.setVideoList((List<SearchResult>) objects[1]);
                    mYtVideoLsv.setAdapter(mYtAdapter);
                } else {
                    mYtAdapter.setVideoList((List<SearchResult>) objects[1]);
                    mYtAdapter.notifyDataSetChanged();
                }

                break;
        }
    }
}
