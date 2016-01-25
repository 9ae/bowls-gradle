package me.valour.bowls.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import me.valour.bowls.R;
import me.valour.bowls.activities.MasterActivity;
import me.valour.bowls.adapters.UserBowlAdapter;
import me.valour.bowls.models.User;
import me.valour.bowls.services.Kitchen;
import me.valour.bowls.views.BowlsGroup;

/**
 * Created by alice on 12/7/15.
 */
public class TableFragment extends Fragment {

    BowlsGroup bowlsGroup;
    UserBowlAdapter usersAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);

        bowlsGroup = (BowlsGroup) view.findViewById(R.id.bowlsGroup);
        bowlsGroup.setActionsAgent(getActivity());
        return view;
    }

    public void initUserAdapter(){
        MasterActivity master = (MasterActivity) this.getActivity();
        usersAdapter = new UserBowlAdapter(bowlsGroup.getContext(), master.users);
        bowlsGroup.setAdapter(usersAdapter);
    }

    public void updateUserAdapter() {
        MasterActivity master = (MasterActivity) this.getActivity();
        usersAdapter.updateUsers(master.users);
        bowlsGroup.requestLayout();
    }

}