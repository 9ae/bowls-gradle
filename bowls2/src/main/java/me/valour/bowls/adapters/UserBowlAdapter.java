package me.valour.bowls.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.Collections;
import java.util.List;

import me.valour.bowls.models.User;
import me.valour.bowls.services.Kitchen;
import me.valour.bowls.views.BowlView;

public class UserBowlAdapter extends BaseAdapter {

    private List<User> users = Collections.emptyList();
    private Context context;

    public int bowlRadius = 0;

    public UserBowlAdapter(Context context, List<User> users){
        this.context = context;
         this.users = users;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public User getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BowlView bowl;
        if (convertView==null){
            bowl = new BowlView(context);
            bowl.setRadius(bowlRadius);
            bowl.setColors(Kitchen.assignColor(position + 1));
            bowl.setId(position);
            bowl.setUser(users.get(position));
        } else {
            bowl = (BowlView) convertView;
        }

        return bowl;
    }

    public void updateUsers(List<User> users){
        this.users = users;
        notifyDataSetChanged();
    }
}
