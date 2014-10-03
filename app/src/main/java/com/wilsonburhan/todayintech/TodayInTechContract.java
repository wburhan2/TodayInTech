package com.wilsonburhan.todayintech;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Wilson on 9/19/2014.
 */
public class TodayInTechContract {

    public static final Uri RSS_FEED_URI = Uri.parse("content://com.wilsonburhan.todayintech/raw_feed");

    public static final String COLUMN_ID = "_id"; 					// Default column name for the PK in the table
    public static final String COLUMN_TITLE = "title";				// Colmun for the title
    public static final String COLUMN_ARTICLE_ID = "article_id";	// Column for the article ID
    public static final String COLUMN_ARTICLE_LINK = "link";		// Column for the link to the article -- Missing from spec, but in XML feed
    public static final String COLUMN_PUBLISHER = "publisher";      // Columns for the article publisher
    public static final String COLUMN_PUBLISHED_DATE = "published";	// Column for the time the article was published
    public static final String COLUMN_SUMMARY = "summary";      	// Column for the summary of the article
    public static final String COLUMN_UPDATED_DATE = "updated"; 	// Column for the time the article was last updated
    public static final String COLUMN_CONTENT = "content";			// Column for the Content for the article
    public static final String COLUMN_AUTHOR_NAME = "author_name"; 	// Column for the name of the author
    public static final String COLUMN_AUTHOR_URI = "author_uri";	// Column for the URI for the author
    public static final String COLUMN_PICTURE = "content_img";      // Column for the picture URI of the content.
    public static final String COLUMN_FAVORITE = "favorite";		// Column for saving favorite items

    // Default projection for the Table
    public static final String[] DEFAULT_PROJECTION = {
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_ARTICLE_ID,
            COLUMN_ARTICLE_LINK,
            COLUMN_PUBLISHER,
            COLUMN_PUBLISHED_DATE,
            COLUMN_SUMMARY,
            COLUMN_UPDATED_DATE,
            COLUMN_CONTENT,
            COLUMN_AUTHOR_NAME,
            COLUMN_AUTHOR_URI,
            COLUMN_PICTURE,
            COLUMN_FAVORITE
    };

    public static List<Source> SOURCES = new ArrayList<Source>(Arrays.asList(
            new Source("Gizmodo","http://gizmodo.com/rss/vip", true),
            new Source("CNET News","http://www.cnet.com/rss/news", false),
            new Source("The Verge -  All Posts","http://www.theverge.com/rss/index.xml", true)
    ));

    // Intent Actions to perform on search results
    public static final String ACTION_CLEAR = "com.wilsonburhan.todayintech.action.clear";
    public static final String ACTION_CLEAR_ALL = "com.wilsonburhan.todayintech.action.clear";
    public static final String ACTION_GET = "com.wilsonburhan.todayintech.action.get";

    public static class Source {
        public String mTitle;
        public String mUrl;
        public Boolean mActive;

        public Source(String the_title, String the_url, Boolean isActive){
            mTitle = the_title;
            mUrl = the_url;
            mActive = isActive;
        }

        public void setActive(Boolean isActive) {
            mActive = isActive;
        }

        public String getTitle(){
            return mTitle;
        }

        public String getUrl(){
            return mUrl;
        }

        public Boolean isActive(){
            return mActive;
        }
    }
}
