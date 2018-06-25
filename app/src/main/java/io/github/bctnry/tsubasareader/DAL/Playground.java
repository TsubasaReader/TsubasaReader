package io.github.bctnry.tsubasareader.DAL;


import com.rometools.rome.feed.synd.SyndContent;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import io.github.bctnry.tsubasareader.Constants;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Playground {
    public static void main(String[] args) throws IOException, FeedException {
        SyndFeedInput syndFeedInput = new SyndFeedInput();
        SyndFeed syndFeed = syndFeedInput.build(new XmlReader(new URL("http://xahlee.info/comp/blog.xml")));
        for(SyndEntry entry : syndFeed.getEntries()) {
            System.out.println(entry.getLink());
        }
    }
}
