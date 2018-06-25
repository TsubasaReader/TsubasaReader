package io.github.bctnry.tsubasareader;

import io.github.bctnry.tsubasareader.Model.Feed;
import io.github.bctnry.tsubasareader.Model.FeedSource;

import java.util.ArrayList;

public interface FeedDisplayingActivity {
    void retrieveFeedNotify(FeedSource feedSource, ArrayList<Feed> feeds);
}
