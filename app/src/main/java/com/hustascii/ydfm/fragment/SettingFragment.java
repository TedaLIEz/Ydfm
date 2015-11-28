package com.hustascii.ydfm.fragment;

import android.content.Intent;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.support.v7.app.ActionBarActivity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.github.machinarius.preferencefragment.PreferenceFragment;
import com.hustascii.ydfm.R;
import com.hustascii.ydfm.activity.SettingsActivity;
import com.hustascii.ydfm.util.Globles;


public class SettingFragment extends PreferenceFragment {
    private Toolbar toolbar;
    private TextView statusBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        addPreferencesFromResource(R.xml.pref_setting);

    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        String title = preference.getTitle().toString();

        if (null == title || title.isEmpty()) {
            return false;
        }

        if (title.equals(R.string.pref_about_us)) {


        } else if (title.equals(R.string.pref_clear_mem)) {


        } else if (title.equals(R.string.pref_fallback)) {
//            Intent intent = new Intent(getActivity())
        }


        return true;
    }

    //    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        View view=inflater.inflate(R.layout.fragment_setting,null,false);
////        Intent i = new Intent(getActivity(), SettingsActivity.class);
////        startActivity(i);
//        return view;
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_all);
        menu.removeItem(R.id.action_rank);
    }
}

