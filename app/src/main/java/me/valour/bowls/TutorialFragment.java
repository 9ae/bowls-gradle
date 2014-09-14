package me.valour.bowls;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TutorialFragment extends Fragment implements  BowlsGroupMockery.BowlsGroupAgent {

	private TutorialCloseAgent agent;

    private TextView instructions;
    private BowlsGroupMockery mock;

    private int state = 0;
	
	public TutorialFragment() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_tutorial, container, false);

		instructions = (TextView) view.findViewById(R.id.instructional_text);
        mock = (BowlsGroupMockery) view.findViewById(R.id.mock_table);
        mock.attachBowlAgents(this);

		return view;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try{
			agent = (TutorialCloseAgent)activity;
		} catch (ClassCastException e){
			throw new ClassCastException(activity.toString()+" must implement OnNewItemAddedListener");
		}
	}

    @Override
    public void addBowl() {
        if(state==0){
            instructions.setText(R.string.tutorial_instruction2);
            state = 1;
        }
    }

    @Override
    public void removeBowl() {
        if(state==2){
            return;
        }
        state = 2;
        mock.setVisibility(View.INVISIBLE);
        instructions.setText(R.string.tutorial_instruction3);
        Handler wait = new Handler();
        wait.postDelayed(new Runnable(){
            @Override
            public void run(){
                agent.closeTutorialFragment();
            }
        }, 1500);
    }

    public interface TutorialCloseAgent{
		public void closeTutorialFragment();
	}

}
