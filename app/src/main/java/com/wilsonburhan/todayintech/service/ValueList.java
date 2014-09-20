package com.wilsonburhan.todayintech.service;

import android.content.ContentValues;

import java.util.ArrayList;

/**
 * Created by Wilson on 9/20/2014.
 */
public class ValueList {
    public ContentValues currentRow = new ContentValues();
    public ArrayList<ContentValues> rows = new ArrayList<ContentValues>();

    public ContentValues[] getRows() {
        return rows.toArray(new ContentValues[rows.size()]);
    }
}
