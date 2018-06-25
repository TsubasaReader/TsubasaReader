package io.github.bctnry.tsubasareader.DAL;

import android.content.SharedPreferences;
import com.rometools.rome.io.FeedException;
import io.github.bctnry.tsubasareader.Constants;
import io.github.bctnry.tsubasareader.FeedSinglActivity;
import io.github.bctnry.tsubasareader.Model.Feed;
import io.github.bctnry.tsubasareader.Model.FeedSource;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.SocketHandler;


public class Generic {

    public static ArrayList<FeedSource> getFeedSourceList(SharedPreferences sharedPreferences) {
        ArrayList<FeedSource> feedSourceList = new ArrayList<>();
        int feedSourceCount = sharedPreferences.getInt(Constants.feedListCountFieldName, 0);
        for(int i = 0; i < feedSourceCount; i++) {
            String thisSourceJson = sharedPreferences.getString(
                    Constants.feedListFeedPrefix + String.valueOf(i),
                    ""
            );
            FeedSource thisSource = Constants.gson.fromJson(
                    thisSourceJson,
                    FeedSource.class
            );
            if (thisSource != null) {
                feedSourceList.add(thisSource);
            }
        }
        return feedSourceList;
    }
    public static FeedSource getFeedSourceByPosition(SharedPreferences sharedPreferences, int position) {
        return Generic.getFeedSourceList(sharedPreferences).get(position);
    }


    public static ArrayList<Feed> getFavouriteFeedList(SharedPreferences sharedPreference) {
        ArrayList<Feed> result = new ArrayList<>();
        int count = sharedPreference.getInt(Constants.feedListCountFieldName    ,0 );
        for(int i = 0; i < count; i++) {
            Feed thisFeed =
                    Constants.gson.fromJson(
                            sharedPreference.getString(
                                    Constants.favouriteFeedFieldPrefix + String.valueOf(i),
                                    ""),
                            Feed.class
                    );
            if(thisFeed != null) {
                thisFeed.setMultiPurposeID(i);
                result.add(thisFeed);
            }
        }
        return result;
    }

    public static void removeFavFeed(SharedPreferences sharedPreferences, int multiPurposeID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        ArrayList<Feed> feedArrayList = getFavouriteFeedList(sharedPreferences);
        for(Feed feed : feedArrayList) {
            if(feed.getMultiPurposeID() == multiPurposeID) {
                editor.remove(
                        Constants.favouriteFeedFieldPrefix + String.valueOf(multiPurposeID)
                );
                break;
            }
        }
        editor.apply();
    }

    public static void addFeedSource(SharedPreferences sharedPreferences, ArrayList<FeedSource> feedSources) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentCount = sharedPreferences.getInt(Constants.feedListCountFieldName, 0);
        for(FeedSource feedSource : feedSources) {
            editor.putString(
                    Constants.feedListFeedPrefix + String.valueOf(currentCount),
                    Constants.gson.toJson(feedSource)
            );
            currentCount++;
        }
        editor.putInt(
                Constants.feedListCountFieldName,
                currentCount
        );
        editor.apply();
    }
    public static void clearFeedSource(SharedPreferences sharedPreferences) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int currentCount = sharedPreferences.getInt(Constants.feedListCountFieldName, 0);
        for(int i = 0; i < currentCount; i++) {
            editor.remove(Constants.feedListFeedPrefix + String.valueOf(currentCount));
        }
        editor.putInt(Constants.feedListCountFieldName,0);
        editor.apply();
    }
}
