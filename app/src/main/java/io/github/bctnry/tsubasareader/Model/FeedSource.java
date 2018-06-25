package io.github.bctnry.tsubasareader.Model;



import java.io.Serializable;

public class FeedSource implements Serializable {

    private int id;
    private String sourceURL;
    private String sourceName;

    public int getID() { return id; }
    public String getSourceName() { return sourceName; }
    public String getSourceURL() { return sourceURL; }
    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public FeedSource(int position, String sourceName, String sourceURL) {
        this.id = position;
        this.sourceName = sourceName;
        this.sourceURL = sourceURL;
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof FeedSource) {
            return this.id == ((FeedSource)object).getID();
        } else {
            return false;
        }
    }
}
