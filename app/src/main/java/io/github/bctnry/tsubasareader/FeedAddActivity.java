package io.github.bctnry.tsubasareader;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import io.github.bctnry.tsubasareader.Model.FeedSource;

public class FeedAddActivity extends AppCompatActivity {

    EditText tfFeedAddr, tfFeedName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_add);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_feedAdd);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.feedAdd_title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.tfFeedAddr = (EditText)findViewById(R.id.tfFeedURL_feedAdd);
        this.tfFeedName = (EditText)findViewById(R.id.tfFeedName_feedAdd);
    }

    // callback for the "Cancel" button.
    public void btnCancel_onClick(View view) {
        this.finish();
    }

    // callback for the "OK" button.
    public void btnOK_onClick(View view) {
        SharedPreferences sharedPreferences = getSharedPreferences(Constants.feedListFileName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentCount = sharedPreferences.getInt(Constants.feedListCountFieldName, 0);
        editor.putString(
                Constants.feedListFeedPrefix + String.valueOf(currentCount),
                Constants.gson.toJson(
                        new FeedSource(
                                currentCount,
                                this.tfFeedName.getText().toString(),
                                this.tfFeedAddr.getText().toString()
                        )
                )
        );
        editor.putInt(
                Constants.feedListCountFieldName,
                currentCount + 1
        );
        editor.apply();
        this.finish();
    }
}