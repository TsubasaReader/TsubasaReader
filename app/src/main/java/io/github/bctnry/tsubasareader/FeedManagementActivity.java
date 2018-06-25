package io.github.bctnry.tsubasareader;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import io.github.bctnry.tsubasareader.DAL.Generic;

public class FeedManagementActivity extends AppCompatActivity {


    private SharedPreferences sharedPreferences;
    private RecyclerView recyclerView;
    private FeedSourceAdapter feedSourceAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final FeedManagementActivity self = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_management);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_feedManagement);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.feedManagement_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.feedListFileName, MODE_PRIVATE);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_feedManagement);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout_feedManagement);

        feedSourceAdapter = new FeedSourceAdapter(Generic.getFeedSourceList(sharedPreferences));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(feedSourceAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.addOnItemTouchListener(new FeedSourceTouchListener(
                this.getBaseContext(),
                recyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(self, FeedEditActivity.class);
                        intent.putExtra(Constants.feedEditIntentFieldName, position);
                        startActivity(intent);
                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }
        ));


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedSourceAdapter.update(sharedPreferences);
                feedSourceAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_management, menu);
        return true;
    }

    // callback for the "Add Entity Source" menu option.
    public void menuAddEntitySource_onClick(MenuItem item) {
        Intent intent = new Intent(this, FeedAddActivity.class);
        startActivity(intent);
    }

    public void menuClearFeed_onClick(MenuItem item) {
        Generic.clearFeedSource(sharedPreferences);
        this.feedSourceAdapter.update(sharedPreferences);
        this.feedSourceAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Feed cleared.", Toast.LENGTH_SHORT).show();
    }
    public void menuLoadFromFile_onClick(MenuItem item) {
        Intent intent = new Intent(this, FileActivity.class);
        intent.setAction(FileActivity.ACTION_OPEN_FILE);
        intent.putExtra(FileActivity.EXTRA_STARTING_PATH, "/storage/emulated/0");
        intent.putExtra(FileActivity.EXTRA_OBJECT_TYPE, Constants.FEED_SOURCE);
        startActivity(intent);
        this.feedSourceAdapter.update(sharedPreferences);
        this.feedSourceAdapter.notifyDataSetChanged();
    }
    public void menuSaveToFile_onClick(MenuItem item) {
        Intent intent = new Intent(this, FileActivity.class);
        intent.setAction(FileActivity.ACTION_SAVE_FILE);
        intent.putExtra(FileActivity.EXTRA_STARTING_PATH, "/storage/emulated/0");
        intent.putExtra(FileActivity.EXTRA_OBJECT_TYPE, Constants.FEED_SOURCE);
        startActivity(intent);
    }
}