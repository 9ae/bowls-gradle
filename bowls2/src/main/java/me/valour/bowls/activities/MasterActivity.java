package me.valour.bowls.activities;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.getbase.floatingactionbutton.FloatingActionsMenu;

import me.valour.bowls.R;
import me.valour.bowls.fragments.BillFragment;
import me.valour.bowls.fragments.TableFragment;


public class MasterActivity extends Activity {

    private FragmentManager fm;
    private BillFragment billFragment;
    private TableFragment tableFragment;
    private FloatingActionsMenu addActionMenu;

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

    }

    class AddBowlClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            tableFragment.addBowl();
            addActionMenu.collapse();
        }
    }

    class AddItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {

        }
    }

}
