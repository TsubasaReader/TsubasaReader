package io.github.bctnry.tsubasareader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.TextView;
import io.github.bctnry.tsubasareader.Model.Feed;

import java.util.ArrayList;

public class FeedDetailActivity extends AppCompatActivity {


    private SharedPreferences sharedPreference;
    private TextView tvTitle, tvSource, tvTime, tvContent;
    private String link;
    private Feed feed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_detail);
        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_feedDetail));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvTitle = (TextView)findViewById(R.id.tvTitle_feedDetail);
        tvSource = (TextView)findViewById(R.id.tvSource_feedDetail);
        tvTime = (TextView)findViewById(R.id.tvTime_feedDetail);
        tvContent = (TextView)findViewById(R.id.tvContent_feedDetail);
        sharedPreference = getSharedPreferences(Constants.favouriteFeedListFileName, MODE_PRIVATE);

        ArrayList<Feed> feedArrayList = getIntent().getParcelableArrayListExtra(Constants.feedIntentFieldName);
        Feed thisFeed = feedArrayList.get(0);
        feed = thisFeed;
        tvTitle.setText(thisFeed.getTitle());
        getSupportActionBar().setTitle(thisFeed.getTitle());
        tvSource.setText(thisFeed.getSource().getSourceName());
        tvTime.setText(thisFeed.getDate().toString());
        tvContent.setText(Html.fromHtml(thisFeed.getContent()));
        link = thisFeed.getLink();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_detail, menu);
        return true;
    }
    public void menuOpenFeedLink_onClick(MenuItem menuItem) {
        if(link.startsWith("http://") || link.startsWith("https://")) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(link));
            startActivity(intent);
        }
    }
    public void menuSaveToFavourite_onClick(MenuItem menuItem) {
        int favouriteCount = sharedPreference.getInt(Constants.feedListCountFieldName, 0);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putString(
                Constants.favouriteFeedFieldPrefix + String.valueOf(favouriteCount),
                Constants.gson.toJson(this.feed)
        );
        editor.putInt(Constants.feedListCountFieldName, favouriteCount + 1);
        editor.apply();
    }
    public void menuShare_onClick(MenuItem menuItem) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, feed.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, feed.getLink());
        shareIntent.setType("text/plain");
        startActivity(Intent.createChooser(shareIntent, "Share feed to.."));
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
