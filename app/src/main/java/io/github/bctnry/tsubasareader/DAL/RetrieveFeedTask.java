package io.github.bctnry.tsubasareader.DAL;

import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.bctnry.tsubasareader.Constants;
import io.github.bctnry.tsubasareader.FeedDisplayingActivity;
import io.github.bctnry.tsubasareader.MainActivity;
import io.github.bctnry.tsubasareader.Model.Feed;
import io.github.bctnry.tsubasareader.Model.FeedSource;
import io.github.bctnry.tsubasareader.Model.Utilities;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static io.github.bctnry.tsubasareader.Constants.FEED_SOURCE_EXTRA;


public class RetrieveFeedTask extends AsyncTask<FeedSource, Void, ArrayList<Feed>> {

    private FeedSource feedSource;
    private FeedDisplayingActivity context;

    public RetrieveFeedTask(FeedDisplayingActivity context) {
        this.context = context;
    }

    @Override
    protected ArrayList<Feed> doInBackground(FeedSource... feedSources) {
        FeedSource feedSource = feedSources[0];
        this.feedSource = feedSource;
        URL url = null;
        try {
            url = new URL(feedSource.getSourceURL());
            SyndFeedInput input = new SyndFeedInput();
            SyndFeed feed = input.build(new XmlReader(url));
            return Utilities.toFeedList(feed, feedSource);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.e("MalformedURLException", e.getMessage());
        } catch (FeedException e) {
            e.printStackTrace();
            Log.e("FeedException", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("IOException", e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<Feed> feedArrayList) {
        super.onPostExecute(feedArrayList);
        context.retrieveFeedNotify(this.feedSource, feedArrayList);
    }
}
