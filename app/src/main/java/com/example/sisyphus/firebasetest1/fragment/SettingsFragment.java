package com.example.sisyphus.firebasetest1.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sisyphus.firebasetest1.R;

public class SettingsFragment extends Fragment {


    //set the user's profile
    public SettingsFragment() {
        // Required empty public constructor
    }

    private static final String KEY_TITLE = "Edit your profile here...";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // don't look at this layout it's just a listView to show how to handle the keyboard
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        TextView textView = (TextView) view.findViewById(R.id.title);

        textView.setText(KEY_TITLE);

        return view;
    }
}
