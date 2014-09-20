package com.wilsonburhan.todayintech.provider;

import com.wilsonburhan.todayintech.base.BaseLookup;
import com.wilsonburhan.todayintech.base.BaseProvider;
import com.wilsonburhan.todayintech.table.TodayInTechTable;

/**
 * Created by Wilson on 9/19/2014.
 */
public class TodayInTechProvider extends BaseProvider {

    @Override
    protected String getDatabaseName() {
        return "todayintech.db";
    }

    @Override
    protected int getDatabaseVersion() {
        return 1;
    }

    @Override
    protected void addTables(BaseLookup locator) {
        locator.putTable(new TodayInTechTable());
    }
}
