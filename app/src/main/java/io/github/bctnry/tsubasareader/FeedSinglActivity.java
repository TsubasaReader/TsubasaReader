package io.github.bctnry.tsubasareader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import io.github.bctnry.tsubasareader.DAL.RetrieveFeedTask;
import io.github.bctnry.tsubasareader.Model.Feed;
import io.github.bctnry.tsubasareader.Model.FeedSource;

import java.util.ArrayList;
import java.util.Collections;

public class FeedSinglActivity extends AppCompatActivity implements FeedDisplayingActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private ArrayList<Feed> feedArrayList;
    private FeedSource feedSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_singl);
        final FeedSinglActivity self = this;

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_feedSingl);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_feedSingl);
        if(feedAdapter == null) feedAdapter = new FeedAdapter();

        feedSource = (FeedSource) getIntent().getSerializableExtra(Constants.FEED_SOURCE_EXTRA);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_feedSingl));
        getSupportActionBar().setTitle(feedSource.getSourceName());
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // setting up the feed:
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                self.refreshFeed();
            }
        });
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        if(this.feedAdapter == null) feedAdapter = new FeedAdapter();
        recyclerView.setAdapter(feedAdapter);
        recyclerView.addOnItemTouchListener(new FeedSourceTouchListener(
                this.getBaseContext(),
                recyclerView,
                new ClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(self, FeedDetailActivity.class);
                        intent.putParcelableArrayListExtra(Constants.feedIntentFieldName, new ArrayList<Parcelable>(Collections.singletonList(self.feedArrayList.get(position))));
                        startActivity(intent);
                    }
                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }
        ));

        refreshFeed();
    }

    private void refreshFeed() {
        new RetrieveFeedTask(this).execute(this.feedSource);
    }

    @Override
    public void retrieveFeedNotify(FeedSource feedSource, ArrayList<Feed> feeds) {
        this.feedArrayList = feeds;
        feedAdapter.setAll(feeds);
        feedAdapter.notifyDataSetChanged();
        this.swipeRefreshLayout.setRefreshing(false);
    }
}
