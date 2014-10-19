package com.wilsonburhan.todayintech.fragments;

/**
 * Created by Wilson on 9/17/2014.
 */

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.twotoasters.jazzylistview.JazzyListView;
import com.wilsonburhan.todayintech.R;
import com.wilsonburhan.todayintech.TodayInTechContract;
import com.wilsonburhan.todayintech.adapters.NewsListAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NewsList extends Fragment implements LoaderCallbacks<Cursor>, OnItemClickListener {

    OnArticleSelectedListener mSelectedArticleCallback;
    OnRefreshArticlesListener mRefreshArticlesListener;
    private NewsListAdapter mAdapter;
    @InjectView(R.id.news_list) JazzyListView mListView;
    @InjectView(R.id.top_button) TextView mTopButton;
    @InjectView(R.id.fragment_container) SwipeRefreshLayout mSwipeRefreshLayout;

    public interface OnArticleSelectedListener {
        public void onArticleSelected(long _id);
        public void onSelectDefault(long _id);
    }

    public interface OnRefreshArticlesListener {
        public void onRefreshArticles();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAdapter = new NewsListAdapter(getActivity(), null, true);
        getLoaderManager().initLoader(0, null, this);
        getActivity().getContentResolver().registerContentObserver(TodayInTechContract.RSS_FEED_URI, true, contentObserver);
    }

/*
 * this contentObserver looks for the data in the database to update, so when it first loads, it will show data in the content
 * area on the large screen devices.
 */
private ContentObserver contentObserver = new ContentObserver(null) {
    /* (non-Javadoc)
     * @see android.database.ContentObserver#onChange(boolean)
     */
    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor cursor = mAdapter.getCursor();
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                mSelectedArticleCallback.onSelectDefault(cursor.getLong(cursor.getColumnIndex(TodayInTechContract.COLUMN_ID)));
            }
        }
    }
};

    /* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.news_list, container, false);
        ButterKnife.inject(this, contentView);

        mListView.setAdapter(mAdapter);
        mListView.setEmptyView(contentView.findViewById(R.id.empty_view));
        mListView.setOnItemClickListener(this);

        mTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.smoothScrollToPosition(0);
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {
                        mRefreshArticlesListener.onRefreshArticles();
                        restart();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 3500);
            }
        });

        return contentView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mSelectedArticleCallback = (OnArticleSelectedListener) activity;
            mRefreshArticlesListener = (OnRefreshArticlesListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " you forgot to implement an interface");
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mSelectedArticleCallback.onArticleSelected(id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // TODO this is just a marking to note that this is the place for sorting the queries.
        return new CursorLoader(
                getActivity(),
                TodayInTechContract.RSS_FEED_URI,
                TodayInTechContract.DEFAULT_PROJECTION,
                TodayInTechContract.COLUMN_PUBLISHER + " in (" + getActiveFeedsTitle() + ")",
                null,
                TodayInTechContract.COLUMN_PUBLISHED_DATE + " desc limit 40");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor != null) {
            cursor.setNotificationUri(getActivity().getContentResolver(), TodayInTechContract.RSS_FEED_URI);
            mAdapter.swapCursor(cursor);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    mSelectedArticleCallback.onSelectDefault(cursor.getLong(cursor.getColumnIndex(TodayInTechContract.COLUMN_ID)));
                }
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onResume() {
        super.onResume();
        restart();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (menu.size() == 0) {
            inflater.inflate(R.menu.news, menu);
        }
        getActivity().setTitle(getResources().getString(R.string.app_name));
    }

    private void restart(){
        getLoaderManager().restartLoader(0, null, this);
    }

    private String getActiveFeedsTitle() {
        List<String> urls = new ArrayList<String>();

        SharedPreferences settings = getActivity().getSharedPreferences(TodayInTechContract.SOURCE_PREFERENCE, Context.MODE_PRIVATE);
        String value = settings.getString(getResources().getString(R.string.list) + 0, null);

        final List<TodayInTechContract.Source> sourceList;
        if (value == null)
            sourceList = TodayInTechContract.SOURCES;
        else {
            int size = TodayInTechContract.SOURCES.size();
            sourceList = new ArrayList<TodayInTechContract.Source>();
            for (int i = 0; i < size; i++) {
                Gson gson = new Gson();
                String json = settings.getString(getResources().getString(R.string.list)+i, "");
                TodayInTechContract.Source s = gson.fromJson(json, TodayInTechContract.Source.class);
                sourceList.add(s);
            }
        }

        for(TodayInTechContract.Source source : sourceList){
            if (source.isActive())
                urls.add(source.getTitle());
        }
        StringBuilder sb = new StringBuilder();
        int count = 1;
        for (String url : urls){
            sb.append("'");
            sb.append(url);
            sb.append("'");
            if (count < urls.size()) {
                sb.append(",");
                count++;
            }
        }
        return sb.toString();
    }
}
