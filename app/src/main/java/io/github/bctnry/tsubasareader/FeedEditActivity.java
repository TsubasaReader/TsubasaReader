package io.github.bctnry.tsubasareader;


import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import io.github.bctnry.tsubasareader.DAL.Generic;
import io.github.bctnry.tsubasareader.Model.FeedSource;

public class FeedEditActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    private FeedSource feedSource;
    private EditText tfFeedSourceName, tfFeedSourceURL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_edit);
        android.support.v7.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_feedEdit);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.feedEdit_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.sharedPreferences = getSharedPreferences(Constants.feedListFileName, MODE_PRIVATE);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int feedPosition = bundle.getInt(Constants.feedEditIntentFieldName);
        feedSource = Generic.getFeedSourceByPosition(sharedPreferences, feedPosition);
        this.tfFeedSourceName = (EditText) findViewById(R.id.tfFeedSourceName_feedEdit);
        this.tfFeedSourceURL = (EditText) findViewById(R.id.tfFeedSourceURL_feedEdit);

        this.tfFeedSourceName.setText(feedSource.getSourceName());
        this.tfFeedSourceURL.setText(feedSource.getSourceURL());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_edit, menu);
        return true;
    }

    public void btnOK_onClick(View view) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        this.feedSource.setSourceName(this.tfFeedSourceName.getText().toString());
        this.feedSource.setSourceURL(this.tfFeedSourceURL.getText().toString());
        editor.putString(
                Constants.feedListFeedPrefix + this.feedSource.getID(),
                Constants.gson.toJson(this.feedSource)
        );
        editor.apply();
        finish();
    }
    public void btnCancel_onClick(View view) {
        finish();
    }
    public void menuFeedRemove_onClick(MenuItem item) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(Constants.feedListFeedPrefix + this.feedSource.getID());
        editor.apply();
        Toast.makeText(this, "Removed.", Toast.LENGTH_SHORT).show();
        finish();
    }
}
