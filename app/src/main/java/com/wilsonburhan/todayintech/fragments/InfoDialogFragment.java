package com.wilsonburhan.todayintech.fragments;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wilsonburhan.todayintech.R;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by Wilson on 9/26/2014.
 */
public class InfoDialogFragment extends DialogFragment implements View.OnClickListener{

    @InjectView(R.id.dialog_text) TextView mDialogText;
    @InjectView(R.id.ok) View mOkButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_dialog_fragment, container,false);
        ButterKnife.inject(this, view);
        mDialogText.setMovementMethod(new ScrollingMovementMethod());
        mOkButton.setOnClickListener(this);
        Dialog dialog = getDialog();
        dialog.setTitle("Info");

        return view;
    }

    @Override
    public void onClick(View view) {
        dismiss();
    }
}
