package io.github.bctnry.tsubasareader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.google.gson.reflect.TypeToken;
import io.github.bctnry.tsubasareader.DAL.Generic;
import io.github.bctnry.tsubasareader.Model.FeedSource;
import io.github.bctnry.tsubasareader.Model.Utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class FileActivity extends AppCompatActivity {

    public static String ACTION_OPEN_FILE = "io.github.bctnry.tsubasareader.action.OPEN_FILE";
    public static String ACTION_SAVE_FILE = "io.github.bctnry.tsubasareader.action.SAVE_FILE";
    public static String EXTRA_STARTING_PATH = "io.github.bctnry.tsubasareader.extra.STARTING_PATH";
    public static String EXTRA_FILE_NAME = "io.github.bctnry.tsubasareader.extra.FILE_NAME";
    public static String EXTRA_OBJECT_TYPE = "io.github.bctnry.tsubasareader.extra.OBJECT_TYPE";

    private String action;
    private String objectType;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private File currentPosition;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        final FileActivity self = this;
        action = getIntent().getAction();
        objectType = getIntent().getStringExtra(EXTRA_OBJECT_TYPE);
        sharedPreferences = getSharedPreferences(Constants.feedListFileName, MODE_PRIVATE);
        currentPosition = new File(getIntent().getStringExtra(EXTRA_STARTING_PATH));

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout_file);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView_file);

        setSupportActionBar((Toolbar)findViewById(R.id.toolbar_file));
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(currentPosition.getName());

        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        self.fileAdapter.update(self.currentPosition);
                        self.fileAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }
        );

        fileAdapter = new FileAdapter(this.currentPosition);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(fileAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        if(ACTION_OPEN_FILE.contentEquals(getIntent().getAction())) {
            recyclerView.addOnItemTouchListener(new FeedSourceTouchListener(
                    this.getBaseContext(),
                    recyclerView,
                    new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            File chosenFile = fileAdapter.getItemByPosition(position);
                            if(chosenFile.isDirectory()) {
                                currentPosition = chosenFile;
                                fileAdapter.update(currentPosition);
                                fileAdapter.notifyDataSetChanged();
                            } else {
                                loadFile(chosenFile);
                                finish();
                            }
                        }
                        @Override
                        public void onLongClick(View view, int position) {
                        }
                    }
            ));
        } else if(ACTION_SAVE_FILE.contentEquals(getIntent().getAction())) {
            recyclerView.addOnItemTouchListener(new FeedSourceTouchListener(
                    this.getBaseContext(),
                    recyclerView,
                    new ClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            File chosenFile = fileAdapter.getItemByPosition(position);
                            if(chosenFile.isDirectory()) {
                                currentPosition = chosenFile;
                                fileAdapter.update(currentPosition);
                                fileAdapter.notifyDataSetChanged();
                            } else {
                                saveFile(chosenFile);
                                finish();
                            }
                        }
                        @Override
                        public void onLongClick(View view, int position) {
                        }
                    }
            ));
        } else {
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getIntent().getAction().contentEquals(FileActivity.ACTION_SAVE_FILE)) {
            getMenuInflater().inflate(R.menu.file, menu);
        }
        return true;
    }

    private void saveFile(File file) {
        ArrayList<FeedSource> feedSources = Generic.getFeedSourceList(sharedPreferences);
        try {
            Utilities.writeToFile(file, Constants.gson.toJson(feedSources));
            Toast.makeText(this, "Saved.", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to write to file.", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadFile(File file) {
        try {
            String content = Utilities.fileToString(file);
            if(objectType.contentEquals(Constants.FEED_SOURCE)) {
                Type feedSourceArrayListType = new TypeToken<ArrayList<FeedSource>>(){}.getType();
                ArrayList<FeedSource> feedSources = Constants.gson.fromJson(content, feedSourceArrayListType);
                Generic.addFeedSource(sharedPreferences, feedSources);
                Toast.makeText(this, "Loaded.", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to open file.", Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home: {
                String parent = currentPosition.getParent();
                if(parent == null || parent.isEmpty()) {
                    finish();
                } else {
                    currentPosition = currentPosition.getParentFile();
                    getSupportActionBar().setTitle(currentPosition.getName());
                    fileAdapter.update(currentPosition);
                    fileAdapter.notifyDataSetChanged();
                }
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void menuFileSave_onClick(MenuItem item) {
        Calendar date = Calendar.getInstance();
        String filename =
                "feedsourcebackup_"
                + date.get(Calendar.YEAR) + date.get(Calendar.MONTH) + date.get(Calendar.DAY_OF_MONTH)
                + date.get(Calendar.HOUR_OF_DAY) + date.get(Calendar.MINUTE) + date.get(Calendar.SECOND)
                + ".json";
        Toast.makeText(this, "Saved to " + filename + ".", Toast.LENGTH_SHORT).show();
        saveFile(new File(currentPosition, filename));
    }
}
