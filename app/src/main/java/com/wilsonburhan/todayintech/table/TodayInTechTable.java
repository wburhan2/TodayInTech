package com.wilsonburhan.todayintech.table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.wilsonburhan.todayintech.TodayInTechContract;
import com.wilsonburhan.todayintech.base.BaseTable;

/**
 * Created by Wilson on 9/20/2014.
 */
public class TodayInTechTable extends BaseTable {

    @Override
    public int update(SQLiteDatabase db, Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return super.update(db, uri, values, selection, selectionArgs);
    }

    @Override
    public long insert(SQLiteDatabase db, Uri uri, ContentValues values) {
        String[] shortProjection = new String[] {
                TodayInTechContract.COLUMN_ID,
                TodayInTechContract.COLUMN_ARTICLE_ID,
                TodayInTechContract.COLUMN_FAVORITE
        };

        String selection = TodayInTechContract.COLUMN_ARTICLE_ID + " like ? and "
                + TodayInTechContract.COLUMN_FAVORITE + " = ?";
        String[] selectionArgs = new String[]
                { values.getAsString(TodayInTechContract.COLUMN_ARTICLE_ID),
                        "1" };
        Cursor checkCursor = db.query(getTableName(), shortProjection, selection, selectionArgs, null, null, null);

        if (checkCursor != null) {
            checkCursor.moveToFirst();
            if (checkCursor.getCount() > 0) {
                // This item already exists in the db as a favorite, need to just update it vs inserting it.
                Long _id = checkCursor.getLong(checkCursor.getColumnIndex(TodayInTechContract.COLUMN_ID));
                String updateSelection = TodayInTechContract.COLUMN_ID + "=?";
                String[] updateSelectionArgs = new String[] { String.valueOf(_id) };
                update(db, uri, values, updateSelection, updateSelectionArgs);
                checkCursor.close();
                return _id;
            }
            checkCursor.close();
        }
        return super.insert(db, uri, values);
    }

    private static final String TABLE_NAME = "raw_feed";

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // rss create sql
        String sql = "CREATE TABLE " + TABLE_NAME + " (" +
                TodayInTechContract.COLUMN_ID + " integer primary key autoincrement, " +
                TodayInTechContract.COLUMN_TITLE + " text, " +
                TodayInTechContract.COLUMN_ARTICLE_ID + " text, " +
                TodayInTechContract.COLUMN_ARTICLE_LINK + " text, " +
                TodayInTechContract.COLUMN_PUBLISHED_DATE + " text, " +
                TodayInTechContract.COLUMN_UPDATED_DATE + " text, " +
                TodayInTechContract.COLUMN_AUTHOR_NAME + " text, " +
                TodayInTechContract.COLUMN_AUTHOR_URI + " text, " +
                TodayInTechContract.COLUMN_CONTENT + " text, " +
                TodayInTechContract.COLUMN_FAVORITE + " integer default 0," +
                TodayInTechContract.COLUMN_PICTURE + " blob" +
                ");";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}

