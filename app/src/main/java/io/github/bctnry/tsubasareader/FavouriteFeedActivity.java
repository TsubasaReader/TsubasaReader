package io.github.bctnry.tsubasareader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import io.github.bctnry.tsubasareader.DAL.Generic;
import io.github.bctnry.tsubasareader.Model.Feed;
import io.github.bctnry.tsubasareader.Model.FeedSource;

import java.util.ArrayList;
import java.util.Collections;

public class FavouriteFeedActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private ArrayList<Feed> feedArrayList = new ArrayList<>();
    private SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_singl);
        final FavouriteFeedActivity self = this;

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout_feedSingl);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_feedSingl);
        if(feedAdapter == null) feedAdapter = new FeedAdapter();

        sharedPreference = getSharedPreferences(Constants.favouriteFeedListFileName, MODE_PRIVATE);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_feedSingl));
        getSupportActionBar().setTitle(R.string.favouriteFeed_title);
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
                        showPopupMenu(position);
                    }
                }
        ));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });

        refreshFeed();
    }

    private void showPopupMenu(final int position) {

        PopupMenu popupMenu = new PopupMenu(
                FavouriteFeedActivity.this,
                recyclerView.findViewHolderForAdapterPosition(position).itemView
        );
        popupMenu.getMenuInflater().inflate(R.menu.popup_favfeed, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.favfeed_delete: {
                        removeFavFeed(position, feedArrayList.get(position).getMultiPurposeID());
                    }
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void removeFavFeed(int position, int multiPurposeID) {
        this.feedArrayList.remove(position);
        Generic.removeFavFeed(sharedPreference, multiPurposeID);
        this.feedAdapter.setAll(this.feedArrayList);
        this.feedAdapter.notifyDataSetChanged();
    }

    private void refreshFeed() {
        this.feedArrayList.clear();
        this.feedArrayList.addAll(Generic.getFavouriteFeedList(this.sharedPreference));
        this.feedAdapter.setAll(this.feedArrayList);
        this.feedAdapter.notifyDataSetChanged();
        this.swipeRefreshLayout.setRefreshing(false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}
