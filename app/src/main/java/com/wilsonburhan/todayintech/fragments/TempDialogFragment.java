package com.wilsonburhan.todayintech.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wilsonburhan.todayintech.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Wilson on 10/4/2014.
 */
public class TempDialogFragment extends DialogFragment implements View.OnClickListener{

    @InjectView(R.id.temp_dialog_text) TextView mDialogText;
    @InjectView(R.id.temp_ok) View mOkButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.temp_refresh_fragment, container,false);
        ButterKnife.inject(this, view);
        mDialogText.setMovementMethod(new ScrollingMovementMethod());
        mOkButton.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle("Warning");

        return view;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
