package io.github.bctnry.tsubasareader;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.github.bctnry.tsubasareader.Model.Feed;
import io.github.bctnry.tsubasareader.Model.FeedSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter {

    public static class FeedViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitle, tvSource, tvDate;

        public FeedViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView)itemView.findViewById(R.id.tvTitle_itemFeed);
            tvSource = (TextView)itemView.findViewById(R.id.tvSource_itemFeed);
            tvDate = (TextView)itemView.findViewById(R.id.tvTime_itemFeed);
        }
    }

    private List<Feed> feedList;

    public FeedAdapter() { feedList = new ArrayList<>(); }
    public FeedAdapter(List<Feed> feedList) {
        this.feedList = feedList;
        if(feedList == null) this.feedList = new ArrayList<>();
    }

    public void addItem(Feed feed) { this.feedList.add(feed); }
    public void addAll(List<Feed> feedList) {
        for(Feed feed : feedList) {
            this.feedList.add(feed);
        }
    }
    public void setAll(List<Feed> feedList) { this.feedList = feedList; }
    public void sortFeed() {
        Feed[] feedArray = this.feedList.toArray(new Feed[]{});
        Arrays.sort(feedArray, new Comparator<Feed>() {
            @Override
            public int compare(Feed feed, Feed t1) {
                return 1 - feed.getDate().compareTo(t1.getDate());
            }
        });
        this.feedList = Arrays.asList(feedArray);
        this.feedList = new ArrayList<>(this.feedList);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);
        return new FeedAdapter.FeedViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Feed feed = feedList.get(position);
        FeedAdapter.FeedViewHolder feedViewHolder = (FeedAdapter.FeedViewHolder) holder;
        feedViewHolder.tvTitle.setText(feed.getTitle());
        feedViewHolder.tvSource.setText(feed.getSource().getSourceName());
        feedViewHolder.tvDate.setText(feed.getDate().toString());
    }
    @Override
    public int getItemCount() {
        return this.feedList.size();
    }
}
