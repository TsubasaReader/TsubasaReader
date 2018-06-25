package io.github.bctnry.tsubasareader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.MenuItem;
import android.view.View;
import io.github.bctnry.tsubasareader.DAL.Generic;
import io.github.bctnry.tsubasareader.Model.Utilities;

public class FeedListActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FeedSourceAdapter feedSourceAdapter;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_list);

        final FeedListActivity self = this;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_feedList);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.feedList_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sharedPreferences = getSharedPreferences(Constants.feedListFileName, MODE_PRIVATE);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout_feedList);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_feedList);
        if(feedSourceAdapter == null) feedSourceAdapter = new FeedSourceAdapter(Generic.getFeedSourceList(sharedPreferences));
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(feedSourceAdapter);
        recyclerView.addOnItemTouchListener(new FeedSourceTouchListener(
                this.getBaseContext(),
                recyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(self, FeedSinglActivity.class);
                        intent.putExtra(Constants.FEED_SOURCE_EXTRA, Generic.getFeedSourceByPosition(sharedPreferences, position));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                this.finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
