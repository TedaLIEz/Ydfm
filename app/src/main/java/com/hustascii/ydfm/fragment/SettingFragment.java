package com.hustascii.ydfm.fragment;

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

import com.hustascii.ydfm.R;
import com.hustascii.ydfm.util.Globles;


public class SettingFragment extends Fragment {
    private Toolbar toolbar;
    private TextView statusBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting,null,false);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.removeItem(R.id.action_search);
        menu.removeItem(R.id.action_all);
        menu.removeItem(R.id.action_rank);
    }
}

