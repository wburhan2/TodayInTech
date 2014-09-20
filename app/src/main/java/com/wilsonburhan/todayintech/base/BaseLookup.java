package com.wilsonburhan.todayintech.base;

import android.net.Uri;

import java.util.Hashtable;
import java.util.Iterator;

/**
 * Created by Wilson on 9/19/2014.
 */
public class BaseLookup implements Iterable<BaseTable>{

    private Hashtable<String, BaseTable> tables = new Hashtable<String, BaseTable>();

    public void putTable(BaseTable table) {
        tables.put(table.getTableName(), table);
    }

    public BaseTable locate(String tableName) {
        return tables.get(tableName);
    }

    public BaseTable locate(Uri uri){
        String tableName = uri.getPathSegments().get(0);
        return locate(tableName);
    }

    @Override
    public Iterator<BaseTable> iterator() {
        return tables.values().iterator();
    }
}
