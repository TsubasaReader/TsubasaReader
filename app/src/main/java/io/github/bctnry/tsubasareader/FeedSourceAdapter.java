package io.github.bctnry.tsubasareader;


import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.github.bctnry.tsubasareader.DAL.Generic;
import io.github.bctnry.tsubasareader.Model.FeedSource;


import java.util.List;

public class FeedSourceAdapter extends RecyclerView.Adapter {
    private List<FeedSource> feedSourceList;
    public FeedSourceAdapter(List<FeedSource> feedSourceList) {
        this.feedSourceList = feedSourceList;
    }
    public void update(SharedPreferences sharedPreferences) {
        this.feedSourceList = Generic.getFeedSourceList(sharedPreferences);
    }
    public class FeedSourceViewHolder extends RecyclerView.ViewHolder {
        public TextView tvFeedName, tvFeedURL;
        public FeedSourceViewHolder(View itemView) {
            super(itemView);
            this.tvFeedName = (TextView)itemView.findViewById(R.id.tvFeedName_feedSource);
            this.tvFeedURL = (TextView)itemView.findViewById(R.id.tvFeedURL_feedSource);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feedsource, parent, false);
        return new FeedSourceAdapter.FeedSourceViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        FeedSource feedSource = feedSourceList.get(position);
        FeedSourceAdapter.FeedSourceViewHolder feedViewHolder = (FeedSourceAdapter.FeedSourceViewHolder) holder;
        feedViewHolder.tvFeedURL.setText(feedSource.getSourceURL());
        feedViewHolder.tvFeedName.setText(feedSource.getSourceName());
    }

    @Override
    public int getItemCount() {
        return this.feedSourceList.size();
    }
}
