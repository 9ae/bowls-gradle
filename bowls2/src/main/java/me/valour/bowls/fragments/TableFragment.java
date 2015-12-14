package me.valour.bowls.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.valour.bowls.R;
import me.valour.bowls.views.BowlsGroup;

/**
 * Created by alice on 12/7/15.
 */
public class TableFragment extends Fragment {

    BowlsGroup bowlsGroup;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);

        bowlsGroup = (BowlsGroup) view.findViewById(R.id.bowlsGroup);

        return view;
    }

    public void addBowl() {
        bowlsGroup.addBowl();

    }
}