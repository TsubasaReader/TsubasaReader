package io.github.bctnry.tsubasareader;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.bctnry.tsubasareader.Model.FeedSource;

import java.lang.reflect.Type;

public class Constants {
    public static final String feedListFileName = "TsubasaFeed";
    public static final String favouriteFeedListFileName = "TsubasaFavourite";
    public static String feedListCountFieldName = "FeedCount";
    public static String feedListFeedPrefix = "feed";
    public static String preferenceFileName = "TsubasaPreference";
    public static String preferenceFeedSizeFieldName = "feed_size";
    public static Gson gson = new Gson();
    public static Type feedType = new TypeToken<FeedSource>(){}.getType();
    public static String feedEditIntentFieldName = "feed";
    public static String feedIntentFieldName = "feed";
    public static String feedFileName = "feed.json";
    public static final String broadcastAction = "io.github.bctnry.tsubasareader.BROADCAST";
    public static final String favouriteFeedFieldPrefix = "feed";

    public static final String FEED_EXTRA = "io.github.bctnry.tsubasareader.extra.FEED";
    public static final String FEED_SOURCE_EXTRA = "io.github.bctnry.tsubasareader.extra.FEED_SOURCE";
    public static final String RETRIEVE_FEED_ACTION = "io.github.bctnry.tsubasareader.action.RETRIEVE_FEED";
    public static final String FEED_RETRIEVE_DONE_NOTIFY_ACTION = "io.github.bctnry.tsubasareader.action.FEED_RETRIEVE_DONE_NOTIFY";

    public static final String FEED_SOURCE = "feedsource";
}
