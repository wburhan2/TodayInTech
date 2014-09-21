package com.wilsonburhan.todayintech.fragments;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.wilsonburhan.todayintech.R;
import com.wilsonburhan.todayintech.TodayInTechContract;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;
import butterknife.Optional;

/**
 * Created by Wilson on 9/20/2014.
 */
public class ContentFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    @InjectView(R.id.article_title) TextView mTitle;
    @InjectView(R.id.article_author) TextView mAuthor;
    @InjectView(R.id.article_content) TextView mContent;
    @InjectView(R.id.favorite) CheckBox mFavorite;
    @InjectView(R.id.feed_picture) ImageView mFeedImage;
    @Optional @InjectView(R.id.published_date) TextView mPublishedDate;
    @Optional @InjectView(R.id.edited_date) TextView mEditedDate;
    private String mArticleUrl;


    //public final String CURRENT_ARTICLE_ID = "current_article_id";
    private long mID = -1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Log.e("TAG", "onCreate(ContentFragment)");
            setHasOptionsMenu(true);
            }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
        mID = savedInstanceState.getInt(TodayInTechContract.COLUMN_ID);
        }

        View contentView = inflater.inflate(R.layout.feed_content, container, false);
        ButterKnife.inject(this, contentView);

        getLoaderManager().initLoader(2, null, this);

        return contentView;
    }

    @OnCheckedChanged(R.id.favorite)
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        ContentValues values = new ContentValues();
        values.put(TodayInTechContract.COLUMN_FAVORITE, (isChecked?1:0));
        String where = TodayInTechContract.COLUMN_ID + "=?";
        String[] whereArgs = new String[] { String.valueOf(mID) };
        getActivity().getContentResolver().update(TodayInTechContract.RSS_FEED_URI,  values, where, whereArgs );
    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        if (args != null) {
        showArticle(args.getLong(TodayInTechContract.COLUMN_ID));
        } else if (mID != -1) {
        showArticle(mID);
        }
    }

    public void showArticle(long _id) {
        mID = _id;
        Bundle bundle = new Bundle();
        bundle.putLong(TodayInTechContract.COLUMN_ID, _id);
        getLoaderManager().restartLoader(2, bundle, this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(TodayInTechContract.COLUMN_ID, mID);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            String selection = null;
        String[] selectionArgs = null;
        if (args != null) {
        Long _Id = args.getLong(TodayInTechContract.COLUMN_ID);
        selection = TodayInTechContract.COLUMN_ID + " = ?";
        selectionArgs = new String[] { String.valueOf(_Id) };
        }

        return new CursorLoader(
                getActivity(),
                TodayInTechContract.RSS_FEED_URI,
                TodayInTechContract.DEFAULT_PROJECTION,
                selection,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        cursor.moveToFirst();
        if (cursor.getCount() == 1) {
        String title = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_TITLE));
        String author = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_AUTHOR_NAME));
        String authorUri = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_AUTHOR_URI));
        String content = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_CONTENT));
        String publishedDate = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_PUBLISHED_DATE));
        String editedDate = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_UPDATED_DATE));
        byte[] feedPictureUri = cursor.getBlob(cursor.getColumnIndex(TodayInTechContract.COLUMN_PICTURE));
        mArticleUrl = cursor.getString(cursor.getColumnIndex(TodayInTechContract.COLUMN_ARTICLE_LINK));
        int isFavorite = cursor.getInt(cursor.getColumnIndex(TodayInTechContract.COLUMN_FAVORITE));

        String authorAndUri = "<a href=\"" + authorUri + "\">" + author + "</a>";
        mTitle.setText(title);

        publishedDate = getActivity().getString(R.string.published) + publishedDate;
        editedDate = getActivity().getString(R.string.edited) + editedDate;


        // TODO: Fix this :
        // For some reason setMovementMethod is not working correctly - it should enable links embedded in HTML to be clickable to launch a website.
        // Commenting it out so we can just have the highlighting.
        //mAuthor.setMovementMethod(LinkMovementMethod.getInstance());
        mAuthor.setLinksClickable(true);
        mAuthor.setText(Html.fromHtml(authorAndUri));


        if (feedPictureUri != null) {
            Bitmap bmp;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inMutable = true;
            bmp = BitmapFactory.decodeByteArray(feedPictureUri, 0, feedPictureUri.length, options);
            mFeedImage.setImageBitmap(bmp);
        }

        // TODO: Fix this :
        // For some reason setMovementMethod is not working correctly - it should enable links embedded in HTML to be clickable to launch a website.
        // Commenting it out so we can just have the highlighting.
        //mContent.setMovementMethod(LinkMovementMethod.getInstance());
        mContent.setLinksClickable(true);
        mContent.setText(Html.fromHtml(content));

       // mPublishedDate.setText(publishedDate);
     //   mEditedDate.setText(editedDate);

        mFavorite.setChecked((isFavorite==1)?true:false);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.content, menu);
        //((TodayInTechActivity)getActivity()).setT
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        case R.id.menu_share:
            final Intent intent = new Intent(Intent.ACTION_SEND);

            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "" + mTitle.getText());
            intent.putExtra(Intent.EXTRA_TEXT,
            Html.fromHtml(getString(R.string.wanted_to_share) + ": "+ "\n<br> <a href=\""  + mArticleUrl + "\">Link to theVerge Post Article</a> <br><br>\n\n" + mContent.getText()));

            startActivity(Intent.createChooser(intent,
                getString(R.string.menu_share)));
            break;
        }
        return super.onOptionsItemSelected(item);
    }


    public long getCurrentId() {
        return mID;
    }
}

