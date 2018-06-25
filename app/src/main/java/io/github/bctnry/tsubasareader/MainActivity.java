package io.github.bctnry.tsubasareader;

import android.app.PendingIntent;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.constraint.solver.Cache;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.bctnry.tsubasareader.DAL.Generic;
import io.github.bctnry.tsubasareader.DAL.RetrieveFeedTask;
import io.github.bctnry.tsubasareader.Model.Feed;
import io.github.bctnry.tsubasareader.Model.FeedSource;
import io.github.bctnry.tsubasareader.Model.Utilities;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MainActivity
        extends AppCompatActivity
        implements
            NavigationView.OnNavigationItemSelectedListener,
            FeedDisplayingActivity
{

    public static final String FEED_RETRIEVE_RESULT = "io.github.bctnry.tsubasaserver.FEED_RETRIEVE_RESULT";


    private boolean mCacheServiceBound;

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FeedAdapter feedAdapter;
    private SharedPreferences sharedPreferences;
    private ArrayList<Feed> feedArrayList = new ArrayList<>();
    private Set<FeedSource> pendingFeedSource = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final MainActivity self = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences(Constants.feedListFileName, MODE_PRIVATE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
                Intent intent = new Intent(self, FeedAddActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        // setting up the feed:
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_main);
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout_main);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                self.initFeedSource();
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



        initFeedSource();
        initAdapter();
        if(!pendingFeedSource.isEmpty()) {
            swipeRefreshLayout.setRefreshing(true);
            refreshFeed();
        }
    }

    private void initFeedSource() {
        if(this.pendingFeedSource == null) this.pendingFeedSource = new HashSet<>();
        this.pendingFeedSource.clear();
        this.pendingFeedSource.addAll(Generic.getFeedSourceList(sharedPreferences));
    }
    private void initAdapter() {
        this.feedArrayList.clear();
        this.feedAdapter.setAll(feedArrayList);
        this.feedAdapter.notifyDataSetChanged();
    }
    private void refreshFeed() {
        for(final FeedSource source : Generic.getFeedSourceList(sharedPreferences)) {
            Toast.makeText(this, "Fetching " + source.getSourceName(), Toast.LENGTH_SHORT).show();
            retrieveFeed(source);
        }
    }

    private void setFeed() {
        this.feedAdapter.setAll(this.feedArrayList);
        this.feedAdapter.notifyDataSetChanged();
    }
    private void retrieveFeed(FeedSource feedSource) {
        new RetrieveFeedTask(this).execute(feedSource);
    }

    public void retrieveFeedNotify(FeedSource feedSource, ArrayList<Feed> feeds) {
        if(feeds == null) {
            Toast.makeText(this, "Failed to fetch feed from " + feedSource.getSourceName(), Toast.LENGTH_SHORT).show();
            return ;
        }
        this.feedArrayList.addAll(feeds);
        for(FeedSource source : this.pendingFeedSource) {
            if(source.getID() == feedSource.getID()) {
                this.pendingFeedSource.remove(source);
                break;
            }
        }
        if(this.pendingFeedSource.isEmpty()) {
            this.swipeRefreshLayout.setRefreshing(false);
            sortFeed();
            setFeed();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void sortFeed() {
        Feed[] feeds = this.feedArrayList.toArray(new Feed[]{});
        Arrays.sort(feeds, new Comparator<Feed>() {
            @Override
            public int compare(Feed feed, Feed t1) {
                return 1 - feed.getDate().compareTo(t1.getDate());
            }
        });
        ArrayList<Feed> result = new ArrayList<>(Arrays.asList(feeds));
        this.feedArrayList = result;
        this.feedAdapter.setAll(result);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_about) {
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_feed_management) {
            Intent intent = new Intent(this, FeedManagementActivity.class);
            startActivity(intent);
        } // else if (id == R.id.nav_settings) {
            // the activity is deprecated.
            // Intent intent = new Intent(this, SettingsActivity.class);
            // startActivity(intent);
        // }
        else if (id == R.id.nav_singlefeed) {
            Intent intent = new Intent(this, FeedListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_favfeed) {
            Intent intent = new Intent(this, FavouriteFeedActivity.class);
            startActivity(intent);
        } if (id == R.id.nav_home) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
