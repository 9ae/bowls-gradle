package me.valour.bowls.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;

import me.valour.bowls.R;
import me.valour.bowls.adapters.UserBowlAdapter;
import me.valour.bowls.models.User;
import me.valour.bowls.services.Kitchen;
import me.valour.bowls.views.BowlsGroup;

/**
 * Created by alice on 12/7/15.
 */
public class TableFragment extends Fragment {

    BowlsGroup bowlsGroup;
    LinkedList<User> users;
    UserBowlAdapter usersAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        users = new LinkedList<User>();
        for (int i = 0; i < Kitchen.minBowls; i++) {
            users.add(new User());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_table, container, false);

        bowlsGroup = (BowlsGroup) view.findViewById(R.id.bowlsGroup);
        usersAdapter = new UserBowlAdapter(bowlsGroup.getContext(), users);
        bowlsGroup.setAdapter(usersAdapter);

        return view;
    }

    public void addBowl() {
        users.add(new User());
        usersAdapter.updateUsers(users);
        bowlsGroup.requestLayout();
    }
}