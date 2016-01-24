package me.valour.bowls.views;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;

import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;

import me.valour.bowls.R;
import me.valour.bowls.adapters.UserBowlAdapter;
import me.valour.bowls.services.Kitchen;
import me.valour.bowls.models.User;

public class BowlsGroup extends AdapterView<UserBowlAdapter> {

    int mMeasuredWidth;
    int mMeasuredHeight;
    float centerX=0;
    float centerY=0;
    int tableRadius;
    int bowlRadius;
    int bowlDiameter;
    boolean measuredScreen;

    BowlSelectListener selectListener;

    UserBowlAdapter usersAdapter;

    ViewGroup.LayoutParams defaultParams;

    BowlTouchMode bowlTouchMode = BowlTouchMode.NONE;

    private HashSet<BowlView> selected = new HashSet<BowlView>();

    BroadcastActions actions;

    public BowlsGroup(Context context) {
        super(context);
        init();
    }

    public BowlsGroup(Context context, AttributeSet ats, int ds) {
        super(context, ats, ds);
        init();
    }

    public BowlsGroup(Context context, AttributeSet attr) {
        super(context, attr);

        init();
    }

    @Override
    public UserBowlAdapter getAdapter() {
        return usersAdapter;
    }

    @Override
    public void setAdapter(UserBowlAdapter adapter) {
        this.usersAdapter = adapter;
        removeAllViewsInLayout();
        requestLayout();

    }

    @Override
    protected void onLayout (boolean changed, int left, int top, int right, int bottom){
        super.onLayout(changed, left, top, right, bottom);
        measureView();

        int usersCount = usersAdapter.getCount();
        double angleDelta = Math.PI*2.0/(double)usersCount;

        if (getChildCount() != usersCount) {
            for(int j = 0; j<usersCount; j++){
                View convertView = this.findViewById(j);
                BowlView bowl = (BowlView) usersAdapter.getView(j, convertView, this);
                if (convertView==null) {
                    bowl.setId(j);
                    addViewInLayout(bowl, j, defaultParams, true);
                    bowl.measure(bowlDiameter, bowlDiameter);
                    bowl.layout(0, 0, bowlDiameter, bowlDiameter);
                    bowl.setOnTouchListener(selectListener);
                }

                double angle = angleDelta*j;
                double px = Math.sin(angle)*tableRadius;
                double py = Math.cos(angle)*tableRadius;

                if(px<0.0) {
                    px = centerX - Math.abs(px);
                } else {
                    px = centerX + px;
                }
                if(py<0.0) {
                    py = centerY + Math.abs(py);
                } else {
                    py = centerY - py;
                }

                bowl.setAngle(angle);

                bowl.move((float) px, (float) py);
                bowl.bringToFront();

                if(j+1 == usersCount){
                    setSelection(j);
                }
            }
        }

        if (usersCount>2) {
            bowlTouchMode = BowlTouchMode.DELETE;
        } else {
            bowlTouchMode = BowlTouchMode.NONE;
        }
    }

    @Override
    public View getSelectedView() {
        return null;
    }

    @Override
    public void setSelection(int position) {
        BowlView selectedBowl = (BowlView) findViewById(position);
        selected.add(selectedBowl);
    }

    private void init() {
        selectListener = new BowlSelectListener();
//        deleteListener = new DeleteDropListener();
        measuredScreen = false;

        defaultParams = new ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

/*        deleteBox = new FrameLayout(this.getContext());
        deleteBox.setBackgroundResource(android.R.drawable.ic_delete);
        addViewInLayout(deleteBox, 109, defaultParams);
        deleteBox.setVisibility(View.GONE);
        deleteBox.setOnDragListener(deleteListener);

        TextView deleteCaption = new TextView(deleteBox.getContext());
        deleteCaption.setText("Drag here to delete");
        deleteBox.addView(deleteCaption);
*/
    }

    public void measureView(){
        if(measuredScreen){
            return;
        } else {
            mMeasuredWidth = getMeasuredWidth();
            mMeasuredHeight = getMeasuredHeight();
            int cx = mMeasuredWidth / 2;
            int cy = mMeasuredHeight / 2;
            tableRadius = Math.min(cx, cy);
            centerX = (float)cx;
            centerY = (float)cy;

            double q = ((double) tableRadius * 2.0 * Math.PI)
                    / (double) Kitchen.maxBowls;
            bowlDiameter = (int) q;
            bowlRadius = (int) (q / 2.0);
            tableRadius -= bowlRadius;

            measuredScreen = true;

            if(usersAdapter!=null){
                usersAdapter.bowlRadius = bowlRadius;
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measuredWidth = measure(widthMeasureSpec);
        int measuredHeight = measure(heightMeasureSpec);
        int d = Math.min(measuredWidth, measuredHeight);
        setMeasuredDimension(d, d);

    }

    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.UNSPECIFIED) {
            result = 500;
        } else {
            result = specSize;
        }
        return result;
    }

    public void setActionsAgent(Activity activity){
        actions = (BroadcastActions) activity;
    }

/*
    private BowlView getNewBowl(){
        BowlView bowl = new BowlView(this.getContext());

        if(measuredScreen){
            bowl.setRadius(bowlRadius);
        }
        bowl.move(centerX, centerY-(bowlRadius/2));

        bowl.setOnTouchListener(selectListener);
        bowl.bringToFront();
        return bowl;
    }

    public void addBowl(){
        BowlView bowl = getNewBowl();

        bowls.add(bowl);
    //        agent.addUser(bowl.user);
        bowl.formatText();


        if(bowls.size()>=Kitchen.maxBowls){
            //TODO: flag that no more new bowls can be added
        }
    }

    public void removeBowl(final BowlView bowl){

        bowls.remove(bowl);
        refreshBowls();

        if((bowls.size()+1)==Kitchen.maxBowls){
           //TODO: allow bowl to be added again
        }
    }

    public void refreshBowls() {
        for(BowlView bv: bowls){
            bv.formatText();
            bv.invalidate();
        }
    }

    public List<Integer> getBowlViewIds() {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (BowlView bw : bowls) {
            ids.add(bw.getId());
        }
        return ids;
    }

    public List<User> getBowlUsers() {
        ArrayList<User> users = new ArrayList<User>();
        for (BowlView bw : bowls) {
            users.add(bw.user);
        }
        return users;
    }

    public void bowlsFocus(boolean unfade) {
        for (BowlView bv : bowls) {
            if (unfade) {
                bv.unfade();
            } else {
                bv.fade();
            }
        }
    }


    public void clearSelection(){
        selectListener.selected.clear();
        for(BowlView bv: bowls){
            bv.setSelected(false);
        }
    }

    public void readyBowlSelect(){
        selectReady = true;
        clearSelection();
        bowlsFocus(false);

    }

    public void stopBowlSelect(){
        selectReady = false;
        clearSelection();
        bowlsFocus(true);

    }

    public List<User> getSelectedUsers(){
        return selectListener.selected;
    }

    public void manualSelect(List<User> users){
        selectListener.selected.addAll(users);

        for(User u: users){
            u.view.setSelected(true);
            u.view.unfade();
        }

    }

    public void enableActions(){
        addRemovable = true;
    }

    public void disableActions(){
        selectReady = false;
        addRemovable = false;
    }
*/

    private class BowlSelectListener implements OnTouchListener{

        public BowlSelectListener(){

        }

        @Override
        public boolean onTouch(View v, MotionEvent move) {
            BowlView bv = (BowlView)v;
            int action = move.getAction();
            if(action==MotionEvent.ACTION_DOWN){

                switch (bowlTouchMode){
                    case DELETE:

                        if(bv.isDeleteReady()){
                            //TODO: delete
                        } else {
                            bv.prepareDelete();
                        }
                        break;

                    case ASSIGN_TO_ITEM:
                        //TOODO: assign to item
                        break;

                    default:
                        break;

                }

                return false;
            } else {
                return true;
            }
        }

    }

    enum BowlTouchMode {
        NONE, DELETE, ASSIGN_TO_ITEM;
    }

    public interface BroadcastActions {

        public void deleteUser(User user);

    }
}