package com.wilsonburhan.todayintech;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;

import com.wilsonburhan.todayintech.fragments.ContentFragment;
import com.wilsonburhan.todayintech.fragments.NewsList;

import butterknife.InjectView;

public class TodayInTechActivity extends FragmentActivity implements NewsList.OnArticleSelectedListener, NewsList.OnRefreshArticlesListener{

    @InjectView(R.id.fragment_container)FrameLayout mMainContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_area);

        if (findViewById(R.id.fragment_container) != null) {

            if (savedInstanceState != null) {
                return;
            }

            NewsList smallScreenFragement = new NewsList();
            smallScreenFragement.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container, smallScreenFragement)
                    .commit();
        }

        updateStories();
    }

    public void updateStories() {
        startService(new Intent(TodayInTechContract.ACTION_CLEAR));
        startService(new Intent(TodayInTechContract.ACTION_GET));
    }

    @Override
    public void onArticleSelected(long _id) {
        /*
    	 * Get the fragment for the content area for large screens, if it is null, then
    	 * create a new content fragment & swap the fragments in the fragment container.
    	 * If it is large, just udpate the content fragment w/ the correct article information.
    	 */
        ContentFragment contentFragment = (ContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.content_fragment);

        if (contentFragment != null) {
            contentFragment.showArticle(_id);
        } else {
            ContentFragment newContentFragment = new ContentFragment();
            Bundle args = new Bundle();
            args.putLong(TodayInTechContract.COLUMN_ID, _id);
            newContentFragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, newContentFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onSelectDefault(long _id) {
        ContentFragment contentFragment = (ContentFragment)
                getSupportFragmentManager().findFragmentById(R.id.content_fragment);

        if (contentFragment != null) {
            if (contentFragment.getCurrentId() == -1) {
                contentFragment.showArticle(_id);
            }
        }
    }

    @Override
    public void onRefreshArticles() {
        updateStories();
    }
}
