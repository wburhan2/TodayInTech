package adapters;

/**
 * Created by Wilson on 9/17/2014.
 */

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.todayintech.wilsonburhan.com.todayintech.R;

public class NewsListAdapter extends CursorAdapter {

    public NewsListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView articleTitle = (TextView) view.getTag(R.id.article_title);
        TextView articleSummary = (TextView) view.getTag(R.id.article_summary);
      //  articleTitle.setText(cursor.getString(cursor.getColumnIndex(HuffingtonPostContract.COLUMN_TITLE)));
       // articleSummary.setText(cursor.getString(cursor.getColumnIndex(HuffingtonPostContract.COLUMN_SUMMARY)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewgroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.news_feed, null);
        view.setTag(R.id.article_title, view.findViewById(R.id.article_title));
        view.setTag(R.id.article_summary, view.findViewById(R.id.article_summary));
        return view;
    }

}
