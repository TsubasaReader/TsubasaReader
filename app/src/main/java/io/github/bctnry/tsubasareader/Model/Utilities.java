package io.github.bctnry.tsubasareader.Model;




import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.nio.*;

public class Utilities {
    public static ArrayList<Feed> toFeedList(SyndFeed syndFeed, FeedSource feedSource) {
        ArrayList<Feed> result = new ArrayList<>();
        for(SyndEntry entry : syndFeed.getEntries()) {
            result.add(
                    new Feed(
                            entry.getTitle(),
                            entry.getLink(),
                            feedSource,
                            entry.getUpdatedDate(),
                            entry.getContents().get(0).getValue()
                    )
            );
        }
        return result;
    }
    public static String fileToString(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        FileChannel fc = fileInputStream.getChannel();
        MappedByteBuffer byteBuffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        return Charset.defaultCharset().decode(byteBuffer).toString();
    }
    public static String toFileName(String dir, FeedSource feedSource) {
        return dir + "/feed_" + String.valueOf(feedSource.getID()) + ".json";
    }

    public static void writeToFile(File file, String s) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file.getAbsoluteFile());
        printWriter.print(s);
        printWriter.flush();
        printWriter.close();
    }
}
