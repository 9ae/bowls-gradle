package me.valour.bowls.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import java.util.LinkedList;

import me.valour.bowls.R;
import me.valour.bowls.fragments.BillFragment;
import me.valour.bowls.fragments.TableFragment;
import me.valour.bowls.models.User;
import me.valour.bowls.services.Kitchen;
import me.valour.bowls.views.BowlsGroup;


public class MasterActivity extends Activity implements BowlsGroup.BroadcastActions{

    private FragmentManager fm;
    private BillFragment billFragment;
    private TableFragment tableFragment;
    private FloatingActionsMenu addActionMenu;

    public LinkedList<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);

        fm = getFragmentManager();

        billFragment = (BillFragment) fm.findFragmentById(R.id.bills_fragment);
        tableFragment = (TableFragment) fm.findFragmentById(R.id.table_fragment);

        findViewById(R.id.add_bowl).setOnClickListener(new AddBowlClickListener());
        findViewById(R.id.add_item).setOnClickListener(new AddItemClickListener());

        addActionMenu = (FloatingActionsMenu) findViewById(R.id.fab_add);

        users = new LinkedList<User>();
        for (int i = 0; i < Kitchen.minBowls; i++) {
            users.add(new User());
        }

        tableFragment.initUserAdapter();

    }

    @Override
    public void deleteUser(User user) {
        Log.i("vars", "MasterActivity.deleteUser");
        users.remove(user);
        tableFragment.updateUserAdapter();
    }

    class AddBowlClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            users.add(new User());
            tableFragment.updateUserAdapter();
            addActionMenu.collapse();
        }
    }

    class AddItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }

}
