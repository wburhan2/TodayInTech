package com.wilsonburhan.todayintech.adapters;

/**
 * Created by Wilson on 9/17/2014.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wilsonburhan.todayintech.R;
import com.wilsonburhan.todayintech.TodayInTechContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsListAdapter extends CursorAdapter {

    public NewsListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView articleTitle = (TextView) view.getTag(R.id.article_title);
        TextView articleSummary = (TextView) view.getTag(R.id.article_summary);
        ImageView imageView = (ImageView) view.getTag(R.id.feed_menu_pic);
        TextView articlePublisher = (TextView) view.getTag(R.id.article_publisher);
        TextView articleLastUpdate = (TextView) view.getTag(R.id.last_update);

        String summary = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_SUMMARY));

        String publishedDate = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_PUBLISHED_DATE));
        String imageUri = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_PICTURE));
        if (imageUri != null)
            ImageLoader.getInstance().displayImage(imageUri, imageView);
        else
            imageView.setImageDrawable(null);
        articleTitle.setText(cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_TITLE)));
        if (summary == null)
            articleSummary.setText(Html.fromHtml(cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_CONTENT))));
        else
            articleSummary.setText(Html.fromHtml(summary));
        articlePublisher.setText(cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_PUBLISHER)));
        articleLastUpdate.setText(getLastUpdateDate(publishedDate));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.news_feed, null);
        view.setTag(R.id.article_title, view.findViewById(R.id.article_title));
        view.setTag(R.id.article_summary, view.findViewById(R.id.article_summary));
        view.setTag(R.id.feed_menu_pic, view.findViewById(R.id.feed_menu_pic));
        view.setTag(R.id.article_publisher, view.findViewById(R.id.article_publisher));
        view.setTag(R.id.last_update, view.findViewById(R.id.last_update));
        return view;
    }

    private String getLastUpdateDate(String date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzzz");
        Date d1 = null;
        Date d2 = new Date();
        String time = "";
        try {
            d1 = format.parse(date);
            long diff = d2.getTime() - d1.getTime();

            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            StringBuilder sb = new StringBuilder();

            if (diffDays == 1)
                sb.append(Long.toString(diffDays) + " day ");
            else if (diffDays > 1)
                sb.append(Long.toString(diffDays) + " days ");

            else {
                if (diffHours % 24 == 1)
                    sb.append(Long.toString(diffHours) + " hour ");
                else if (diffHours % 24 > 1)
                    sb.append(Long.toString(diffHours) + " hours ");

                else {
                    if (diffMinutes % 60 == 1)
                        sb.append(Long.toString(diffMinutes) + " min ");
                    else if (diffMinutes % 60 > 1)
                        sb.append(Long.toString(diffMinutes) + " mins ");
                }
            }

            time = sb.toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
}
