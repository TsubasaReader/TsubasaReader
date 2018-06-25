package io.github.bctnry.tsubasareader.Model;

import android.os.Parcel;
import android.os.Parcelable;
import io.github.bctnry.tsubasareader.Constants;

import java.io.Serializable;
import java.util.Date;

public class Feed implements Parcelable {
    private String title;
    private String link;
    private FeedSource source;
    private Date date;
    private String content;
    private int multiPurposeID;

    protected Feed(Parcel in) {
        title = in.readString();
        content = in.readString();
    }

    public void setMultiPurposeID(int id) {
        this.multiPurposeID = id;
    }
    public int getMultiPurposeID() {
        return this.multiPurposeID;
    }

    public static final Creator<Feed> CREATOR = new Creator<Feed>() {
        @Override
        public Feed createFromParcel(Parcel in) {
            String[] strings = new String[5];
            in.readStringArray(strings);
            return new Feed(
                    strings[0],
                    strings[1],
                    Constants.gson.fromJson(strings[2], FeedSource.class),
                    new Date(strings[3]),
                    strings[4]
            );
        }

        @Override
        public Feed[] newArray(int size) {
            return new Feed[size];
        }
    };

    public FeedSource getSource() {
        return source;
    }

    public Date getDate() {
        return date;
    }

    public String getLink() { return link; }

    public String getContent() {
        return this.content;
    }

    public String getTitle() {
        return title;
    }
    public Feed(String title, String link, FeedSource source, Date date, String content) {
        this.title = title;
        this.link = link;
        this.source = source;
        this.date = date;
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{this.title, this.link, Constants.gson.toJson(this.source), this.date.toString(), this.content});
    }

 }
