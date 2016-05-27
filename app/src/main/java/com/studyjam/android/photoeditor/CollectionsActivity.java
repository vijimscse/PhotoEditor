package com.studyjam.android.photoeditor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.studyjam.android.photoeditor.adapters.SavedCollectionListAdapter;
import com.studyjam.android.photoeditor.utils.Constants;
import com.studyjam.android.photoeditor.utils.FileUtility;

import java.io.File;

/**
 * Created by Vijayalakshmi on 27-05-2016.
 */
public class CollectionsActivity extends AppCompatActivity {

    private File[] mFileList = null;
    private RecyclerView mRecyclerView;
    private TextView mEmptyTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_files_list);

        initialiseViews();
        mFileList = FileUtility.getSavedCollections();
        if (mFileList == null || mFileList.length == 0) {
            mEmptyTextView.setVisibility(View.VISIBLE);
        } else {
            mEmptyTextView.setVisibility(View.GONE);
        }
        SavedCollectionListAdapter adapter = new SavedCollectionListAdapter(this, mFileList, new SavedCollectionListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(File file) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.SELECTED_FILE_PATH, file.getAbsolutePath());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(adapter);
    }

    /**
     * Initialises the views
     */
    private void initialiseViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.saved_files);
        TextView titleView = (TextView) findViewById(R.id.toolbarTitle);
        titleView.setText(R.string.saved_files);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.white));
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mEmptyTextView = (TextView) findViewById(R.id.empty_list);
    }
}
