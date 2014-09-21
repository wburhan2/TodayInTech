package com.wilsonburhan.todayintech.service;

import android.app.IntentService;
import android.content.Intent;

import com.wilsonburhan.todayintech.TodayInTechContract;

/**
 * Created by Wilson on 9/20/2014.
 */
public class TodayInTechService extends IntentService {

    public TodayInTechService(String name) {
        super(name);
    }

    public TodayInTechService() {
        super("TodayInTechService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        // Clear out all the saved stories, other than the saved ones

        if(action.equals(TodayInTechContract.ACTION_CLEAR)) {
            String where = TodayInTechContract.COLUMN_FAVORITE + "=?";
            String[] args = {"0"};
            //getContentResolver().delete(TodayInTechContract.RSS_FEED_URI, where, args);
            // ** NOTE ** by calling this below, we clear the list view & cause it to show
            // the view for empty list. While it may be better to show the list w/out clearing
            // it for the user, there are other interactions which could hurt the experience.
            // For example, user hits refresh, list is not cleared visually, but they click
            // on a row before the list actually refreshes, at this point there is no data
            // behind the list, and it is being refreshed. So we will show the progress bar by
            // way of clearing out the db, and notifying the changes. There is another side
            // effect of the list having favorites. Those won't be empty. But they are accessed
            // by the PK _ID of the table, so even if the list is refreshing in the background
            // Their favorite lists are still visible while it updates. The only thing probably
            // missing is an indeterminant progress bar.
            //getContentResolver().notifyChange(TodayInTechContract.RSS_FEED_URI, null);
        }
        // Clear out ALL the saved stories
        else if(action.equals(TodayInTechContract.ACTION_CLEAR_ALL)) {
            getContentResolver().delete(TodayInTechContract.RSS_FEED_URI, null, null);
            getContentResolver().notifyChange(TodayInTechContract.RSS_FEED_URI, null);
        }
        // Get the latest stories
        else if(action.equals(TodayInTechContract.ACTION_GET)) {
            TodayInTechGetRss.get(getContentResolver());
            getContentResolver().notifyChange(TodayInTechContract.RSS_FEED_URI, null);
        }
    }
}
