package com.wilsonburhan.todayintech.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.wilsonburhan.todayintech.R;
import com.wilsonburhan.todayintech.TodayInTechContract;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Wilson on 9/27/2014.
 */
public class SourceDialogFragment extends DialogFragment{

    @InjectView(R.id.source_list_view) ListView mListView;
    @InjectView(R.id.save_source) Button mButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.sources_dialog_fragment, null, false);
        ButterKnife.inject(this, view);
        getDialog().getWindow().setTitle("Manage Sources");
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        final MyCustomAdapter myCustomAdapter = new MyCustomAdapter();
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(Map.Entry<String, Boolean> entry : myCustomAdapter.mStrings.entrySet()){
                    Iterator<TodayInTechContract.Source> itr = TodayInTechContract.SOURCES.iterator();
                    while (itr.hasNext()) {
                        TodayInTechContract.Source source = itr.next();
                        if (source.getTitle().equals(entry.getKey()))
                            source.setActive(entry.getValue());
                    }
                }
                getDialog().dismiss();

                //TODO Apply Filter to the feeds
            }
        });
        mListView.setAdapter(myCustomAdapter);
    }

    class MyCustomAdapter extends BaseAdapter {

        @InjectView(R.id.source) TextView mSource;
        @InjectView(R.id.source_active) CheckBox mCheckBox;
        Map<String, Boolean> mStrings;

        public MyCustomAdapter() {
            mStrings = new LinkedHashMap<String, Boolean>();
            for(TodayInTechContract.Source source : TodayInTechContract.SOURCES) {
                mStrings.put(source.getTitle(), source.isActive());
            }
        }

        @Override
        public boolean isEnabled(int position) {
            return false;
        }

        @Override
        public int getCount() {
            return mStrings.size();
        }

        public void clear(){
            mStrings.clear();
        }

        @Override
        public Object getItem(int i) {
            if (i >= mStrings.size() || i < 0)
                return null;
            return mStrings.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View contextView, ViewGroup viewGroup) {
            View view = contextView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater)viewGroup.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.source_list_item, viewGroup, false);
            }
            ButterKnife.inject(this, view);
            final String sourceName = mStrings.keySet().toArray(new String[mStrings.size()])[i];
            mSource.setText(sourceName);
            TodayInTechContract.Source source = TodayInTechContract.SOURCES.get(i);

            mCheckBox.setOnCheckedChangeListener(null);
            mCheckBox.setChecked(source.isActive());
            mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mStrings.put(sourceName,isChecked);
                }
            });
            return view;
        }
    }
}
